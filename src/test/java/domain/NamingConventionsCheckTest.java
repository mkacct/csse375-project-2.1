package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import datasource.Configuration;
import datasource.DirLoader;
import datasource.FilesLoader;
import domain.javadata.ClassDataCollection;

/**
 * Test Class Graph on
 */
public class NamingConventionsCheckTest {
	private static final String STRING_RESOURCE_PATH = "src/test/resources/NamingConventionTest";
    Check ncc = new NamingConventionsCheck();
    ClassDataCollection classes;

	@BeforeEach
	public void setup() throws IOException {
		FilesLoader fl = new DirLoader(STRING_RESOURCE_PATH);

        classes = TestUtility.toClassDataCollection(fl.loadFiles("class"));
	}



	@Test
	public void testDefaultConfig() {
		Set<Message> out = ncc.run(classes, new Configuration(Map.of()));
        Set<Message> exp = Set.of(
            new Message(MessageLevel.WARNING, "Abstract Class Naming Violation", "weirdStuff.weirdabstractclass"),
            new Message(MessageLevel.WARNING, "Interface Naming Violation", "weirdStuff.weird_interface"),
            new Message(MessageLevel.WARNING, "Enum Naming Violation", "weirdStuff.weirdEnum"),
            new Message(MessageLevel.WARNING, "Class Naming Violation", "weirdStuff.WEIRD_CLASS"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Package ({0}) Naming Violation", "")),
            new Message(MessageLevel.WARNING, MessageFormat.format("Package ({0}) Naming Violation", "weirdStuff")),
            new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", "one"), "weirdStuff.weirdEnum"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", "two"), "weirdStuff.weirdEnum"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", "three"), "weirdStuff.weirdEnum"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", "four"), "weirdStuff.weirdEnum"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", "five"), "weirdStuff.weirdEnum"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", "six"), "weirdStuff.weirdEnum"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", "seven"), "weirdStuff.weirdEnum"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Constant ({0}) Naming Violation", "pi"), "weirdStuff.weird_interface"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Constant ({0}) Naming Violation", "number_eight"), "weirdStuff.WEIRD_CLASS"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Field ({0}) Naming Violation", "NINE"), "weirdStuff.weirdabstractclass"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Field ({0}) Naming Violation", "SEVEN"), "weirdStuff.WEIRD_CLASS"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Field ({0}) Naming Violation", "TR_EE"), "weirdStuff.WEIRD_CLASS"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Method ({0}) Naming Violation", "AddSeven"), "weirdStuff.weirdabstractclass"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Method ({0}) Naming Violation", "Area"), "weirdStuff.weird_interface"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Method ({0}) Naming Violation", "Cake"), "weirdStuff.WEIRD_CLASS"),
            //I guess technically this is camelCase //new Message(MessageLevel.WARNING, MessageFormat.format("Method Paramater ({0} of {1}) Naming Violation", "rAdIuS", "Area"), "weirdStuff.weird_interface"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Method Paramater ({0} of {1}) Naming Violation", "Ni_nE", "Cake"), "weirdStuff.WEIRD_CLASS"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Method Paramater ({0} of {1}) Naming Violation", "C_A_K_E_flavor", "Cake"), "weirdStuff.WEIRD_CLASS"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Local Variable ({0} in {1}) Naming Violation", "EIGHT", "toString"), "weirdStuff.weirdabstractclass"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Local Variable ({0} in {1}) Naming Violation", "PRIMENUMBER", "Cake"), "weirdStuff.WEIRD_CLASS")
        );

        assertEquals(exp.size(), out.size());
        for (Message m : out) {
            if (exp.contains(m)) {
                assertTrue(exp.contains(m));
            } else {
                assertEquals(null, m);
            }
        }
	}

    @Test
    public void testAnyAndLength() {
        Map<String, Object> config = new HashMap<String, Object>(Map.of( // why is making a map so hard
            "convPackage", "af",
            "convClass", "ANY",
            "convInterface", "ANY",
            "convAbstract", "ewr",
            "convEnum", "ANY",
            "convField", "",
            "convMethod", "",
            "convConstant", "",
            "convEnumConstant", "",
            "convLocalVar", "3"));
        config.put("convMethodParam", "e");
        config.put("convAllowEmptyPackage", true);
        config.put("convMaxLength", 13);
        Set<Message> out = ncc.run(classes, new Configuration(config));
        assertEquals(Set.of(
            new Message(MessageLevel.WARNING, MessageFormat.format("Package ({0}) Name exceeds {1} characters", "normalconventions", 13)),
            new Message(MessageLevel.WARNING, MessageFormat.format("Class Name exceeds {0} characters", 13), "weirdStuff.weirdabstractclass"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Class Name exceeds {0} characters", 13), "normalconventions.NormalAbstractClass"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Class Name exceeds {0} characters", 13), "normalconventions.NormalClassName"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Class Name exceeds {0} characters", 13), "normalconventions.NormalInterface"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Class Name exceeds {0} characters", 13), "weirdStuff.weird_interface"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Local Variable or Method Param ({0} in {1}) name exceeds {2} characters", "C_A_K_E_flavor", "Cake", 13), "weirdStuff.WEIRD_CLASS")
        ), out);

    }


    @Test
    public void testOtherConventions() {
        Map<String, Object> config = new HashMap<String, Object>(Map.of(
            "convPackage", "ANY",
            "convClass", "UPPERCASE",
            "convInterface", "lower_case",
            "convAbstract", "lower_case",
            "convEnum", "ANY",
            "convField", "ANY",
            "convMethod", "ANY",
            "convConstant", "ANY",
            "convEnumConstant", "UPPERCASE",
            "convLocalVar", "ANY"));
        config.put("convMethodParam", "ANY");
        config.put("convAllowEmptyPackage", true);
        config.put("convMaxLength", -1);
        Set<Message> out = ncc.run(classes, new Configuration(config));
        Set<Message> exp = Set.of(
            new Message(MessageLevel.WARNING, "Class Naming Violation", "weirdStuff.WEIRD_CLASS"),
            new Message(MessageLevel.WARNING, "Class Naming Violation", "normalconventions.NormalClassName"),
            new Message(MessageLevel.WARNING, "Class Naming Violation", "Main"),
            new Message(MessageLevel.WARNING, "Interface Naming Violation", "normalconventions.NormalInterface"),
            new Message(MessageLevel.WARNING, "Abstract Class Naming Violation", "normalconventions.NormalAbstractClass"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", "one"), "weirdStuff.weirdEnum"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", "two"), "weirdStuff.weirdEnum"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", "three"), "weirdStuff.weirdEnum"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", "four"), "weirdStuff.weirdEnum"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", "five"), "weirdStuff.weirdEnum"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", "six"), "weirdStuff.weirdEnum"),
            new Message(MessageLevel.WARNING, MessageFormat.format("Enum Constant ({0}) Naming Violation", "seven"), "weirdStuff.weirdEnum")

        );
        assertEquals(exp, out);

    }




}
