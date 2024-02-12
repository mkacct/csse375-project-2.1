package domain;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;



public class PlantUMLGenerator {
    SourceStringReader test;
    File source = new File("design.puml");
    public PlantUMLGenerator() throws IOException {
        Scanner scan = new Scanner(source);
        // yoinked this from stack overflow
        scan.useDelimiter("\\Z");
        String eee = scan.next();
        test = new SourceStringReader(eee);
    }
    
    public String test() throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        // Write the first image to "os"
        String desc = test.generateImage(os, new FileFormatOption(FileFormat.SVG));
        os.close();

        // The XML is stored into svg
        final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
        return svg;
    }
}
