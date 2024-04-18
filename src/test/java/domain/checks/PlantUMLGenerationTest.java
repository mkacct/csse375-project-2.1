package domain.checks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import datasource.Configuration;
import datasource.DirLoader;
import datasource.FilesLoader;
import domain.Check;
import domain.Message;
import domain.MessageLevel;
import domain.TestUtility;
import domain.javadata.ClassDataCollection;

public class PlantUMLGenerationTest {
    private static final String STRING_RESOURCE_PATH1 = "src/test/resources/graphtest";
    private static final String STRING_RESOURCE_PATH2 = "src/test/resources/TypeParamaterTest";
    private static final String STRING_RESOURCE_PATH3 = "src/test/resources/LowCouplingTest";
    private static final String PUMLOUT_PATH1 = "src/test/resources/PlantUMLGenerationTest/pout1.puml";
    private static final String SVGOUT_PATH1 = "src/test/resources/PlantUMLGenerationTest/svgout1.puml";
    private static final String PUMLOUT_PATH2 = "src/test/resources/PlantUMLGenerationTest/pout2.puml";
    private static final String SVGOUT_PATH2 = "src/test/resources/PlantUMLGenerationTest/svgout2.puml";
    private static final String PUMLOUT_PATH3 = "src/test/resources/PlantUMLGenerationTest/pout3.puml";
    private static final String SVGOUT_PATH3 = "src/test/resources/PlantUMLGenerationTest/svgout3.puml";


    // The order of lines is nondeterministic, so I can't really test the svgs
    private static final String PUMLOUT_PATH1e = "src/test/resources/PlantUMLGenerationTest/pout1e.puml";
    //private static final String SVGOUT_PATH1e = "src/test/resources/PlantUMLGenerationTest/svgout1e.puml";
    private static final String PUMLOUT_PATH2e = "src/test/resources/PlantUMLGenerationTest/pout2e.puml";
    //private static final String SVGOUT_PATH2e = "src/test/resources/PlantUMLGenerationTest/svgout2e.puml";
    private static final String PUMLOUT_PATH3e = "src/test/resources/PlantUMLGenerationTest/pout3e.puml";
    //private static final String SVGOUT_PATH3e = "src/test/resources/PlantUMLGenerationTest/svgout3e.puml";
    Check pumlg = new PlantUMLGenerator();
    ClassDataCollection classes1;
    ClassDataCollection classes2;
    ClassDataCollection classes3;

	@BeforeEach
	public void setup() throws IOException {
		FilesLoader fl;

        fl = new DirLoader(STRING_RESOURCE_PATH1);
        classes1 = TestUtility.toClassDataCollection(fl.loadFiles("class"));

        fl = new DirLoader(STRING_RESOURCE_PATH2);
        classes2 = TestUtility.toClassDataCollection(fl.loadFiles("class"));

        fl = new DirLoader(STRING_RESOURCE_PATH3);
        classes3 = TestUtility.toClassDataCollection(fl.loadFiles("class"));
	}



    @Test
	public void testSystem1() throws FileNotFoundException {
		Set<Message> out = pumlg.run(classes1, new Configuration(Map.of(
            ".pumlOutputPath", PUMLOUT_PATH1,
            ".svgOutputPath", SVGOUT_PATH1
        )));
        Set<Message> exp = Set.of(new Message(MessageLevel.INFO, MessageFormat.format("PlantUML code and image outputted to {0} and {1}", PUMLOUT_PATH1, SVGOUT_PATH1)));
        assertEquals(exp, out);
        assertEquals(TestUtility.getEntireFile(PUMLOUT_PATH1e), TestUtility.getEntireFile(PUMLOUT_PATH1));
	}

    @Test
	public void testSystem2() throws FileNotFoundException {
		Set<Message> out = pumlg.run(classes2, new Configuration(Map.of(
            ".pumlOutputPath", PUMLOUT_PATH2,
            ".svgOutputPath", SVGOUT_PATH2
        )));
        Set<Message> exp = Set.of(new Message(MessageLevel.INFO, MessageFormat.format("PlantUML code and image outputted to {0} and {1}", PUMLOUT_PATH2, SVGOUT_PATH2)));
        assertEquals(exp, out);
        assertEquals(TestUtility.getEntireFile(PUMLOUT_PATH2e), TestUtility.getEntireFile(PUMLOUT_PATH2));
	}

    @Test
	public void testSystem3() throws FileNotFoundException {
		Set<Message> out = pumlg.run(classes3, new Configuration(Map.of(
            ".pumlOutputPath", PUMLOUT_PATH3,
            ".svgOutputPath", SVGOUT_PATH3
        )));
        Set<Message> exp = Set.of(new Message(MessageLevel.INFO, MessageFormat.format("PlantUML code and image outputted to {0} and {1}", PUMLOUT_PATH3, SVGOUT_PATH3)));
        assertEquals(exp, out);
        assertEquals(TestUtility.getEntireFile(PUMLOUT_PATH3e), TestUtility.getEntireFile(PUMLOUT_PATH3));
	}

    @Test
    public void testError() {
		Set<Message> out = pumlg.run(classes3, new Configuration(Map.of(
            ".pumlOutputPath", "",
            ".svgOutputPath", ""
        )));
        Set<Message> exp = Set.of(new Message(MessageLevel.ERROR, "Error creating .puml and .svg files"));
        assertEquals(exp, out);
    }
}
