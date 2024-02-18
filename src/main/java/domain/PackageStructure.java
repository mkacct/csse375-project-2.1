package domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Package Structure. Contains all the (full names of) classes inside of this package, and all of this package's subPackages
 */
public class PackageStructure {
    private final String thisPackageName;
    private final Set<PackageStructure> subPackages;
    private final Set<String> classes;
    public PackageStructure(Set<String> classes) {
        this.thisPackageName = ""; // default package
        this.classes = new HashSet<String>();
        this.subPackages = new HashSet<PackageStructure>();
        Map<String, String> classToClass = new HashMap<String, String>();
        for (String c: classes) {
            classToClass.put(c,c); // this is so we can keep all the full class names, but also recursively remove package prefixes to make it easier to identify new packages and new classes to add
        }
        parseClasses(classToClass);
    }

    private PackageStructure(String thisPackageName, Map<String, String> classes) {
        this.thisPackageName = thisPackageName;
        this.classes = new HashSet<String>();
        this.subPackages = new HashSet<PackageStructure>();
        parseClasses(classes);
    }

    private void parseClasses(Map<String,String> classes) {
        String packageName;
        Map<String,Map<String, String>> packageToClass = new HashMap<String,Map<String, String>>();
        for (String c: classes.keySet()) {
            if (!c.contains(".")) { // this means this has no prefix anymore, so it is in this package
                this.classes.add(classes.get(c));
            } else {
                packageName = c.split("\\.")[0];
                if (packageToClass.containsKey(packageName)) {
                    packageToClass.get(packageName).put(c.substring(packageName.length() + 1), classes.get(c));
                } else {
                    packageToClass.put(packageName, new HashMap<String, String>());
                    packageToClass.get(packageName).put(c.substring(packageName.length() + 1), classes.get(c));
                }
            }
        }
        for (String p : packageToClass.keySet()) {
            this.subPackages.add(new PackageStructure(p, packageToClass.get(p)));
        }
    }

    public String getPackageName() {
        return this.thisPackageName;
    }

    public Set<PackageStructure> getSubPackages() {
        return Set.copyOf(this.subPackages);
    }

    public Set<String> getClasses() {
        return Set.copyOf(this.classes);
    }
}
