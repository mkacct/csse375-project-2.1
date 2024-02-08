package domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClassGraph {
    Set<String> packages;
    Map<Object, Integer> classes; 
    Map<Integer, Object> inverse; // inverse of the other map
    int[][] edges; // weighted
    int numClasses;
    public ClassGraph() {
        // TODO: constructor
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

    public Set<Object> getClasses() {
        return Set.copyOf(classes.keySet());
    }

    public int getIndex(Object c) {
        return classes.get(c);
    }

    public Object indexToClass(int i) {
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
