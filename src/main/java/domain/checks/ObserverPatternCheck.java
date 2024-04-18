package domain.checks;

import java.util.HashSet;
import java.util.Set;

import datasource.Configuration;
import domain.ClassGraph;
import domain.ClassGraphIterator;
import domain.Message;
import domain.MessageLevel;
import domain.javadata.ClassData;
import domain.javadata.ClassType;

public class ObserverPatternCheck extends GraphCheck {
    private static final String NAME = "observerPattern";

    public ObserverPatternCheck() {
        super(NAME, false);
    }

    @Override
    public Set<Message> gRun(Configuration config) {
        boolean checkInterface = config.getBoolean("obsInterface", true);
        boolean checkAbstract = config.getBoolean("obsAbstract", true);
        boolean checkConcrete = config.getBoolean("obsConcrete", true);
        Set<Message> messages = new HashSet<Message>();
        ClassData dat;
        for (int i = 0; i < graph.getNumClasses(); i++) {
            ClassGraphIterator it = graph.graphIterator(i);
            dat = graph.getClasses().get(graph.indexToClass(it.getCurrent()));
            checkInterfaces(checkInterface, dat, it, messages);
            checkAbstractClassesNonInterfaces(checkAbstract, dat, it, messages);
            checkConcreteClasses(checkConcrete, dat, it, messages);
        }
        return messages;
    }

    private void checkInterfaces(boolean checkInterface, ClassData dat, ClassGraphIterator it, Set<Message> messages) {
        Set<String> obsClasses;
        boolean checkingForValidInterfaces = checkInterface && dat.getClassType() == ClassType.INTERFACE;
        if (!checkingForValidInterfaces) {
            return;
        }
        obsClasses = new HashSet<String>();
        obsClasses.add(graph.indexToClass(it.getCurrent()));
        Set<ClassGraphIterator> edges = it.followEdge(2, 2, 2, 1);
        if (containsObserverInterfaceOrAbstracts(it, edges, obsClasses)) {
            messages.add(new Message(MessageLevel.INFO, "(Interface) Observer pattern found", obsClasses));
        }
    }

    private boolean containsObserverInterfaceOrAbstracts(ClassGraphIterator it, Set<ClassGraphIterator> edges, Set<String> obsClasses) {
        boolean patternFound = false;
        ClassData dat2;
        for (ClassGraphIterator it2 : edges) { // observer interface/abstract
            dat2 = graph.getClasses().get(graph.indexToClass(it2.getCurrent()));
            boolean abstractOrInterface = dat2.isAbstract() || dat2.getClassType() == ClassType.INTERFACE;
            if (!abstractOrInterface) {
                continue;
            }
            patternFound = checkConcreteObservers(it, it2, obsClasses);
        }
        return patternFound;
    }

    private boolean checkConcreteObservers(ClassGraphIterator it, ClassGraphIterator it2, Set<String> obsClasses) {
        boolean patternFound = false;
        for (int j = 0; j < graph.column(it2.getCurrent()).length; j++) {
            boolean isConcreteObserver = j != it2.getCurrent() && (ClassGraph.checkImplement(graph.getWeight(j, it2.getCurrent())) || ClassGraph.checkExtend(graph.getWeight(j, it2.getCurrent())));
            if (!isConcreteObserver) { // concrete observer
                continue;
            }
            patternFound = checkConcreteSubjectsWithInterfaces(it, it2, obsClasses, j);
        }
        return patternFound;
    }

    private boolean checkConcreteSubjectsWithInterfaces(ClassGraphIterator it, ClassGraphIterator it2, Set<String> obsClasses, int j) {
        ClassData dat3;
        boolean patternFound = false;
        for (ClassGraphIterator it3 : graph.graphIterator(j).followEdge(2, 2, 1, 2)) { // possible concrete subjects
            dat3 = graph.getClasses().get(graph.indexToClass(it3.getCurrent()));
            boolean isConcreteSubject = dat3.getClassType() == ClassType.CLASS && !dat3.isAbstract() && ClassGraph.checkImplement(graph.getWeight(it3.getCurrent(), it.getCurrent())) && ClassGraph.checkHasA(graph.getWeight(it3.getCurrent(), it2.getCurrent()));
            if (!isConcreteSubject) { // is concrecte subject
                continue;
            }
            obsClasses.add(graph.indexToClass(it2.getCurrent()));
            obsClasses.add(graph.indexToClass(j));
            obsClasses.add(graph.indexToClass(it3.getCurrent()));
            patternFound = true;
        }
        return patternFound;
    }

