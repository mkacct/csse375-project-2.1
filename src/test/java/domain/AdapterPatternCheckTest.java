package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import datasource.Configuration;
import datasource.DirLoader;
import datasource.FilesLoader;
import domain.javadata.ClassData;
import domain.javadata.ClassReaderUtil;

public class AdapterPatternCheckTest {
private static final String CLASS_DIR_PATH = "src/test/resources/adapter-pattern-check-test-classes";

	private static final Configuration CONFIG_EMPTY = new Configuration(Map.of());

	private Map<String, ClassData> classes;

	@BeforeEach
	public void setup() throws IOException {
		FilesLoader loader = new DirLoader(CLASS_DIR_PATH);
		Set<byte[]> bytecodes = loader.loadFiles("class");
		this.classes = new HashMap<String, ClassData>();
		for (byte[] bytecode : bytecodes) {
			ClassData classData = ClassReaderUtil.read(bytecode);
			this.classes.put(classData.getFullName(), classData);
		}
	}

	@Test
	public void testDefault() {
		Check check = new AdapterPatternCheck();
		Set<Message> msgs = check.run(this.classes, CONFIG_EMPTY);
		assertEquals(Set.of(
			new Message(MessageLevel.WARNING, "Adapter class does not implement any interface", "domain.NonsenseAdapter"),
			new Message(MessageLevel.WARNING, "Field \"bad\" is of adapter type \"domain.ActualAdapter\"", "domain.Main"),
			new Message(MessageLevel.WARNING, "Method \"badMethod\" has adapter return type \"domain.NonsenseAdapter\"", "domain.Foo"),
			new Message(MessageLevel.WARNING, "Method \"badMethod\" has parameter \"adapter\" of adapter type \"domain.ActualAdapter\"", "domain.Foo")
		), msgs);
	}

	private static final Configuration CONFIG_ADAPTER_FOO = new Configuration(Map.of(
		"adapterClassNameRegex", "^Foo$"
	));

	@Test
	public void testDifferentAdapter() { // Now we pretend Foo is the adapter
		Check check = new AdapterPatternCheck();
		Set<Message> msgs = check.run(this.classes, CONFIG_ADAPTER_FOO);
		assertEquals(Set.of(
			new Message(MessageLevel.WARNING, "Adapter class does not implement any interface", "domain.Foo")
		), msgs);
	}
}
