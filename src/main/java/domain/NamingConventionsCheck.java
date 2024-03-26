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
        if (chars.length == 0) {
            return false;
        }
        if (str.contains("$") || str.contains("<") || str.contains(">")) { // not user defined names
            return true;
        }
        return convention.check(chars);
    }

    @Override
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
            runClassNameChecks(classInfo, maxLength, messages, abstractNames, interfaceNames, enumNames, classNames);
            runPackageChecks(classInfo, packages, maxLength, messages, packageNames, allowEmptyPackage);

            // field-like checks
            if (ClassType.ENUM == classInfo.getClassType()) {
                for (FieldData f : classInfo.getFields()) {
                    maxLengthCheck(f.getName().length() > maxLength, messages, new Message(MessageLevel.WARNING, MessageFormat.format("Field (or constant or Enum constant) ({0}) name exceeds {1} characters", f.getName(), maxLength), classInfo.getFullName()));
                    if (f.getTypeFullName().equals(classInfo.getFullName())) {
                        maxLengthCheck(!checkConvention(f.getName(), enumConstantNames), messages, new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", f.getName()), classInfo.getFullName()));
                    } else if (f.isStatic() && f.isFinal()) {
                        maxLengthCheck(!checkConvention(f.getName(), constantNames), messages, new Message(MessageLevel.WARNING, MessageFormat.format("Constant ({0}) Naming Violation", f.getName()), classInfo.getFullName()));
                    } else
                        maxLengthCheck(!checkConvention(f.getName(), fieldNames), messages, new Message(MessageLevel.WARNING, MessageFormat.format("Field ({0}) Naming Violation", f.getName()), classInfo.getFullName()));
                }
            } else {
                for (FieldData f : classInfo.getFields()) {
                    maxLengthCheck(f.getName().length() > maxLength, messages, new Message(MessageLevel.WARNING, MessageFormat.format("Field (or constant) ({0}) name exceeds {1} characters", f.getName(), maxLength), classInfo.getFullName()));
                    if (f.isStatic() && f.isFinal()) {
                        maxLengthCheck(!checkConvention(f.getName(), constantNames), messages, new Message(MessageLevel.WARNING, MessageFormat.format("Constant ({0}) Naming Violation", f.getName()), classInfo.getFullName()));
                    } else
                        maxLengthCheck(!checkConvention(f.getName(), fieldNames), messages, new Message(MessageLevel.WARNING, MessageFormat.format("Field ({0}) Naming Violation", f.getName()), classInfo.getFullName()));
                }
            }


            // method checks
            for (MethodData m : classInfo.getMethods()) {
                maxLengthCheck(m.getName().length() > maxLength, messages, new Message(MessageLevel.WARNING, MessageFormat.format("Method ({0}) name exceeds {1} characters", m.getName(), maxLength), classInfo.getFullName()));
                maxLengthCheck(!checkConvention(m.getName(), methodNames), messages, new Message(MessageLevel.WARNING, MessageFormat.format("Method ({0}) Naming Violation", m.getName()), classInfo.getFullName()));


                // var checks
                for (VariableData lvar : m.getLocalVariables()) {

                    if (lvar.name == null) {
                        continue;
                    }
                    maxLengthCheck(lvar.name.length() > maxLength, messages, new Message(MessageLevel.WARNING, MessageFormat.format("Local Variable or Method Param ({0} in {1}) name exceeds {2} characters", lvar.name, m.getName(), maxLength), classInfo.getFullName()));
                    if (m.getParams().contains(lvar)) {
                        maxLengthCheck(!checkConvention(lvar.name, methodParamNames), messages, new Message(MessageLevel.WARNING, MessageFormat.format("Method Paramater ({0} of {1}) Naming Violation", lvar.name, m.getName()), classInfo.getFullName()));
                    } else
                        maxLengthCheck(!checkConvention(lvar.name, localVarNames), messages, new Message(MessageLevel.WARNING, MessageFormat.format("Local Variable ({0} in {1}) Naming Violation", lvar.name, m.getName()), classInfo.getFullName()));

                }
            }
        }
        return messages;
    }

    private void runPackageChecks(ClassData classInfo, Set<String> packages, int maxLength, Set<Message> messages, NamingConventions packageNames, boolean allowEmptyPackage) {
        for (String pckg : classInfo.getPackageName().split("\\.|\\$")) {
            checkPackage(packages, maxLength, messages, packageNames, allowEmptyPackage, pckg);
        }
    }

    private void checkPackage(Set<String> packages, int maxLength, Set<Message> messages, NamingConventions packageNames, boolean allowEmptyPackage, String pckg) {
        if (packages.add(pckg)) {
            maxLengthCheck(pckg.length() > maxLength, messages, new Message(MessageLevel.WARNING, MessageFormat.format("Package ({0}) Name exceeds {1} characters", pckg, maxLength)));
            handleIncorrectConventionsOrEmptyPackage(messages, packageNames, allowEmptyPackage, pckg);
        }
    }

    private void handleIncorrectConventionsOrEmptyPackage(Set<Message> messages, NamingConventions packageNames, boolean allowEmptyPackage, String pckg) {
        boolean incorrectConventions = !checkConvention(pckg, packageNames);
        boolean isNotEmptyPackage = !allowEmptyPackage || !pckg.isEmpty();
        if (incorrectConventions && isNotEmptyPackage) {
            messages.add(new Message(MessageLevel.WARNING, MessageFormat.format("Package ({0}) Naming Violation", pckg)));
        }
    }

    private void runClassNameChecks(ClassData classInfo, int maxLength, Set<Message> messages, NamingConventions abstractNames, NamingConventions interfaceNames, NamingConventions enumNames, NamingConventions classNames) {
        maxLengthCheck(classInfo.getSimpleName().length() > maxLength, messages, new Message(MessageLevel.WARNING, MessageFormat.format("Class Name exceeds {0} characters", maxLength), classInfo.getFullName()));
        boolean isAbstractAndImplements = classInfo.isAbstract() && ClassType.INTERFACE != classInfo.getClassType();
        boolean isInterface = ClassType.INTERFACE == classInfo.getClassType();
        boolean isEnum = ClassType.ENUM == classInfo.getClassType();
        if (isAbstractAndImplements) {
            maxLengthCheck(!checkConvention(classInfo.getSimpleName(), abstractNames), messages, new Message(MessageLevel.WARNING, "Abstract Class Naming Violation", classInfo.getFullName()));
        } else if (isInterface) {
            maxLengthCheck(!checkConvention(classInfo.getSimpleName(), interfaceNames), messages, new Message(MessageLevel.WARNING, "Interface Naming Violation", classInfo.getFullName()));
        } else if (isEnum) {
            maxLengthCheck(!checkConvention(classInfo.getSimpleName(), enumNames), messages, new Message(MessageLevel.WARNING, "Enum Naming Violation", classInfo.getFullName()));
        } else {
            maxLengthCheck(!checkConvention(classInfo.getSimpleName(), classNames), messages, new Message(MessageLevel.WARNING, "Class Naming Violation", classInfo.getFullName()));
        }
    }

    private static void maxLengthCheck(boolean classInfo, Set<Message> messages, Message WARNING) {
        if (classInfo) {
            messages.add(WARNING);
        }
    }

}