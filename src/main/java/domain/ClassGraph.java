package domain;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import domain.javadata.ClassData;
import domain.javadata.FieldData;
import domain.javadata.VariableData;

public class ClassGraph {
    Map<String, ClassData> stringToClass;
    Map<String, Integer> classes; 
    Map<Integer, String> inverse; // inverse of the other map
    int[][] edges; // weighted
    int numClasses;
    public ClassGraph(Map<String, ClassData> strToClass) {
        this.stringToClass = strToClass;
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
        VariableData varTemp;
        Iterator<VariableData> varIt;

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

            //TODO: finish depends-on creation in graph
            //plan:
            //create a set of the following:
            //  methodReturnTypes
            //  methodLocalVarTypes(including Params)
            //  anything from "FullName" in instrTypes
            //then check from that set
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
            ret += (edges[i][ret] != 0)? 1 : 0;
            i++;
        }
        return ret;
    }

    public int outDegree(int v) {
        int j = 0;
        int ret = 0;
        while (j < numClasses) {
            ret += (edges[ret][j] != 0)? 1 : 0;
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

    // i.e. which classes extend/implement/etc on class i
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
