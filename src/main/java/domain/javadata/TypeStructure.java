package domain.javadata;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TypeStructure {
    private final String baseTypeFullName;
    private final int numOfArray;
    private List<TypeStructure> subtypes;
    public TypeStructure(String signature) { // base constructor. Singature stores information about the entire type
        StringBuilder typeFullName = new StringBuilder();
        if (signature.contains(")")) { // credit to Eric Bender (using ChatGPT) for noticing that you can do this
            signature = signature.split("\\)")[1];
        }
        char[] chars = signature.toCharArray();
        int i = 0;
        while (chars[i] == '[') { // find the "array depth" of this type (i.e. char[][][][][] has 5 arrays or whatever)
            i++;
        }
        numOfArray = i;
        i++; // remove L prefix
        while (i < chars.length && chars[i] != '<' && chars[i] != ';') {
            if(chars[i] == '/') {
                typeFullName.append('.');
            } else {
                typeFullName.append(chars[i]);
            }
            i++;
        }
        baseTypeFullName = typeFullName.toString();
        if (i >= chars.length || chars[i] == ';') {
            subtypes = List.of();
        } else {
            StringBuilder strings = new StringBuilder();
            int depth = 1;
            i++;
            subtypes = new LinkedList<TypeStructure>();
            while (i < chars.length) {
                if (chars[i] == '<') {
                    depth++;
                    strings.append(chars[i]);
                } else
                if (chars[i] == '>') {
                    depth--;
                    if (depth == 0) { // this means we have finished
                        break;
                    } else {
                        strings.append(chars[i]);
                    }

                } else
                if (chars[i] == ';') {
                    strings.append(chars[i]);
                    if (depth == 1) { // this means we are in our base type paramater list
                        subtypes.add(new TypeStructure(strings.toString()));
                        strings.delete(0, strings.length());
                    }
                } else {
                    strings.append(chars[i]);
                }
                i++;
            }
        }
    }
    public TypeStructure(String typeFullName, int numArray) {
        baseTypeFullName = typeFullName;
        numOfArray = numArray;
        subtypes = List.of();
    }
    public String getFullTypeName() {
        return baseTypeFullName;
    }
    /**
     * 
     * @return All of the Full Type Names used in this type, including itself and any type paramaters
     */
    public Set<String> getAllFullTypeNames() {
        Set<String> ret = new HashSet<String>();
        ret.add(this.getFullTypeName());
        for (TypeStructure t : subtypes) {
            ret.addAll(t.getAllFullTypeNames());
        }
        return ret;
    }
    /**
     * 
     * @return The number of arrays on this type (does not include any additional arrays in type paramaters)
     */
    public int getNumArrays() {
        return numOfArray;
    }

    /**
     * 
     * @return Gets all the type paramaters of this type
     */
    public List<TypeStructure> getSubTypes() {
        return List.copyOf(subtypes);
    }
}
