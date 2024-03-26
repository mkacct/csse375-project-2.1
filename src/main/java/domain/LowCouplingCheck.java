package domain;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import datasource.Configuration;

public class LowCouplingCheck extends GraphCheck {
    private int maxInDegree;
    private int maxOutDegree;
    private String packageName;
    private boolean checkCycles;
    private boolean ignoreSelf;
    private static final String NAME = "lowCoupling";
    private final Set<Message> messages = new HashSet<Message>();
    private final PriorityQueue<IntegerAndDegree> lowestInDegrees = new PriorityQueue<IntegerAndDegree>();

    public LowCouplingCheck() {
        super(NAME);
    }

    @Override
    /**
     * @param coupMaxInDegree - int - Maximum In-Degree for a class. -1 for no max. Defaults to -1
     * @param coupMaxOutDegree - int - Maximum Out-Degree for a class. -1 for no max. Defaults to -1
     * @param coupIgnorePackage - String - Full Package Name to not check (such as presentation). Defaults to null
     * @param coupCycles - boolean - Whether to check for cycles. Defaults to true
     * @param coupIgnoreSelfCycles - boolean - Whether to ignore cycles produced by classes that depend on themselves, such as when they contain themselves as a field. Defaults to true
     */
    public Set<Message> gRun(Configuration config) {
        if (graph.getNumClasses() <= 0) {
            return new HashSet<Message>();
        }
        parseConfig(config);
        checkGraphDegrees();
        return checkCycles();
    }

    private Set<Message> checkCycles() {
        if (!checkCycles) {
            return messages;
        }
        recurseThroughClasses();
        return messages;
    }

    private void recurseThroughClasses() {
        ClassGraphIterator it = graph.graphIterator(lowestInDegrees.poll().index);
        recursion(it, messages, lowestInDegrees, ignoreSelf);
        while (!lowestInDegrees.isEmpty()) {
            recursion(graph.graphIterator(lowestInDegrees.poll().index), messages, lowestInDegrees, ignoreSelf);
        }
    }

    private void checkGraphDegrees() {
        handleNegativeGraphDegrees();
        for (int i = 0; i < graph.getNumClasses(); i++) {
            boolean alreadyInPackage = packageName != null && graph.getClasses().get(graph.indexToClass(i)).getPackageName().equals(packageName);
            if (alreadyInPackage) continue;
            lowestInDegrees.add(new IntegerAndDegree(i, graph.inDegree(i)));
            handleExceededMaxInDegree(i);
            handleExceededMaxOutDegree(i);
        }
    }

    private void handleExceededMaxOutDegree(int i) {
        if (graph.outDegree(i) > maxOutDegree) {
            messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Out Degree exceeds {0}, is {1}", maxOutDegree, graph.outDegree(i)), graph.indexToClass(i)));
        }
    }

    private void handleExceededMaxInDegree(int i) {
        if (graph.inDegree(i) > maxInDegree) {
            messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("In Degree exceeds {0}, is {1}", maxInDegree, graph.inDegree(i)), graph.indexToClass(i)));
        }
    }

    private void handleNegativeGraphDegrees() {
        if (maxInDegree == -1) {
            maxInDegree = Integer.MAX_VALUE;
        }
        if (maxOutDegree == -1) {
            maxOutDegree = Integer.MAX_VALUE;
        }
    }

    private void parseConfig(Configuration config) {
        maxInDegree = config.getInt("coupMaxInDegree", -1);
        maxOutDegree = config.getInt("coupMaxOutDegree", -1);
        packageName = config.getString("coupIgnorePackage", null);
        checkCycles = config.getBoolean("coupCycles", true);
        ignoreSelf = config.getBoolean("coupIgnoreSelfCycles", true);
    }

    @SuppressWarnings("unlikely-arg-type")
    private void recursion(ClassGraphIterator it, Set<Message> messages, PriorityQueue<IntegerAndDegree> pq, boolean ignoreSelf) { // graph traversing
        pq.remove(it.getCurrent());
        if (handleCycle(it, messages, ignoreSelf)) return;
        recurseThroughEdges(it, messages, pq, ignoreSelf);
    }

    private void recurseThroughEdges(ClassGraphIterator it, Set<Message> messages, PriorityQueue<IntegerAndDegree> pq, boolean ignoreSelf) {
        for (ClassGraphIterator o : it.followEdge()) {
            recursion(o, messages, pq, ignoreSelf);
        }
    }

    private boolean handleCycle(ClassGraphIterator it, Set<Message> messages, boolean ignoreSelf) {
        if (it.hasCycle()) {
            List<Integer> pathImm = it.getPath();
            List<Integer> path = new LinkedList<Integer>(pathImm);
            int last = path.get(path.size() - 1);
            while (path.get(0) != last) {
                path.remove(0);
            }
            if (ignoreSelf && path.size() <= 2) { // like node --> node cycles
                return true;
            }
            List<String> classes = new LinkedList<String>();
            for (int i : path) {
                classes.add(graph.indexToClass(i));
            }
            messages.add(new Message(MessageLevel.WARNING, "Cycle detected: " + cyclePrint(classes))); // probably won't duplicate cycles
            return true;
        }
        return false;
    }

    private String cyclePrint(List<String> classes) {
        StringBuilder str = new StringBuilder();
        Iterator<String> it = classes.iterator();
        if (!it.hasNext()) { // shouldnt happen
            return "";
        }
        str.append(it.next());
        while (it.hasNext()) {
            str.append(" --> ");
            str.append(it.next());
        }
        return str.toString();
    }

}