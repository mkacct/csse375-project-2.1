package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import datasource.Configuration;
import datasource.DirLoader;
import datasource.FilesLoader;
import domain.javadata.ClassData;

public class ProgramToInterfaceNotImplementationCheckTest {
	private static final String CLASS_DIR_PATH = "src/test/resources/program-to-interface-not-implementation-check-test-classes";

	private static final Configuration CONFIG_EMPTY = new Configuration(Map.of());

	private Map<String, ClassData> classes;

	@BeforeEach
	public void setup() throws IOException {
		FilesLoader loader = new DirLoader(CLASS_DIR_PATH);
		Set<byte[]> javaBytecodes = loader.loadFiles("class");
		this.classes = TestUtility.getMap(javaBytecodes);
	}

	@Test
	public void testDefault() {
		Check check = new ProgramToInterfaceNotImplementationCheck();
		Set<Message> msgs = check.run(this.classes, CONFIG_EMPTY);
		assertEquals(Set.of(
			new Message(MessageLevel.WARNING, "Field \"uhOh\" is of type \"java.lang.Exception\"", "domain.Alpha"),
			new Message(MessageLevel.WARNING, "Method \"aBadArg\" has parameter \"gamma\" of type \"other.Gamma\"", "domain.Alpha"),
			new Message(MessageLevel.WARNING, "Method \"aBadReturn\" has return type \"javax.swing.JFrame\"", "domain.Alpha"),
			new Message(MessageLevel.WARNING, "Field \"gamma\" is of type \"other.Gamma\"", "domain.Beta")
		), msgs);
	}

	private static final Configuration CONFIG_ALLOW_GAMMA = new Configuration(Map.of(
		"allowedDependencies", List.of("other.Gamma")
	));

	@Test
	public void testAllowDependency() { // Gamma is an acceptable dependency
		Check check = new ProgramToInterfaceNotImplementationCheck();
		Set<Message> msgs = check.run(this.classes, CONFIG_ALLOW_GAMMA);
		assertEquals(Set.of(
			new Message(MessageLevel.WARNING, "Field \"uhOh\" is of type \"java.lang.Exception\"", "domain.Alpha"),
			new Message(MessageLevel.WARNING, "Method \"aBadReturn\" has return type \"javax.swing.JFrame\"", "domain.Alpha")
		), msgs);
	}

	private static final Configuration CONFIG_OTHER_DOMAIN = new Configuration(Map.of(
		"domainPackageName", "other"
	));

	@Test
	public void testDifferentDomainPkgName() { // other is the domain package now
		Check check = new ProgramToInterfaceNotImplementationCheck();
		Set<Message> msgs = check.run(this.classes, CONFIG_OTHER_DOMAIN);
		assertEquals(Set.of(
			new Message(MessageLevel.WARNING, "Field \"nothingWrong\" is of type \"javax.swing.JFrame\"", "other.Gamma")
		), msgs);
	}

	private static final Configuration CONFIG_ADAPTER_BETA = new Configuration(Map.of(
		"adapterClassNameRegex", "^Beta$"
	));

	@Test
	public void testExcludeAdapter() { // Beta is exempted because we are pretending it is an adapter
		Check check = new ProgramToInterfaceNotImplementationCheck();
		Set<Message> msgs = check.run(this.classes, CONFIG_ADAPTER_BETA);
		assertEquals(Set.of(
			new Message(MessageLevel.WARNING, "Field \"uhOh\" is of type \"java.lang.Exception\"", "domain.Alpha"),
			new Message(MessageLevel.WARNING, "Method \"aBadArg\" has parameter \"gamma\" of type \"other.Gamma\"", "domain.Alpha"),
			new Message(MessageLevel.WARNING, "Method \"aBadReturn\" has return type \"javax.swing.JFrame\"", "domain.Alpha")
		), msgs);
	}
}
