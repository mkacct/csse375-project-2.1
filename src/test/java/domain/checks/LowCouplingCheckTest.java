package domain.checks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
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

/**
 * Test Class Graph on
 */
public class LowCouplingCheckTest {
	private static final String STRING_RESOURCE_PATH = "src/test/resources/LowCouplingTest";
    Check lcc = new LowCouplingCheck();
    ClassDataCollection classes;

	@BeforeEach
	public void setup() throws IOException {
		FilesLoader fl = new DirLoader(STRING_RESOURCE_PATH);

        classes = TestUtility.toClassDataCollection(fl.loadFiles("class"));
	}



	@Test
	public void testDefaultConfig() {
		Set<Message> out = lcc.run(classes, new Configuration(Map.of()));
        Set<Message> exp = Set.of(
            new Message(MessageLevel.WARNING, "Cycle detected: complicatedyes.B --> complicatedyes.V --> complicatedyes.W --> complicatedyes.A --> complicatedyes.B"),
            new Message(MessageLevel.WARNING, "Cycle detected: simplecycle.C --> simplecycle.A --> simplecycle.B --> simplecycle.C"),
            new Message(MessageLevel.WARNING, "Cycle detected: complicatedyes.W --> complicatedyes.A --> complicatedyes.B --> complicatedyes.V --> complicatedyes.W"),
            new Message(MessageLevel.WARNING, "Cycle detected: complicatedyes.V --> complicatedyes.W --> complicatedyes.A --> complicatedyes.B --> complicatedyes.V"),
            new Message(MessageLevel.WARNING, "Cycle detected: simplecycle.A --> simplecycle.B --> simplecycle.C --> simplecycle.A"),
            new Message(MessageLevel.WARNING, "Cycle detected: simplecycle.B --> simplecycle.C --> simplecycle.A --> simplecycle.B"),
            new Message(MessageLevel.WARNING, "Cycle detected: complicatedyes.A --> complicatedyes.B --> complicatedyes.V --> complicatedyes.W --> complicatedyes.A"),
            new Message(MessageLevel.WARNING, "Cycle detected: complicatedyes.A --> complicatedyes.B --> complicatedyes.C --> complicatedyes.A"),
            new Message(MessageLevel.WARNING, "Cycle detected: complicatedyes.B --> complicatedyes.C --> complicatedyes.A --> complicatedyes.B"),
            new Message(MessageLevel.WARNING, "Cycle detected: complicatedyes.C --> complicatedyes.A --> complicatedyes.B --> complicatedyes.C")
        );

        assertEquals(exp, out);
        // assertEquals(exp.size(), out.size());
        // for (Message m : out) {
        //     if (exp.contains(m)) {
        //         assertTrue(exp.contains(m));
        //     } else {
        //         assertEquals(null, m);
        //     }
        // }
	}

    @Test
	public void testDegree() {
		Set<Message> out = lcc.run(classes, new Configuration(Map.of(
            "coupMaxInDegree", 2,
            "coupMaxOutDegree", 5,
            "coupCycles", false
        )));
        Set<Message> exp = Set.of(
            new Message(MessageLevel.WARNING, "Out Degree exceeds 5, is 6", "highdegrees.Bad"),
            new Message(MessageLevel.WARNING, "In Degree exceeds 2, is 3", "complicatedyes.A")
        );

        assertEquals(exp, out);
        // assertEquals(exp.size(), out.size());
        // for (Message m : out) {
        //     if (exp.contains(m)) {
        //         assertTrue(exp.contains(m));
        //     } else {
        //         assertEquals(null, m);
        //     }
        // }
	}

    @Test
	public void testIgnorepackage() {
		Set<Message> out = lcc.run(classes, new Configuration(Map.of(
            "coupMaxOutDegree", 5,
            "coupCycles", false,
            "coupIgnorePackage", "highdegrees"
        )));
        Set<Message> exp = Set.of(

        );

        assertEquals(exp, out);
        // assertEquals(exp.size(), out.size());
        // for (Message m : out) {
        //     if (exp.contains(m)) {
        //         assertTrue(exp.contains(m));
        //     } else {
        //         assertEquals(null, m);
        //     }
        // }
	}

    @Test
	public void testSelfCycle() {
		Set<Message> out = lcc.run(classes, new Configuration(Map.of(
            "coupIgnoreSelfCycles", false
        )));
        assertTrue(out.contains(new Message(MessageLevel.WARNING, "Cycle detected: highdegrees.Bad --> highdegrees.Bad")));
        // assertEquals(exp.size(), out.size());
        // for (Message m : out) {
        //     if (exp.contains(m)) {
        //         assertTrue(exp.contains(m));
        //     } else {
        //         assertEquals(null, m);
        //     }
        // }
	}



}
