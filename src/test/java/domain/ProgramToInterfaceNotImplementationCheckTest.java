package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import datasource.Configuration;
import datasource.DirLoader;
import datasource.FilesLoader;
import domain.javadata.ClassData;
import domain.javadata.ClassReaderUtil;

public class ProgramToInterfaceNotImplementationCheckTest {
	private static final String CLASS_DIR_PATH = "src/test/resources/maddie-test-classes";

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
		Check check = new ProgramToInterfaceNotImplementationCheck();
		Set<Message> messages = check.run(this.classes, CONFIG_EMPTY);
		assertEquals(messages, Set.of(
			new Message(MessageLevel.WARNING, "Field \"uhOh\" is of type \"java.lang.Exception\"", "domain.Alpha"),
			new Message(MessageLevel.WARNING, "Method \"aBadArg\" has parameter \"gamma\" of type \"other.Gamma\"", "domain.Alpha"),
			new Message(MessageLevel.WARNING, "Method \"aBadReturn\" has return type \"javax.swing.JFrame\"", "domain.Alpha"),
			new Message(MessageLevel.WARNING, "Field \"gamma\" is of type \"other.Gamma\"", "domain.Beta")
		));
	}

	private static final Configuration CONFIG_ALLOW_GAMMA = new Configuration(Map.of(
		"allowedDependencies", List.of("other.Gamma")
	));

	@Test
	public void tesAllowDependency() { // Gamma is an acceptable dependency
		Check check = new ProgramToInterfaceNotImplementationCheck();
		Set<Message> messages = check.run(this.classes, CONFIG_ALLOW_GAMMA);
		assertEquals(messages, Set.of(
			new Message(MessageLevel.WARNING, "Field \"uhOh\" is of type \"java.lang.Exception\"", "domain.Alpha"),
			new Message(MessageLevel.WARNING, "Method \"aBadReturn\" has return type \"javax.swing.JFrame\"", "domain.Alpha")
		));
	}

	private static final Configuration CONFIG_OTHER_DOMAIN = new Configuration(Map.of(
		"domainPackageName", "other"
	));

	@Test
	public void testDifferentDomainPkgName() { // other is the domain package now
		Check check = new ProgramToInterfaceNotImplementationCheck();
		Set<Message> messages = check.run(this.classes, CONFIG_OTHER_DOMAIN);
		assertEquals(messages, Set.of(
			new Message(MessageLevel.WARNING, "Field \"nothingWrong\" is of type \"javax.swing.JFrame\"", "other.Gamma")
		));
	}

	private static final Configuration CONFIG_ADAPTER_BETA = new Configuration(Map.of(
		"adapterClassNameRegex", "^Beta$"
	));

	@Test
	public void testExcludeAdapter() { // Beta is exempted because we are pretending it is an adapter
		Check check = new ProgramToInterfaceNotImplementationCheck();
		Set<Message> messages = check.run(this.classes, CONFIG_ADAPTER_BETA);
		assertEquals(messages, Set.of(
			new Message(MessageLevel.WARNING, "Field \"uhOh\" is of type \"java.lang.Exception\"", "domain.Alpha"),
			new Message(MessageLevel.WARNING, "Method \"aBadArg\" has parameter \"gamma\" of type \"other.Gamma\"", "domain.Alpha"),
			new Message(MessageLevel.WARNING, "Method \"aBadReturn\" has return type \"javax.swing.JFrame\"", "domain.Alpha")
		));
	}
}
