package domain.checks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import datasource.Configuration;
import domain.Check;
import domain.Message;
import domain.MessageLevel;
import domain.javadata.ClassData;
import domain.javadata.ClassDataCollection;
import domain.javadata.ClassReaderUtil;

public class MethodLengthCheckTest {
	private static final String STRING_RESOURCE_PATH = "java/lang/String.class";

	private static final Configuration CONFIG_300 = new Configuration(Map.of(
		"maxMethodLengthInstrs", 300
	));
	private static final Configuration CONFIG_150 = new Configuration(Map.of(
		"maxMethodLengthInstrs", 150
	));
	private static final Configuration CONFIG_EMPTY = new Configuration(Map.of());

	private ClassData classData;

	@BeforeEach
	public void setup() throws IOException {
		byte[] javaBytecode;
		try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(STRING_RESOURCE_PATH)) {
			javaBytecode = in.readAllBytes();
		}
		this.classData = ClassReaderUtil.read(javaBytecode);
	}

	@Test
	public void testAllPass() {
		Check check = new MethodLengthCheck();
		Set<Message> msgs = check.run(new ClassDataCollection(this.classData), CONFIG_300);

		assertEquals(Set.of(), msgs);
	}

	@Test
	public void testWarnings() {
		Check check = new MethodLengthCheck();
		Set<Message> msgs = check.run(new ClassDataCollection(this.classData), CONFIG_150);

		Set<String> classFullNames = Set.of(this.classData.getFullName());
		assertEquals(Set.of(
			new Message(MessageLevel.WARNING, "Method \"split\" is too long (248 instrs, should be <= 150)", classFullNames),
			new Message(MessageLevel.WARNING, "Method \"repeat\" is too long (171 instrs, should be <= 150)", classFullNames),
			new Message(MessageLevel.WARNING, "Method \"regionMatches\" is too long (157 instrs, should be <= 150)", classFullNames)
		), msgs);
	}

	@Test
	public void testNoMaxMethodLength() {
		Check check = new MethodLengthCheck();
		Set<Message> msgs = check.run(new ClassDataCollection(this.classData), CONFIG_EMPTY);

		assertEquals(Set.of(
			new Message(MessageLevel.INFO, "Config property \"maxMethodLengthInstrs\" not found; skipping check")
		), msgs);
	}
}