    private void checkAbstractClassesNonInterfaces(boolean checkAbstract, ClassData dat, ClassGraphIterator it, Set<Message> messages) {
        Set<String> obsClasses;
        boolean checkingForAbstractNonInterfaces = checkAbstract && dat.getClassType() != ClassType.INTERFACE && dat.isAbstract();
        if (!checkingForAbstractNonInterfaces) {
            return;
        }
        obsClasses = new HashSet<String>();
        obsClasses.add(graph.indexToClass(it.getCurrent()));
        if (containsAbstractNonInterfaces(it, obsClasses)) {
            messages.add(new Message(MessageLevel.INFO, "(Abstract) Observer pattern found", obsClasses));
        }
    }

    private boolean containsAbstractNonInterfaces(ClassGraphIterator it, Set<String> obsClasses) {
        boolean patternFound = false;
        ClassData dat2;
        Set<ClassGraphIterator> edges = it.followEdge(2, 2, 1, 2);
        for (ClassGraphIterator it2 : edges) { // observer interface/abstract
            dat2 = graph.getClasses().get(graph.indexToClass(it2.getCurrent()));
            boolean isAbstractOrInterface = dat2.isAbstract() || dat2.getClassType() == ClassType.INTERFACE;
            if (isAbstractOrInterface) {
                patternFound = checkConcreteObserversWithAbstractNonInterfaces(it, obsClasses, it2);
            }
        }
        return patternFound;
    }

    private boolean checkConcreteObserversWithAbstractNonInterfaces(ClassGraphIterator it, Set<String> obsClasses, ClassGraphIterator it2) {
        boolean patternFound = false;
        for (int j = 0; j < graph.column(it2.getCurrent()).length; j++) {
            boolean isConcreteObserver = j != it2.getCurrent() && (ClassGraph.checkImplement(graph.getWeight(j, it2.getCurrent())) || ClassGraph.checkExtend(graph.getWeight(j, it2.getCurrent())));
            if (isConcreteObserver) { // concrete observer
                patternFound = checkForConcreteSubjects(it, obsClasses, it2, j);
            }
        }
        return patternFound;
    }

    private boolean checkForConcreteSubjects(ClassGraphIterator it, Set<String> obsClasses, ClassGraphIterator it2, int j) {
        boolean patternFound = false;
        ClassData dat3;
        for (ClassGraphIterator it3 : graph.graphIterator(j).followEdge(2, 2, 1, 2)) { // possible concrete subjects
            dat3 = graph.getClasses().get(graph.indexToClass(it3.getCurrent()));
            if (dat3.getClassType() == ClassType.CLASS && !dat3.isAbstract() && ClassGraph.checkExtend(graph.getWeight(it3.getCurrent(), it.getCurrent()))) { // is concrecte subject
                obsClasses.add(graph.indexToClass(it2.getCurrent()));
                obsClasses.add(graph.indexToClass(j));
                obsClasses.add(graph.indexToClass(it3.getCurrent()));
                patternFound = true;
            }
        }
        return patternFound;
    }

    private void checkConcreteClasses(boolean checkConcrete, ClassData dat, ClassGraphIterator it, Set<Message> messages) {
        Set<String> obsClasses;
        boolean checkingConcreteClasses = checkConcrete && dat.getClassType() == ClassType.CLASS && !dat.isAbstract();
        if (!checkingConcreteClasses) {
            return;
        }
        obsClasses = new HashSet<String>();
        obsClasses.add(graph.indexToClass(it.getCurrent()));
        if (containsObserverInterface(it, obsClasses)) {
            messages.add(new Message(MessageLevel.INFO, "(Concrete) Observer pattern found", obsClasses));
        }
    }

    private boolean containsObserverInterface(ClassGraphIterator it, Set<String> obsClasses) {
        boolean patternFound = false;
        ClassData dat2;
        for (ClassGraphIterator it2 : it.followEdge(2, 2, 1, 2)) {
            dat2 = graph.getClasses().get(graph.indexToClass(it2.getCurrent()));
            boolean isAbstractOrInterface = dat2.isAbstract() || dat2.getClassType() == ClassType.INTERFACE;
            if (isAbstractOrInterface) {
                patternFound = containsConcreteImplementers(it, obsClasses, it2, patternFound);
            }
        }
        return patternFound;
    }

    private boolean containsConcreteImplementers(ClassGraphIterator it, Set<String> obsClasses, ClassGraphIterator it2, boolean patternFound) {
        for (int j = 0; j < graph.column(it2.getCurrent()).length; j++) {
            if (isConcreteClass(it, it2, j)) {
                obsClasses.add(graph.indexToClass(it2.getCurrent()));
                obsClasses.add(graph.indexToClass(j));
                patternFound = true;
            }
        }
        return patternFound;
    }

    private boolean isConcreteClass(ClassGraphIterator it, ClassGraphIterator it2, int j) {
        return j != it2.getCurrent() && ClassGraph.checkHasA(graph.getWeight(j, it.getCurrent())) && (ClassGraph.checkImplement(graph.getWeight(j, it2.getCurrent())) || ClassGraph.checkExtend(graph.getWeight(j, it2.getCurrent())));
    }

}
