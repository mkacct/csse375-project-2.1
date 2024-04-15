package domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import datasource.Configuration;
import datasource.DirLoader;
import datasource.FilesLoader;
import domain.javadata.ClassDataCollection;

public class ImmutableExceptionsCheckTest {
private static final String CLASS_DIR_PATH = "src/test/resources/immutable-exceptions-check-test-classes";

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
		Check check = new ImmutableExceptionsCheck();
		Set<Message> msgs = check.run(this.classes, CONFIG_EMPTY);
		// order of multiple names in message is not guaranteed, so check both possibilities
		Message msg1possibility1 = new Message(MessageLevel.WARNING, "Exception class has non-final field(s): badField, thisOneToo", "domain.BadException");
		Message msg1possibility2 = new Message(MessageLevel.WARNING, "Exception class has non-final field(s): thisOneToo, badField", "domain.BadException");
		Message msg2 = new Message(MessageLevel.WARNING, "Exception class has non-final field(s): why", "domain.AlsoBadError");
		Set<Message> possibleExpected1 = Set.of(msg1possibility1, msg2);
		Set<Message> possibleExpected2 = Set.of(msg1possibility2, msg2);
		assertTrue(possibleExpected1.equals(msgs) || possibleExpected2.equals(msgs));
	}
}
