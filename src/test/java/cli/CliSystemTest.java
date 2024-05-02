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

import datasource.ConfigRW;
import datasource.Configuration;
import datasource.DirLoader;
import domain.Check;
import domain.checks.MethodLengthCheck;
import domain.checks.NamingConventionsCheck;
import domain.checks.NoGlobalVariablesCheck;
import domain.checks.ParameterCountCheck;

public class CliSystemTest {
	private static final String TEST_RESOURCES_DIR_PATH = "src/test/resources/system-test";
	private static final String CLASS_DIR_PATH = TEST_RESOURCES_DIR_PATH + "/classes";
	private static final String EXPECTED_OUTPUT_DIR_PATH = TEST_RESOURCES_DIR_PATH + "/cli-expected-output";

	private static final Check[] CHECKS = new Check[] {
		new NamingConventionsCheck(),
		new MethodLengthCheck(),
		new ParameterCountCheck(),
		new NoGlobalVariablesCheck()
	};

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
		ConfigRW fakeConfigLoader = new ConfigRW() {
			@Override
			public boolean sourceExists() {return true;}

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

	private static String readExpectedOutput(String filename) throws IOException {
		File file = new File(EXPECTED_OUTPUT_DIR_PATH + "/" + filename);
		try (FileInputStream in = new FileInputStream(file)) {
			return new String(in.readAllBytes());
		}
	}
}
