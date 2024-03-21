package domain;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Set;

import datasource.Configuration;
import datasource.DataPrinter;
import datasource.FullFilePrinter;
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
        boolean classIsRecognized = graph.getClasses().containsKey(str);
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
            PackageStructure ps = new PackageStructure(graph.getClasses().keySet());
            StringBuilder puml = new StringBuilder();
            writeHeader(puml);
            generatePackage(ps, puml, 0);

            // arrows
            int classes = graph.getNumClasses();
            for (int i = 0; i < classes; i++) {
                for (int j = 0; j < classes; j++) {
                    int weight = graph.getWeight(i, j);
                    checkClassRelationships(puml, i, j);
                }
            }



            // file output
            puml.append("@enduml");
            DataPrinter pumlPrint = new FullFilePrinter(pumlOut);
            pumlPrint.print(puml.toString());
            DataPrinter svgPrint = new FullFilePrinter(svgOut);
            svgPrint.print(generateSVG(new SourceStringReader(puml.toString())));


            return Set.of(new Message(MessageLevel.INFO, MessageFormat.format("PlantUML code and image outputted to {0} and {1}", pumlOut, svgOut)));
        } catch (Exception ex) {
            // Probably a file error occured
            return Set.of(new Message(MessageLevel.ERROR, "Error creating .puml and .svg files"));
        }
    }

    private void checkClassRelationships(StringBuilder puml, int i, int j) {
        checkDependsRelationship(puml, i, j);
        checkExtendsRelationship(puml, i, j);
        checkHasRelationship(puml, i, j);
        checkImplementsRelationship(puml, i, j);

    }

    private void checkImplementsRelationship(StringBuilder puml, int i, int j) {
        boolean isImplements = ClassGraph.checkImplement(graph.getWeight(i, j));
        if (isImplements) {
            String implementsArrow = " ..|> ";
            appendClassInfo(puml, i, j, implementsArrow);
        }
    }

    private void checkHasRelationship(StringBuilder puml, int i, int j) {
        boolean isHas = ClassGraph.checkHasA(graph.getWeight(i, j));
        if (isHas) {
            String hasArrow = " --> ";
            appendClassInfo(puml, i, j, hasArrow);
        }
    }

    private void checkExtendsRelationship(StringBuilder puml, int i, int j) {
        boolean isExtends = ClassGraph.checkExtend(graph.getWeight(i, j));
        if (isExtends) {
            String extendsArrow = " --|> ";
            appendClassInfo(puml, i, j, extendsArrow);
        }
    }

    private void checkDependsRelationship(StringBuilder puml, int i, int j) {
        if (ClassGraph.checkDepends(graph.getWeight(i, j))) {
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
        ClassData cd;
        for (String c : ps.getClasses()) {
            cd = graph.getClasses().get(c);
            printClassName(c, cd, puml, numTabs);
            writeClass(cd, puml, numTabs + 1);
            appendTabs(numTabs, puml);
            puml.append("}\n");
        }
        for (PackageStructure p : ps.getSubPackages()) {
            appendTabs(numTabs, puml);
            puml.append("package ");
            puml.append(p.getPackageName());
            puml.append(" {\n");
            generatePackage(p, puml, numTabs + 1);
            appendTabs(numTabs, puml);
            puml.append(" }\n");
        }
    }

    private void writeClass(ClassData cd, StringBuilder puml, int numTabs) {
        if (cd.getClassType() == ClassType.ENUM) {
            int i = 0;
            for (FieldData f: cd.getFields()) {
                if (f.getTypeFullName().equals(cd.getFullName())) {
                    i++; // this is to check how many enums there are
                    // so we can change between putting a , or not at the end of a line
                }
            }
            for (FieldData f: cd.getFields()) {
                if (f.getTypeFullName().equals(cd.getFullName())) {
                    i--;
                    appendTabs(numTabs, puml);
                    puml.append(f.getName());
                    if (i != 0) {
                        puml.append(",\n");
                    } else {
                        puml.append("\n");
                    }
                }
            }
        }
        for (FieldData f : cd.getFields()) {
            if ((cd.getClassType() != ClassType.ENUM || !f.getTypeFullName().equals(cd.getFullName())) &&
                     !f.getName().contains("$") && !f.getName().contains("<")) { // all non-enum values.
                                        // also ignoring fields with $ and < from java, although apparently
                                        // $ is fine in user-defined fields.
                appendTabs(numTabs, puml);
                appendAccessModifier(f.getAccessModifier(), puml);
                appendStatic(f.isStatic(), puml);
                appendFinal(f.isFinal(), puml);
                puml.append(f.getName());
                puml.append(": ");
                printType(f.typeParam(), puml);
                puml.append("\n");
            }

        }
        for (MethodData m : cd.getMethods()) {
            if (!m.getName().contains("$") && (!m.getName().contains("<") || m.getName().equals(MethodData.CONSTRUCTOR_NAME))) {
            appendTabs(numTabs, puml);
            appendAccessModifier(m.getAccessModifier(), puml);
            appendAbstract(m.isAbstract(), puml);
            appendStatic(m.isStatic(), puml);
            appendFinal(m.isFinal(), puml);
            if (m.getName().equals(MethodData.CONSTRUCTOR_NAME)) {
                puml.append(getSimpleName(cd.getFullName()));
            } else {
                puml.append(m.getName());
            }
            puml.append("(");
            int vi = 0;
            for (VariableData v : m.getParams()) {
                if (v.name == null) { // I'm not exactly sure when this happens, but it seems to happen with all abstract methods, and some enum methods
                    printType(v.typeParam(), puml);
                    if (vi + 1 != m.getParams().size()) {
                        puml.append(", ");
                    }
                } else {
                    puml.append(v.name);
                    puml.append(": ");
                    printType(v.typeParam(), puml);
                    if (vi + 1 != m.getParams().size()) {
                        puml.append(", ");
                    }
                }
                vi++;
            }
            puml.append(")");
            if (m.getName().equals(MethodData.CONSTRUCTOR_NAME)) {

            } else {
                puml.append(": ");
                printType(m.getReturnTypeStructure(), puml);
                if (puml.substring(puml.length()-2).equals(": ")) {
                    puml.append(m.getReturnTypeFullName());
                }
            }
            puml.append("\n");
        }
        }
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
        if (!t.getSubTypes().isEmpty()) {
            puml.append("<");
            for (int i = 0; i < t.getSubTypes().size(); i++) {
                printType(t.getSubTypes().get(i), puml);
                if (i + 1 != t.getSubTypes().size()) {
                    puml.append(", ");
                }
            }
            puml.append(">");
        }
        for (int i = 0; i < t.getNumArrays(); i++) {
            puml.append("[]");
        }
    }
}
