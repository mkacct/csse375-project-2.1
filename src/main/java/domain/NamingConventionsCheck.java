package domain;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.ClassType;
import domain.javadata.FieldData;
import domain.javadata.MethodData;
import domain.javadata.VariableData;

public class NamingConventionsCheck extends Check {
    private static final String NAME = "namingConventions";

    public NamingConventionsCheck() {
        super(NAME);
    }

    private boolean checkConvention(String str, NamingConventions convention) {
        char[] chars = str.toCharArray();
        if (chars.length == 0) { // idk if this can even happen
            return false;
        }
        if (str.contains("$") || str.contains("<") || str.contains(">")) { // not user defined names
            return true;
        }
        return convention.check(chars);
    }

    @Override
    /**
     * @param convPackage -- One of the NamingConvention enum values. No input defaults to standard convention. Invalid input defaults to ANY.
     * @param convClass
     * @param convInterface
     * @param convAbstract
     * @param convEnum
     * @param convField
     * @param convMethod
     * @param convConstant
     * @param convEnumConstant
     * @param convLocalVar
     * @param convMethodParam
     * @param convAllowEmptyPackage - boolean - Whether to allow for "" a package name (i.e. no package). Defaults to false
     * @param convMaxLength - int - Maximum name length. -1 for no max. Defaults to -1.
     */
    public Set<Message> run(Map<String, ClassData> classes, Configuration config) {

        Set<Message> messages = new HashSet<Message>();
        NamingConventions packageNames = NamingConventions.getConvention(config.getString("convPackage", "lowercase"));
        NamingConventions classNames = NamingConventions.getConvention(config.getString("convClass", "PascalCase"));
        NamingConventions interfaceNames = NamingConventions.getConvention(config.getString("convInterface", "PascalCase"));
        NamingConventions abstractNames = NamingConventions.getConvention(config.getString("convAbstract", "PascalCase"));
        NamingConventions enumNames = NamingConventions.getConvention(config.getString("convEnum", "PascalCase"));
        NamingConventions fieldNames = NamingConventions.getConvention(config.getString("convField", "camelCase"));
        NamingConventions methodNames = NamingConventions.getConvention(config.getString("convMethod", "camelCase"));
        NamingConventions constantNames = NamingConventions.getConvention(config.getString("convConstant", "UPPER_CASE"));
        NamingConventions enumConstantNames = NamingConventions.getConvention(config.getString("convEnumConstant", "UPPER_CASE"));
        NamingConventions localVarNames = NamingConventions.getConvention(config.getString("convLocalVar", "camelCase"));
        NamingConventions methodParamNames = NamingConventions.getConvention(config.getString("convMethodParam", "camelCase"));
        boolean allowEmptyPackage = config.getBoolean("convAllowEmptyPackage", false);

        int maxLength = config.getInt("convMaxLength", -1);
        if (maxLength == -1) {
            maxLength = Integer.MAX_VALUE;
        }
        ClassData classInfo;
        // So we don't produce multiple messages from same package name. This technically means that if you have something like domain.javadata and datasource.javadata, only one javadata would be reported
        Set<String> packages = new HashSet<String>();
        for (String s : classes.keySet()) {
            classInfo = classes.get(s);

            // Class Name checks
            if (classInfo.getSimpleName().length() > maxLength) {
                messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Class Name exceeds {0} characters", maxLength), classInfo.getFullName()));
            }


            if (classInfo.isAbstract() && ClassType.INTERFACE != classInfo.getClassType()) { // abstract class
                if (!checkConvention(classInfo.getSimpleName(), abstractNames)) {
                    messages.add(new Message(MessageLevel.WARNING, "Abstract Class Naming Violation", classInfo.getFullName()));
                }

            } else if (ClassType.INTERFACE == classInfo.getClassType()) { // interface
                if (!checkConvention(classInfo.getSimpleName(), interfaceNames)) {
                    messages.add(new Message(MessageLevel.WARNING, "Interface Naming Violation", classInfo.getFullName()));
                }
            } else if (ClassType.ENUM == classInfo.getClassType()) { //enum
                if (!checkConvention(classInfo.getSimpleName(), enumNames)) {
                    messages.add(new Message(MessageLevel.WARNING, "Enum Naming Violation", classInfo.getFullName()));
                }
            } else { // concrete class
                if (!checkConvention(classInfo.getSimpleName(), classNames)) {
                    messages.add(new Message(MessageLevel.WARNING, "Class Naming Violation", classInfo.getFullName()));
                }
            }

            // packages
            for (String pckg : classInfo.getPackageName().split("\\.|\\$")) {
                if (packages.add(pckg)) {
                    if (pckg.length() > maxLength) {
                        messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Package ({0}) Name exceeds {1} characters", pckg, maxLength)));
                    }
                    if (!checkConvention(pckg, packageNames)) {
                        if (allowEmptyPackage && pckg.equals("")) {
                        } else {
                            messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Package ({0}) Naming Violation", pckg)));
                        }
                    }

                }
            }



            // field-like checks
            if (ClassType.ENUM == classInfo.getClassType()) {
                for (FieldData f : classInfo.getFields()) {
                    if (f.getName().length() > maxLength) {
                        messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Field (or constant or Enum constant) ({0}) name exceeds {1} characters", f.getName(), maxLength), classInfo.getFullName()));
                    }
                    if (f.getTypeFullName().equals(classInfo.getFullName())) {
                        if (!checkConvention(f.getName(), enumConstantNames)) { // the actual enum values
                            messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", f.getName()), classInfo.getFullName()));
                        }
                    } else if (f.isStatic() && f.isFinal()) {
                        if (!checkConvention(f.getName(), constantNames)) { // constant fields in enum
                            messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Constant ({0}) Naming Violation", f.getName()), classInfo.getFullName()));
                        }
                    } else if (!checkConvention(f.getName(), fieldNames)) { // normal fields in enum
                        messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Field ({0}) Naming Violation", f.getName()), classInfo.getFullName()));
                    }
                }
            } else {
                for (FieldData f : classInfo.getFields()) {
                    if (f.getName().length() > maxLength) {
                        messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Field (or constant) ({0}) name exceeds {1} characters", f.getName(), maxLength), classInfo.getFullName()));
                    }
                    if (f.isStatic() && f.isFinal()) {
                        if (!checkConvention(f.getName(), constantNames)) {
                            messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Constant ({0}) Naming Violation", f.getName()), classInfo.getFullName()));
                        }
                    } else if (!checkConvention(f.getName(), fieldNames)) {
                        messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Field ({0}) Naming Violation", f.getName()), classInfo.getFullName()));
                    }
                }
            }


            // method checks
            for (MethodData m : classInfo.getMethods()) {
                if (m.getName().length() > maxLength) {
                    messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Method ({0}) name exceeds {1} characters", m.getName(), maxLength), classInfo.getFullName()));
                }
                if (!checkConvention(m.getName(), methodNames)) {
                    messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Method ({0}) Naming Violation", m.getName()), classInfo.getFullName()));
                }


                // var checks
                for (VariableData lvar : m.getLocalVariables()) {

                        if (lvar.name == null) {
                            continue;
                        }
                        if (lvar.name.length() > maxLength) {
                            messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Local Variable or Method Param ({0} in {1}) name exceeds {2} characters", lvar.name, m.getName(), maxLength), classInfo.getFullName()));
                        }
                        if (m.getParams().contains(lvar)) {
                            if (!checkConvention(lvar.name, methodParamNames)) {
                                messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Method Paramater ({0} of {1}) Naming Violation", lvar.name, m.getName()), classInfo.getFullName()));
                            }
                        } else if (!checkConvention(lvar.name, localVarNames)) {
                            messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Local Variable ({0} in {1}) Naming Violation", lvar.name, m.getName()), classInfo.getFullName()));
                        }

                }
            }
        }
        return messages;
    }

}
