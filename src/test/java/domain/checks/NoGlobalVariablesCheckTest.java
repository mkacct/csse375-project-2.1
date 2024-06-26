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

public class NoGlobalVariablesCheckTest {
	private static final String CLASS_DIR_PATH = "src/test/resources/no-global-variables-check-test-classes";

	private static final Configuration CONFIG_EMPTY = new Configuration(Map.of());

	private ClassDataCollection classes;

	@BeforeEach
	public void setup() throws IOException {
		FilesLoader loader = new DirLoader(CLASS_DIR_PATH);
		Set<byte[]> javaBytecodes = loader.loadFiles("class");
		this.classes = TestUtility.toClassDataCollection(javaBytecodes);
	}

	@Test
	public void test() {
		Check check = new NoGlobalVariablesCheck();
		Set<Message> msgs = check.run(this.classes, CONFIG_EMPTY);
		assertEquals(Set.of(
			new Message(MessageLevel.ERROR, "Field \"badbad\" is a global variable", "domain.Foo")
		), msgs);
	}
}
