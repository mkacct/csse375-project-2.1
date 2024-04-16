package cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.junit.jupiter.api.Test;

import datasource.ConfigLoader;
import datasource.Configuration;
import datasource.DirLoader;
import domain.Check;
import domain.MethodLengthCheck;
import domain.NamingConventionsCheck;
import domain.NoGlobalVariablesCheck;
import domain.ParameterCountCheck;

public class CliSystemTest {
	private static final String TEST_RESOURCES_DIR_PATH = "src/test/resources/cli-system-test";
	private static final String CLASS_DIR_PATH = TEST_RESOURCES_DIR_PATH + "/classes";
	private static final String EXPECTED_OUTPUT_DIR_PATH = TEST_RESOURCES_DIR_PATH + "/expected-output";

	private static final Check[] CHECKS = new Check[] {
		new NamingConventionsCheck(),
		new MethodLengthCheck(),
		new ParameterCountCheck(),
		new NoGlobalVariablesCheck()
	};

	private static String readExpectedOutput(String filename) throws IOException {
		File file = new File(EXPECTED_OUTPUT_DIR_PATH + "/" + filename);
		try (FileInputStream in = new FileInputStream(file)) {
			return new String(in.readAllBytes());
		}
	}

	@Test
	public void testDefault() throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ByteArrayOutputStream errStream = new ByteArrayOutputStream();
		App app = new App(new DirLoader(CLASS_DIR_PATH), null, new PrintStream(outStream), new PrintStream(errStream));
		boolean noErrors = app.run(CHECKS);
		assertFalse(noErrors);
		assertEquals(readExpectedOutput("expected-out-default.txt"), outStream.toString());
		assertEquals("", errStream.toString());
	}

	@Test
	public void testWithConfig() throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ByteArrayOutputStream errStream = new ByteArrayOutputStream();
		ConfigLoader fakeConfigLoader = new ConfigLoader() {
			@Override
			public Configuration loadConfig() throws IOException {
				return new Configuration(Map.of(
					"convConstant", "camelCase",
					"maxMethodLengthInstrs", 20,
					"maxNumParameters", 3
				));
			}
		};
		App app = new App(new DirLoader(CLASS_DIR_PATH), fakeConfigLoader, new PrintStream(outStream), new PrintStream(errStream));
		boolean noErrors = app.run(CHECKS);
		assertFalse(noErrors);
		assertEquals(readExpectedOutput("expected-out-with-config.txt"), outStream.toString());
		assertEquals("", errStream.toString());
	}
}