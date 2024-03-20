package domain;

import java.util.*;

import domain.javadata.ClassData;
import domain.javadata.ClassType;
import domain.javadata.FieldData;
import domain.javadata.FieldInstrData;
import domain.javadata.InstrData;
import domain.javadata.InstrType;
import domain.javadata.LocalVarInstrData;
import domain.javadata.MethodData;
import domain.javadata.MethodInstrData;
import domain.javadata.VariableData;

public class ClassGraph {
    private final Map<String, ClassData> stringToClass;
    private final Map<String, Integer> classes; 
    private final Map<Integer, String> inverse; // inverse of the other map
    private final int[][] weightedEdges;
    private final int numClasses;

    private String removeArray(String s) {
        if (s == null) {
            return null;
        }
        int index = s.lastIndexOf('[');
        if (index == - 1) {
            return s;
        } else {
            return removeArray(s.substring(0, index));
        }
    }
    public ClassGraph(Map<String, ClassData> strToClass) {
        this.stringToClass = strToClass;
        classes = new HashMap<String, Integer>();
        inverse = new HashMap<Integer, String>();
        numClasses = stringToClass.keySet().size();
        weightedEdges = new int[numClasses][numClasses];
        retrieveClassInformation();
        initializeEdges();
        populateEdges();
    }

    private void retrieveClassInformation() {
        Iterator<String> it = stringToClass.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            String temp = it.next();
            classes.put(temp, i);
            inverse.put(i, temp);
            i++;
        }
    }

    private void initializeEdges() {
        for(int[] arr : weightedEdges) {
            Arrays.fill(arr, 0);
        }
    }

    private void populateEdges() {
        for (int i = 0; i < numClasses; i++) {
            String inverseClassInfo = inverse.get(i);
            ClassData classInfo = stringToClass.get(inverseClassInfo);
            checkForInheritance(classInfo);
            checkForImplements(classInfo);
            checkForComposition(classInfo);
            checkForDependencies(classInfo);
        }
    }

    private void checkForDependencies(ClassData classInfo) {
        //depends-on
        Set<MethodData> methods = classInfo.getMethods();
        Iterator<MethodData> methodsIterator = methods.iterator();
        Set<String> depSet = new HashSet<String>();
        while (methodsIterator.hasNext()) { // first we find all the classes i depends on, and put them in a set to eliminate duplicates
            MethodData mdTemp = methodsIterator.next();
            depSet.addAll(mdTemp.getAllReturnTypeFullName());
            Iterator<VariableData> varIt = mdTemp.getLocalVariables().iterator();
            while (varIt.hasNext()) {
                depSet.addAll(varIt.next().getAllTypeFullName());
            }
            varIt = mdTemp.getParams().iterator();
            while (varIt.hasNext()) {
                depSet.addAll(varIt.next().getAllTypeFullName());
            }
            Iterator<InstrData> instrIt = mdTemp.getInstructions().iterator();
            while (instrIt.hasNext()) {
                InstrData instrTemp = instrIt.next();
                if (instrTemp.getInstrType() == InstrType.METHOD) {
                    MethodInstrData methodInstrTemp = (MethodInstrData) instrTemp;
                    depSet.add(removeArray(methodInstrTemp.getMethodOwnerFullName()));
                    depSet.add(removeArray(methodInstrTemp.getMethodReturnTypeFullName()));
                } else if (instrTemp.getInstrType() == InstrType.FIELD) {
                    FieldInstrData fieldInstrTemp = (FieldInstrData) instrTemp;
                    depSet.add(removeArray(fieldInstrTemp.getFieldOwnerFullName()));
                    depSet.add(removeArray(fieldInstrTemp.getFieldTypeFullName()));
                } else if (instrTemp.getInstrType() == InstrType.LOCAL_VARIABLE) {
                    LocalVarInstrData localVarInstrTemp = (LocalVarInstrData) instrTemp;
                    depSet.add(removeArray(localVarInstrTemp.getVarTypeFullName()));
                }
            }
        }
        Iterator<String> depIt = depSet.iterator();
        while (depIt.hasNext()) {
            String depTemp = depIt.next();
            if (classes.containsKey(depTemp)) {
                Integer index = classes.get(depTemp);
                if (i != index && weightedEdges[i][index] == 0) { // check to see that i doesn't already have implement or extend this class.
                    weightedEdges[i][index] += 1;
                }
            }
        }
    }

    private void checkForComposition(ClassData classInfo) {
        Iterator<FieldData> fdIt = classInfo.getFields().iterator();
        while (fdIt.hasNext()) {
            FieldData fdTemp = fdIt.next();
            checkFieldTypes(fdTemp);
        }
    }

    private void checkFieldTypes(FieldData fdTemp) {
        for (String s : fdTemp.getAllTypeFullName()) {
            if (classes.containsKey(s)) {
                int otherClass = classes.get(s);
                if (!checkHasA(weightedEdges[i][otherClass])) {
                    if(!(i == otherClass && stringToClass.get(inverse.get(i)).getClassType() == ClassType.ENUM)) // Enum's trivially have themselves
                        weightedEdges[i][otherClass] += 2;
                }
            }
        }
    }

    private void checkForImplements(ClassData classInfo) {
        Iterator<String> interIt = interIt = classInfo.getInterfaceFullNames().iterator();
        while (interIt.hasNext()) {
            String interTemp = removeArray(interIt.next());
            boolean classContainsInterface = classes.containsKey(interTemp);
            if (classContainsInterface) {
                weightedEdges[i][classes.get(interTemp)] += 4;
            }
        }
    }

    private void checkForInheritance(ClassData classInfo) {
        String className = classInfo.getSuperFullName();
        String removedName = removeArray(className);
        boolean classContainsRemovedName = classes.containsKey(removedName);

        if (classContainsRemovedName) {
            weightedEdges[i][classes.get(removedName)] += 8;
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
        return weightedEdges[i][j];
    }

    public int inDegree(int v) {
        int i = 0;
        int ret = 0;
        while (i < numClasses) {
            ret += (weightedEdges[i][v] != 0)? 1 : 0;
            i++;
        }
        return ret;
    }

    public int outDegree(int v) {
        int j = 0;
        int ret = 0;
        while (j < numClasses) {
            ret += (weightedEdges[v][j] != 0)? 1 : 0;
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

    /**
     * 
     * @param j 
     * @return An array of weights (i,j) for all i
     */
    public int[] column(int j) {
        int i = 0;
        int[] ret = new int[numClasses];
        while (i < numClasses) {
            ret[i] = weightedEdges[i][j];
            i++;
        }
        return ret;
    }

    



}
