package domain;


/**
 * 
 * Helper class for coupling check
 * 
 */ 
public class IntegerAndDegree implements Comparable<IntegerAndDegree>{
    public final int index;
    public final int inDegree;
    IntegerAndDegree(int index, int inDegree) {
        this.index = index;
        this.inDegree = inDegree;
    }
    @Override
    public int compareTo(IntegerAndDegree o) {
        return Integer.compare(this.inDegree, o.inDegree);
    }

    public boolean equals(int index) {
        return this.index == index;
    }
}
