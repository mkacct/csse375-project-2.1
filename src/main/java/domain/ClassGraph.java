package domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import domain.javadata.ClassData;
import domain.javadata.FieldData;
import domain.javadata.FieldInstrData;
import domain.javadata.InstrData;
import domain.javadata.InstrType;
import domain.javadata.LocalVarInstrData;
import domain.javadata.MethodData;
import domain.javadata.MethodInstrData;
import domain.javadata.VariableData;

public class ClassGraph {
    private Map<String, ClassData> stringToClass;
    private Map<String, Integer> classes; 
    private Map<Integer, String> inverse; // inverse of the other map
    private int[][] edges; // weighted
    private int numClasses;
    public ClassGraph(Map<String, ClassData> strToClass) {
        this.stringToClass = strToClass;
        classes = new HashMap<String, Integer>();
        inverse = new HashMap<Integer, String>();
        numClasses = stringToClass.keySet().size();
        Iterator<String> it = stringToClass.keySet().iterator();
        int i = 0;
        String temp;

        // retrieving all of the class information
        while (it.hasNext()) {
            temp = it.next();
            classes.put(temp, i);
            inverse.put(i, temp);
            i++;
        }

        // initializing edges
        edges = new int[numClasses][numClasses];

        // populating edges
        int j;
        String interTemp;
        Iterator<String> interIt;
        FieldData fdTemp;
        Iterator<FieldData> fdIt;
        Set<String> depSet;
        MethodData mdTemp;
        Iterator<MethodData> mdIt;
        Iterator<VariableData> varIt;
        InstrData instrTemp;
        FieldInstrData fieldInstrTemp;
        MethodInstrData methodInstrTemp;
        LocalVarInstrData localVarInstrTemp;
        Iterator<InstrData> instrIt;
        Iterator<String> depIt;
        String depTemp;
        int index;


        for (i = 0; i < numClasses; i++) {
            for (j = 0; j < numClasses; j++) {
                edges[i][j] = 0;
            }

            // extends
            if (classes.containsKey(stringToClass.get(inverse.get(i)).getSuperFullName())) {
                    edges[i][classes.get(stringToClass.get(inverse.get(i)).getSuperFullName())] += 8;
            }

            // implements
            interIt = stringToClass.get(inverse.get(i)).getInterfaceFullNames().iterator();
            while (interIt.hasNext()) {
                interTemp = interIt.next();
                if (classes.containsKey(interTemp)) {
                    edges[i][classes.get(interTemp)] += 4;
                }
            }
            
            // has-a
            fdIt = stringToClass.get(inverse.get(i)).getFields().iterator();
            while (fdIt.hasNext()) {
                fdTemp = fdIt.next();
                if (classes.containsKey(fdTemp.getTypeFullName())) {
                    edges[i][classes.get(fdTemp.getTypeFullName())] += 2;
                }
            }

            //depends-on
            mdIt = stringToClass.get(inverse.get(i)).getMethods().iterator();
            depSet = new HashSet<String>();
            while (mdIt.hasNext()) {
                mdTemp = mdIt.next();
                varIt = mdTemp.getLocalVariables().iterator();
                while (varIt.hasNext()) {
                    depSet.add(varIt.next().typeFullName);
                }
                instrIt = mdTemp.getInstructions().iterator();
                while (instrIt.hasNext()) {
                    instrTemp = instrIt.next();
                    if (instrTemp.getInstrType() == InstrType.METHOD) {
                        methodInstrTemp = (MethodInstrData) instrTemp;
                        depSet.add(methodInstrTemp.getMethodOwnerFullName());
                        depSet.add(methodInstrTemp.getMethodReturnTypeFullName());
                    } else if (instrTemp.getInstrType() == InstrType.FIELD) {
                        fieldInstrTemp = (FieldInstrData) instrTemp;
                        depSet.add(fieldInstrTemp.getFieldOwnerFullName());
                        depSet.add(fieldInstrTemp.getFieldTypeFullName());
                    } else if (instrTemp.getInstrType() == InstrType.LOCAL_VARIABLE) {
                        localVarInstrTemp = (LocalVarInstrData) instrTemp;
                        depSet.add(localVarInstrTemp.getVarTypeFullName());
                    }
                }
            }
            depIt = depSet.iterator();
            while (depIt.hasNext()) {
                depTemp = depIt.next();
                if (classes.containsKey(depTemp)) {
                    index = classes.get(depTemp);
                    if (i != index && edges[i][index] == 0) {
                        edges[i][index] += 1;
                    }
                }
            }
        }
    }

    

    // the edge weights are in [0,15]. binary representation would be
    // --|>, ..|>, -->, ..> in terms of plantuml arrows, left is MSB.
    public static boolean checkExtend(int weight) {
        return weight >= 8;
    }

    public static boolean checkImplement(int weight) {
        return weight % 8 >= 4;
    }

    public static boolean checkHasA(int weight) {
        return weight % 4 >= 2;
    }
    
    public static boolean checkDepends(int weight) {
        return weight % 2 >= 1;
    }

    public int getWeight(int i, int j) {
        return edges[i][j];
    }

    public int inDegree(int v) {
        int i = 0;
        int ret = 0;
        while (i < numClasses) {
            ret += (edges[i][v] != 0)? 1 : 0;
            i++;
        }
        return ret;
    }

    public int outDegree(int v) {
        int j = 0;
        int ret = 0;
        while (j < numClasses) {
            ret += (edges[v][j] != 0)? 1 : 0;
            j++;
        }
        return ret;
    }

    public int getNumClasses() {
        return numClasses;
    }

    public Map<String,ClassData> getClasses() {
        return Map.copyOf(stringToClass);
    }

    public int getIndex(String c) {
        return classes.get(c);
    }

    public String indexToClass(int i) {
        return inverse.get(i);
    }

    public ClassGraphIterator graphIterator(int start) {
        List<Integer> list = new LinkedList<Integer>();
        list.add(start);
        return new ClassGraphIterator(this, start, list);
    }

    // i.e. which classes extend/implement/etc class j
    public int[] column(int j) {
        int i = 0;
        int[] ret = new int[numClasses];
        while (i < numClasses) {
            ret[i] = edges[i][j];
            i++;
        }
        return ret;
    }

    



}
