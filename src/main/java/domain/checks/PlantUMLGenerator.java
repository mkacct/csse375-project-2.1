package domain.checks;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Set;

import datasource.Configuration;
import datasource.DataPrinter;
import datasource.FullFilePrinter;
import domain.ClassGraph;
import domain.Message;
import domain.MessageLevel;
import domain.PackageStructure;
import domain.javadata.AccessModifier;
import domain.javadata.ClassData;
import domain.javadata.ClassType;
import domain.javadata.FieldData;
import domain.javadata.MethodData;
import domain.javadata.TypeStructure;
import domain.javadata.VariableData;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;



public class PlantUMLGenerator extends GraphCheck {
    private static final String NAME = "plantUMLGenerator";

    // got this code from the plantuml.com/api
    private static String generateSVG(SourceStringReader source) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        @SuppressWarnings({ "deprecation", "unused" })
        String desc = source.generateImage(os, new FileFormatOption(FileFormat.SVG));
        os.close();

        final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
        return svg;
    }

    public PlantUMLGenerator() {
        super(NAME, false);
    }

    // java and user defined classes are shortened, but other class names are not (like net.sourceforge.plantuml.FileFormat would not be, for example)
    private String getSimpleName(String str) {
        if (isValidFormat(str)) {
            String[] split = str.split("\\.");
            return split[split.length - 1];
        }
        return str;
    }

    private boolean isValidFormat(String str) {
        boolean omitPeriods = !str.contains(".");
        boolean classIsRecognized = graph.getClasses().containsFullName(str);
        boolean isJavaBased = str.split("\\.")[0].equals("java");
        return omitPeriods || classIsRecognized || isJavaBased;
    }

    @Override
    /**
     * @param .pumlOutputPath - The path to put the .puml output at - Defaults to pumlGen.puml
     * @param .svgOutputPath - The path to put the .svg output at - Defaults to svgGen.svg
     */
    public Set<Message> gRun(Configuration config) {
        try {
            String pumlOut = config.getString(".pumlOutputPath", "pumlGen.puml");
            String svgOut = config.getString(".svgOutputPath", "svgGen.svg");
            PackageStructure ps = new PackageStructure(graph.getClasses().getFullNames());
            StringBuilder puml = new StringBuilder();
            writeHeader(puml);
            generatePackage(ps, puml, 0);

            int classes = graph.getNumClasses();
            for (int i = 0; i < classes; i++) {
                for (int j = 0; j < classes; j++) {
                    int weight = graph.getWeight(i, j);
                    checkClassRelationships(puml, i, j, weight);
                }
            }
            createFileOutput(puml, pumlOut, svgOut);
            return Set.of(new Message(MessageLevel.INFO, MessageFormat.format("PlantUML code and image outputted to {0} and {1}", pumlOut, svgOut)));
        } catch (Exception ex) {
            return Set.of(new Message(MessageLevel.ERROR, "Error creating .puml and .svg files"));
        }
    }

    private void createFileOutput(StringBuilder puml, String pumlOut, String svgOut) throws IOException {
        puml.append("@enduml");
        DataPrinter pumlPrint = new FullFilePrinter(pumlOut);
        pumlPrint.print(puml.toString());
        DataPrinter svgPrint = new FullFilePrinter(svgOut);
        svgPrint.print(generateSVG(new SourceStringReader(puml.toString())));
    }

    private void checkClassRelationships(StringBuilder puml, int i, int j, int weight) {
        checkDependsRelationship(puml, i, j, weight);
        checkExtendsRelationship(puml, i, j, weight);
        checkHasRelationship(puml, i, j, weight);
        checkImplementsRelationship(puml, i, j, weight);

    }

    private void checkImplementsRelationship(StringBuilder puml, int i, int j, int weight) {
        boolean isImplements = ClassGraph.checkImplement(weight);
        if (isImplements) {
            String implementsArrow = " ..|> ";
            appendClassInfo(puml, i, j, implementsArrow);
        }
    }

    private void checkHasRelationship(StringBuilder puml, int i, int j, int weight) {
        boolean isHas = ClassGraph.checkHasA(weight);
        if (isHas) {
            String hasArrow = " --> ";
            appendClassInfo(puml, i, j, hasArrow);
        }
    }

    private void checkExtendsRelationship(StringBuilder puml, int i, int j, int weight) {
        boolean isExtends = ClassGraph.checkExtend(weight);
        if (isExtends) {
            String extendsArrow = " --|> ";
            appendClassInfo(puml, i, j, extendsArrow);
        }
    }

    private void checkDependsRelationship(StringBuilder puml, int i, int j, int weight) {
        boolean isDepends = ClassGraph.checkDepends(weight);
        if (isDepends) {
            String dependsArrow = " ..> ";
            appendClassInfo(puml, i, j, dependsArrow);
        }
    }

    private void appendClassInfo(StringBuilder puml, int i, int j, String dependsArrow) {
        puml.append(graph.indexToClass(i));
        puml.append(dependsArrow);
        puml.append(graph.indexToClass(j));
        puml.append("\n");
    }

    private void writeHeader(StringBuilder puml) {
        puml.append("@startuml\n");
        puml.append("'Generated by linter project\n");
        puml.append("'Certain abstract methods and enum methods do not have paramater names available\n");
        puml.append("'Inner classes, Exceptions, and Generic/Paramaterized classes are unsupported\n");
        puml.append("'There may also be the occasional mysterious missing type paramater or return type or dependency\n\n");
    }

    private void generatePackage(PackageStructure ps, StringBuilder puml, int numTabs) {

        Set<String> classNames = ps.getClasses();
        for (String c : classNames) {
            addClassToUML(puml, c, numTabs);
        }
        for (PackageStructure p : ps.getSubPackages()) {
            addPackageToUML(puml, numTabs, p);
        }
    }

    private void addPackageToUML(StringBuilder puml, int numTabs, PackageStructure p) {
        appendTabs(numTabs, puml);
        puml.append("package ");
        puml.append(p.getPackageName());
        puml.append(" {\n");
        generatePackage(p, puml, numTabs + 1);
        appendTabs(numTabs, puml);
        puml.append(" }\n");
    }

    private void addClassToUML(StringBuilder puml, String c, int numTabs) {
        ClassData classData = graph.getClasses().get(c);
        printClassName(c, classData, puml, numTabs);
        writeClass(classData, puml, numTabs + 1);
        appendTabs(numTabs, puml);
        puml.append("}\n");
    }

    private void writeClass(ClassData cd, StringBuilder puml, int numTabs) {
        handleEnumWriting(cd, puml, numTabs);
        handleFieldWriting(cd, puml, numTabs);
        handleMethodWriting(cd, puml, numTabs);
    }

    private void handleMethodWriting(ClassData cd, StringBuilder puml, int numTabs) {
        for (MethodData m : cd.getMethods()) {
            boolean methodIsTag = m.getName().contains("$");
            boolean methodContainsInvalidCharacter = m.getName().contains("<");
            boolean methodIsConstructor = m.getName().equals(MethodData.CONSTRUCTOR_NAME);
            if (!methodIsTag && (!methodContainsInvalidCharacter || methodIsConstructor)) {
                appendTabs(numTabs, puml);
                appendAbstractStaticFinal(m, numTabs, puml);
                if (methodIsConstructor) {
                    puml.append(getSimpleName(cd.getFullName()));
                } else {
                    puml.append(m.getName());
                }
                puml.append("(");
                int vi = 0;
                for (VariableData v : m.getParams()) {
                    handleNonNullVariable(puml, v);
                    printType(v.typeParam(), puml);
                    handleAdditionalParameters(puml, m, vi);
                    vi++;
                }
                puml.append(")");
                handleNonConstructorMethod(puml, m);
                puml.append("\n");
            }
        }
    }

    private void handleFieldWriting(ClassData cd, StringBuilder puml, int numTabs) {
        for (FieldData f : cd.getFields()) {
            boolean classTypeIsEnum = cd.getClassType() != ClassType.ENUM;
            boolean fieldTypeEqualsClassName = f.getTypeFullName().equals(cd.getFullName());
            boolean fieldContainsNoSpecialCharacter = !f.getName().contains("$") && !f.getName().contains("<");
            if ((classTypeIsEnum || !fieldTypeEqualsClassName) && fieldContainsNoSpecialCharacter) {
                appendStaticFinalModifiers(numTabs, puml, f);
            }
        }
    }

    private void handleEnumWriting(ClassData cd, StringBuilder puml, int numTabs) {
        if (cd.getClassType() == ClassType.ENUM) {
            int enums = calculateEnums(cd);
            for (FieldData f: cd.getFields()) {
                handleEnumBasedOnField(cd, puml, numTabs, f, enums);
            }
        }
    }

    private void handleNonConstructorMethod(StringBuilder puml, MethodData m) {
        if (!m.getName().equals(MethodData.CONSTRUCTOR_NAME)) {
            puml.append(": ");
            printType(m.getReturnTypeStructure(), puml);
            if (puml.substring(puml.length()-2).equals(": ")) {
                puml.append(m.getReturnTypeFullName());
            }
        }
    }

    private static void handleAdditionalParameters(StringBuilder puml, MethodData m, int vi) {
        if (vi + 1 != m.getParams().size()) {
                puml.append(", ");
        }
    }

    private static void handleNonNullVariable(StringBuilder puml, VariableData v) {
        if (v.name != null) {
            puml.append(v.name);
            puml.append(": ");
        }
    }

    private void appendAbstractStaticFinal(MethodData m, int numTabs, StringBuilder puml) {
        appendAccessModifier(m.getAccessModifier(), puml);
        appendAbstract(m.isAbstract(), puml);
        appendStatic(m.isStatic(), puml);
        appendFinal(m.isFinal(), puml);
    }

    private void appendStaticFinalModifiers(int numTabs, StringBuilder puml, FieldData f) {
        appendTabs(numTabs, puml);
        appendAccessModifier(f.getAccessModifier(), puml);
        appendStatic(f.isStatic(), puml);
        appendFinal(f.isFinal(), puml);
        puml.append(f.getName());
        puml.append(": ");
        printType(f.typeParam(), puml);
        puml.append("\n");
    }

    private void handleEnumBasedOnField(ClassData cd, StringBuilder puml, int numTabs, FieldData f, int enums) {
        boolean fieldIsEnum = f.getTypeFullName().equals(cd.getFullName());
        if (fieldIsEnum) {
            enums--;
            appendTabs(numTabs, puml);
            puml.append(f.getName());
            if (enums != 0) {
                puml.append(",\n");
            } else {
                puml.append("\n");
            }
        }
    }

    private int calculateEnums(ClassData cd) {
        int i = 0;
        for (FieldData f: cd.getFields()) {
            boolean fieldTypeEqualsClassName = f.getTypeFullName().equals(cd.getFullName());
            if (fieldTypeEqualsClassName) {
                i++;
            }
        }
        return i;
    }

    private void printClassName(String c, ClassData cd, StringBuilder puml, int numTabs) {
        appendTabs(numTabs, puml);
        appendAccessModifier(cd.getAccessModifier(), puml);
        if (cd.getClassType() == ClassType.ENUM) {
            puml.append("enum ");
        } else if (cd.getClassType() == ClassType.INTERFACE) {
            puml.append("interface ");
        } else if (cd.isAbstract()) {
            puml.append("abstract ");
        } else {
            puml.append("class ");
        }
        puml.append(getSimpleName(c));
        puml.append(" ");
        appendStatic(cd.isStatic(), puml);
        appendFinal(cd.isFinal(), puml);
        puml.append("{\n");
    }

    private void appendAccessModifier(AccessModifier am, StringBuilder puml) {
        switch (am) {
            case PACKAGE_PRIVATE:
                puml.append("~");
                break;
            case PRIVATE:
                puml.append("-");
                break;
            case PROTECTED:
                puml.append("#");
                break;
            case PUBLIC:
                puml.append("+");
                break;
        }
    }

    private void appendStatic(boolean isStatic, StringBuilder puml) {
        if (isStatic) {
            puml.append("{static} ");
        }
    }

    private void appendFinal(boolean isFinal, StringBuilder puml) {
        if (isFinal) {
            puml.append("<<final>> ");
        }
    }

    private void appendAbstract(boolean isAbstract, StringBuilder puml) {
        if (isAbstract) {
            puml.append("{abstract} ");
        }
    }

    private void appendTabs(int numTabs, StringBuilder puml) {
        for (int i = 0; i < numTabs; i++) {
            puml.append("\t");
        }
    }

    private void printType(TypeStructure t, StringBuilder puml) {
        puml.append(getSimpleName(t.getFullTypeName()));
        boolean containsSubtypes = !t.getSubTypes().isEmpty();
        if (containsSubtypes) {
            handleSubtypes(t, puml);
        }
      puml.append("[]".repeat(Math.max(0, t.getNumArrays())));
    }

    private void handleSubtypes(TypeStructure t, StringBuilder puml) {
        puml.append("<");
        for (int i = 0; i < t.getSubTypes().size(); i++) {
            printType(t.getSubTypes().get(i), puml);
            if (i + 1 != t.getSubTypes().size()) {
                puml.append(", ");
            }
        }
        puml.append(">");
    }
}
