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
    /**
     * @param obsInterface - boolean - whether to check for interface subject. Defaults to true
     * @param obsAbstract - boolean -whether to check for abstract subject. Defaults to true
     * @param obsConcrete - boolean - whether to check for concrete subject. Defaults to true
     */
    public Set<Message> gRun(Configuration config) {
        boolean checkInterface = config.getBoolean("obsInterface", true);
        boolean checkAbstract = config.getBoolean("obsAbstract", true);
        boolean checkConcrete = config.getBoolean("obsConcrete", true);
        Set<Message> messages = new HashSet<Message>();
        ClassGraphIterator it;
        ClassData dat;
        ClassData dat2;
        ClassData dat3;
        Set<String> obsClasses;
        boolean patternFound = false;
        for (int i = 0; i < graph.getNumClasses(); i++) {
            it = graph.graphIterator(i);
            dat = graph.getClasses().get(graph.indexToClass(it.getCurrent()));
            if (checkInterface && dat.getClassType() == ClassType.INTERFACE) { // subject interface
                patternFound = false;
                obsClasses = new HashSet<String>();
                obsClasses.add(graph.indexToClass(it.getCurrent()));
                for (ClassGraphIterator it2 : it.followEdge(2, 2, 2, 1)) { // observer interface/abstract
                    dat2 = graph.getClasses().get(graph.indexToClass(it2.getCurrent()));
                    if (dat2.isAbstract() || dat2.getClassType() == ClassType.INTERFACE) {
                        for (int j = 0; j < graph.column(it2.getCurrent()).length; j++) {
                            if (j != it2.getCurrent() && (ClassGraph.checkImplement(graph.getWeight(j, it2.getCurrent())) || ClassGraph.checkExtend(graph.getWeight(j, it2.getCurrent())))) { // concrete observer
                                for (ClassGraphIterator it3 : graph.graphIterator(j).followEdge(2, 2, 1, 2)) { // possible concrete subjects
                                    dat3 = graph.getClasses().get(graph.indexToClass(it3.getCurrent()));
                                    if (dat3.getClassType() == ClassType.CLASS && !dat3.isAbstract() && ClassGraph.checkImplement(graph.getWeight(it3.getCurrent(), it.getCurrent())) && ClassGraph.checkHasA(graph.getWeight(it3.getCurrent(), it2.getCurrent()))) { // is concrecte subject
                                        obsClasses.add(graph.indexToClass(it2.getCurrent()));
                                        obsClasses.add(graph.indexToClass(j));
                                        obsClasses.add(graph.indexToClass(it3.getCurrent()));
                                        patternFound = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if (patternFound) {
                    messages.add(new Message(MessageLevel.INFO, "(Interface) Observer pattern found", obsClasses));
                }
            }
            if (checkAbstract && dat.getClassType() != ClassType.INTERFACE && dat.isAbstract()) {
                patternFound = false;
                obsClasses = new HashSet<String>();
                obsClasses.add(graph.indexToClass(it.getCurrent()));
                for (ClassGraphIterator it2 : it.followEdge(2, 2, 1, 2)) { // observer interface/abstract
                    dat2 = graph.getClasses().get(graph.indexToClass(it2.getCurrent()));
                    if (dat2.isAbstract() || dat2.getClassType() == ClassType.INTERFACE) {
                        for (int j = 0; j < graph.column(it2.getCurrent()).length; j++) {
                            if (j != it2.getCurrent() && (ClassGraph.checkImplement(graph.getWeight(j, it2.getCurrent())) || ClassGraph.checkExtend(graph.getWeight(j, it2.getCurrent())))) { // concrete observer
                                for (ClassGraphIterator it3 : graph.graphIterator(j).followEdge(2, 2, 1, 2)) { // possible concrete subjects
                                    dat3 = graph.getClasses().get(graph.indexToClass(it3.getCurrent()));
                                    if (dat3.getClassType() == ClassType.CLASS && !dat3.isAbstract() && ClassGraph.checkExtend(graph.getWeight(it3.getCurrent(), it.getCurrent()))) { // is concrecte subject
                                        obsClasses.add(graph.indexToClass(it2.getCurrent()));
                                        obsClasses.add(graph.indexToClass(j));
                                        obsClasses.add(graph.indexToClass(it3.getCurrent()));
                                        patternFound = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if (patternFound) {
                    messages.add(new Message(MessageLevel.INFO, "(Abstract) Observer pattern found", obsClasses));
                }
            }
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
        return messages;
    }

}
