package domain;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

import datasource.Configuration;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;



public class PlantUMLGenerator extends GraphCheck {    

    // got this code from the plantuml.com/api
    private static String generateSVG(SourceStringReader source) throws IOException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        // Write the first image to "os"
        @SuppressWarnings({ "deprecation", "unused" })
        String desc = source.generateImage(os, new FileFormatOption(FileFormat.SVG));
        os.close();

        // The XML is stored into svg
        final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
        return svg;
    }

    @Override
    public String getName() {
        return "plantUMLGenerator";
    }

    private static String getSimpleName(String str) {
        return null;
    }
    

    @Override
    public Set<Message> gRun(Configuration config) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'gRun'");
    }
}
