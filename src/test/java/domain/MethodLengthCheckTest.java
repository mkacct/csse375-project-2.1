package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.ClassNodeAdapter;

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
		this.classData = new ClassNodeAdapter(javaBytecode);
	}

	@Test
	public void testAllPass() {
		Check check = new MethodLengthCheck();
		Set<Message> messages = check.run(Map.of(
			this.classData.getFullName(), this.classData
		), CONFIG_300);

		assertEquals(Set.of(), messages);
	}

	@Test
	public void testWarnings() {
		Check check = new MethodLengthCheck();
		Set<Message> messages = check.run(Map.of(
			this.classData.getFullName(), this.classData
		), CONFIG_150);

		Set<String> classFullNames = Set.of(this.classData.getFullName());
		assertEquals(Set.of(
			new Message(MessageLevel.WARNING, "Method \"split\" is too long (248 instrs, should be <= 150 instrs)", classFullNames),
			new Message(MessageLevel.WARNING, "Method \"repeat\" is too long (171 instrs, should be <= 150 instrs)", classFullNames),
			new Message(MessageLevel.WARNING, "Method \"regionMatches\" is too long (157 instrs, should be <= 150 instrs)", classFullNames)
		), messages);
	}

	@Test
	public void testNoMaxMethodLength() {
		Check check = new MethodLengthCheck();
		Set<Message> messages = check.run(Map.of(
			this.classData.getFullName(), this.classData
		), CONFIG_EMPTY);

		assertEquals(Set.of(
			new Message(MessageLevel.INFO, "Config property \"maxMethodLengthInstrs\" not found; skipping check")
		), messages);
	}
}
