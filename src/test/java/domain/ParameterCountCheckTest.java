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
import domain.javadata.ClassDataCollection;
import domain.javadata.ClassReaderUtil;

public class ParameterCountCheckTest {
private static final String STRING_RESOURCE_PATH = "java/lang/String.class";

	private static final Configuration CONFIG_5 = new Configuration(Map.of(
		"maxNumParameters", 5
	));
	private static final Configuration CONFIG_4 = new Configuration(Map.of(
		"maxNumParameters", 4
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
		Check check = new ParameterCountCheck();
		Set<Message> msgs = check.run(new ClassDataCollection(this.classData), CONFIG_5);

		assertEquals(Set.of(), msgs);
	}

	@Test
	public void testWarnings() {
		Check check = new ParameterCountCheck();
		Set<Message> msgs = check.run(new ClassDataCollection(this.classData), CONFIG_4);

		Set<String> classFullNames = Set.of(this.classData.getFullName());
		assertEquals(Set.of(
			new Message(MessageLevel.WARNING, "Method \"regionMatches\" has too many params (5 params, should be <= 4)", classFullNames),
			new Message(MessageLevel.WARNING, "Method \"indexOf\" has too many params (5 params, should be <= 4)", classFullNames),
			new Message(MessageLevel.WARNING, "Method \"lastIndexOf\" has too many params (5 params, should be <= 4)", classFullNames)
		), msgs);
	}

	@Test
	public void testNoMaxNumParams() {
		Check check = new ParameterCountCheck();
		Set<Message> msgs = check.run(new ClassDataCollection(this.classData), CONFIG_EMPTY);

		assertEquals(Set.of(
			new Message(MessageLevel.INFO, "Config property \"maxNumParameters\" not found; skipping check")
		), msgs);
	}
}
