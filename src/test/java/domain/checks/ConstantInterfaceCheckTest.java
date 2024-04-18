package domain.checks;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

public class ConstantInterfaceCheckTest {
	private static final String CLASS_DIR_PATH = "src/test/resources/constant-interface-check-test-classes";

	private static final Configuration CONFIG_EMPTY = new Configuration(Map.of());
	private static final Configuration CONFIG_ALLOW_MARKER_INTERFACES = new Configuration(Map.of(
		"allowMarkerInterfaces", true
	));

	private ClassDataCollection classes;

	@BeforeEach
	public void setup() throws IOException {
		FilesLoader loader = new DirLoader(CLASS_DIR_PATH);
		Set<byte[]> javaBytecodes = loader.loadFiles("class");
		this.classes = TestUtility.toClassDataCollection(javaBytecodes);
	}

	@Test
	public void testReportMarkerInterfaces() {
		Check check = new ConstantInterfaceCheck();
		Set<Message> msgs = check.run(this.classes, CONFIG_EMPTY);
		assertEquals(Set.of(
			new Message(MessageLevel.WARNING, "Empty interface", "domain.EmptyInterface"),
			new Message(MessageLevel.WARNING, "Constant interface", "domain.ConstantInterface")
		), msgs);
	}

	@Test
	public void testIgnoreMarkerInterfaces() {
		Check check = new ConstantInterfaceCheck();
		Set<Message> msgs = check.run(this.classes, CONFIG_ALLOW_MARKER_INTERFACES);
		assertEquals(Set.of(
			new Message(MessageLevel.WARNING, "Constant interface", "domain.ConstantInterface")
		), msgs);
	}
}
