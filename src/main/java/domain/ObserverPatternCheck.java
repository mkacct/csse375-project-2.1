package domain;

import java.util.HashSet;
import java.util.Set;

import datasource.Configuration;
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
        ClassData dat2;
        ClassData dat3;
        Set<String> obsClasses;
        boolean patternFound = false;
        for (int i = 0; i < graph.getNumClasses(); i++) {
            ClassGraphIterator it = graph.graphIterator(i);
            dat = graph.getClasses().get(graph.indexToClass(it.getCurrent()));
            checkInterfaces(checkInterface, dat, it, messages);
            checkAbstractClassesNonInterfaces(checkAbstract, dat, it, messages);
            checkConcreteClasses(checkConcrete, dat, it, messages);
        }
        return messages;
    }

    private void checkConcreteClasses(boolean checkConcrete, ClassData dat, ClassGraphIterator it, Set<Message> messages) {
        Set<String> obsClasses;
        ClassData dat2;
        boolean patternFound;
        if (checkConcrete && dat.getClassType() == ClassType.CLASS && !dat.isAbstract()) {
            patternFound = false;
            obsClasses = new HashSet<String>();
            obsClasses.add(graph.indexToClass(it.getCurrent()));
            for (ClassGraphIterator it2 : it.followEdge(2, 2, 1, 2)) { // observer interface/abstract
                dat2 = graph.getClasses().get(graph.indexToClass(it2.getCurrent()));
                if (dat2.isAbstract() || dat2.getClassType() == ClassType.INTERFACE) {
                    for (int j = 0; j < graph.column(it2.getCurrent()).length; j++) {
                        if (j != it2.getCurrent() && ClassGraph.checkHasA(graph.getWeight(j, it.getCurrent()))&& (ClassGraph.checkImplement(graph.getWeight(j, it2.getCurrent())) || ClassGraph.checkExtend(graph.getWeight(j, it2.getCurrent())))) { // concrete observer, has a cs
                            obsClasses.add(graph.indexToClass(it2.getCurrent()));
                            obsClasses.add(graph.indexToClass(j));
                            patternFound = true;
                        }
                    }
                }
            }
            if (patternFound) {
                messages.add(new Message(MessageLevel.INFO, "(Concrete) Observer pattern found", obsClasses));
            }
        }
    }

    private void checkAbstractClassesNonInterfaces(boolean checkAbstract, ClassData dat, ClassGraphIterator it, Set<Message> messages) {
        boolean patternFound;
        ClassData dat2;
        ClassData dat3;
        Set<String> obsClasses;
        boolean checkingForAbstractAndNonInterface = checkAbstract && dat.getClassType() != ClassType.INTERFACE && dat.isAbstract();
        if (!checkingForAbstractAndNonInterface) {
            return;
        }
        patternFound = false;
        obsClasses = new HashSet<String>();
        obsClasses.add(graph.indexToClass(it.getCurrent()));
        patternFound = checkAbstractAndObserverInterface(it, obsClasses, patternFound);
        if (patternFound) {
            messages.add(new Message(MessageLevel.INFO, "(Abstract) Observer pattern found", obsClasses));
        }
    }

    private boolean checkAbstractAndObserverInterface(ClassGraphIterator it, Set<String> obsClasses, boolean patternFound) {
        ClassData dat2;
        ClassData dat3;
        for (ClassGraphIterator it2 : it.followEdge(2, 2, 1, 2)) {
            dat2 = graph.getClasses().get(graph.indexToClass(it2.getCurrent()));
            boolean abstractOrInterface = dat2.isAbstract() || dat2.getClassType() == ClassType.INTERFACE;
            if (abstractOrInterface) {
                patternFound = containsConcreteObservers(it, it2, obsClasses, patternFound);
            }
        }
        return patternFound;
    }

    private boolean checkConcreteSubjects(ClassGraphIterator it, Set<String> obsClasses, boolean patternFound, ClassGraphIterator it2, int j) {
        ClassData dat3;
        for (ClassGraphIterator it3 : graph.graphIterator(j).followEdge(2, 2, 1, 2)) {
            dat3 = graph.getClasses().get(graph.indexToClass(it3.getCurrent()));
            boolean isConcreteSubject = dat3.getClassType() == ClassType.CLASS && !dat3.isAbstract() && ClassGraph.checkExtend(graph.getWeight(it3.getCurrent(), it.getCurrent()));
            if (isConcreteSubject) {
                obsClasses.add(graph.indexToClass(it2.getCurrent()));
                obsClasses.add(graph.indexToClass(j));
                obsClasses.add(graph.indexToClass(it3.getCurrent()));
                patternFound = true;
            }
        }
        return patternFound;
    }

    private void checkInterfaces(boolean checkInterface, ClassData dat, ClassGraphIterator it, Set<Message> messages) {
        boolean patternFound;
        Set<String> obsClasses;
        ClassData dat2;
        ClassData dat3;
        boolean checkingForValidInterfaces = checkInterface && dat.getClassType() == ClassType.INTERFACE;
        if (!checkingForValidInterfaces) {
            return;
        }
        patternFound = false;
        obsClasses = new HashSet<String>();
        obsClasses.add(graph.indexToClass(it.getCurrent()));
        Set<ClassGraphIterator> edges = it.followEdge(2, 2, 2, 1);
        patternFound = checkObserverInterfacesAndAbstracts(it, edges, patternFound, obsClasses);
        if (patternFound) {
            messages.add(new Message(MessageLevel.INFO, "(Interface) Observer pattern found", obsClasses));
        }
    }

    private boolean checkObserverInterfacesAndAbstracts(ClassGraphIterator it, Set<ClassGraphIterator> edges, boolean patternFound, Set<String> obsClasses) {
        ClassData dat2;
        for (ClassGraphIterator it2 : edges) {
            dat2 = graph.getClasses().get(graph.indexToClass(it2.getCurrent()));
            boolean abstractOrInterface = dat2.isAbstract() || dat2.getClassType() == ClassType.INTERFACE;
            if (abstractOrInterface) {
                patternFound = containsConcreteObservers(it, it2, obsClasses, patternFound);
            }
        }
        return patternFound;
    }

    private boolean containsConcreteObservers(ClassGraphIterator it, ClassGraphIterator it2, Set<String> obsClasses, boolean patternFound) {
        for (int j = 0; j < graph.column(it2.getCurrent()).length; j++) {
              if (isConcreteObserver(it2, j)) { // concrete observer
                  patternFound = checkConcreteSubjects(it, it2, obsClasses, patternFound, j);
              }
          }
        return patternFound;
    }

    private boolean isConcreteObserver(ClassGraphIterator it2, int j) {
      return j != it2.getCurrent() && (ClassGraph.checkImplement(graph.getWeight(j, it2.getCurrent())) || ClassGraph.checkExtend(graph.getWeight(j, it2.getCurrent())));
    }

    private boolean checkConcreteSubjects(ClassGraphIterator it, ClassGraphIterator it2, Set<String> obsClasses, boolean patternFound, int j) {
        ClassData dat3;
        for (ClassGraphIterator it3 : graph.graphIterator(j).followEdge(2, 2, 1, 2)) { // possible concrete subjects
            dat3 = graph.getClasses().get(graph.indexToClass(it3.getCurrent()));
            boolean isConcreteSubject = dat3.getClassType() == ClassType.CLASS && !dat3.isAbstract() && ClassGraph.checkImplement(graph.getWeight(it3.getCurrent(), it.getCurrent())) && ClassGraph.checkHasA(graph.getWeight(it3.getCurrent(), it2.getCurrent()));
            if (isConcreteSubject) { // is concrecte subject
                obsClasses.add(graph.indexToClass(it2.getCurrent()));
                obsClasses.add(graph.indexToClass(j));
                obsClasses.add(graph.indexToClass(it3.getCurrent()));
                patternFound = true;
            }
        }
        return patternFound;
    }

}
