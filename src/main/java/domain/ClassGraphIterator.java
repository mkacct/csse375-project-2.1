package domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class ClassGraphIterator { // "iterator"
    private final ClassGraph graph;
    private final int vertex;
    private final List<Integer> path;
    public ClassGraphIterator(ClassGraph graph, int v, List<Integer> path) {
        this.graph = graph;
        this.vertex = v;
        this.path = path;
    }

    public int getCurrent() {
        return vertex;
    }

    

    public Set<ClassGraphIterator> followEdge() {
        Set<ClassGraphIterator> set = new HashSet<ClassGraphIterator>();
        int j = 0;
        List<Integer> temp;
        while (j < graph.getNumClasses()) {
            if (graph.getWeight(vertex, j) != 0) {
                temp = new LinkedList<Integer>(path);
                temp.add(j);
                set.add(new ClassGraphIterator(graph, j, temp));
            }
            j++;
        }
        return set;
    }

    /**
     * For paramaters
     * 0: require not edge
     * 1: require edge
     * 2: don't care
     **/
    public Set<ClassGraphIterator> followEdge(int extend, int implement, int hasA, int depends) {
        Set<ClassGraphIterator> set = new HashSet<ClassGraphIterator>();
        int j = 0;
        List<Integer> temp;
        while (j < graph.getNumClasses()) {
            int w = graph.getWeight(vertex, j);
            if (w != 0) {
                if ((extend == 2 || (extend == 1 && ClassGraph.checkExtend(w)) || (extend == 0 && !ClassGraph. checkExtend(w))) && 
                (implement == 2 || (implement == 1 && ClassGraph.checkImplement(w)) || (implement == 0 && !ClassGraph.checkImplement(w))) && 
                (hasA == 2 || (hasA == 1 && ClassGraph.checkHasA(w)) || (hasA == 0 && !ClassGraph.checkHasA(w))) && 
                (depends == 2 || (depends == 1 && ClassGraph.checkDepends(w)) || (depends == 0 && !ClassGraph.checkDepends(w))))
                {
                    temp = new LinkedList<Integer>(path);
                    temp.add(j);
                    set.add(new ClassGraphIterator(graph, j, temp));
                }
            }
            j++;
        }
        return set;
    }

    


    public List<Integer> getPath() {
        return List.copyOf(path);
    }

    public boolean hasCycle() {
        Iterator<Integer> it = path.iterator();
        Set<Integer> seen = new HashSet<Integer>();
        int temp;
        while (it.hasNext()) {
            temp = it.next();
            if (seen.contains(temp)) {
                return true;
            }
            seen.add(temp);
        }
        return false;
    }
}
