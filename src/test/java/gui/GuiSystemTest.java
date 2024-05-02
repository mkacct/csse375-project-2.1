package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import datasource.ConfigRW;
import datasource.Configuration;
import domain.Check;
import domain.Message;
import domain.MessageLevel;
import domain.checks.MethodLengthCheck;
import domain.checks.NamingConventionsCheck;
import domain.checks.NoGlobalVariablesCheck;
import domain.checks.ParameterCountCheck;

public class GuiSystemTest {
	private static final String TEST_RESOURCES_DIR_PATH = "src/test/resources/system-test";
	private static final String CLASS_DIR_PATH = TEST_RESOURCES_DIR_PATH + "/classes";

	private static final Check[] CHECKS = new Check[] {
		new NamingConventionsCheck(),
		new MethodLengthCheck(),
		new ParameterCountCheck(),
		new NoGlobalVariablesCheck()
	};

	private static final Configuration CONFIG = new Configuration(Map.of(
		"guiAppTargetPath", CLASS_DIR_PATH,
		"convConstant", "camelCase",
		"maxMethodLengthInstrs", 20,
		"maxNumParameters", 3
	));

	private static FakeConfigRW createFakeConfigRW(Configuration config) {
		return new FakeConfigRW() {
			private Configuration currentConfig = config;
			private Configuration lastSavedConfig = null;

			@Override
			public Configuration loadConfig() throws IOException {
				return this.currentConfig;
			}

			@Override
			public void saveConfig(Configuration config) {
				this.currentConfig = config;
				this.lastSavedConfig = config;
			}

			@Override
			public Configuration retrieveLastSavedConfig() {
				Configuration savedConfig = this.lastSavedConfig;
				this.lastSavedConfig = null;
				return savedConfig;
			}
		};
	}

	private static FakeConfigRW createNonexistentConfigRW() {
		return new FakeConfigRW() {
			private boolean triedToLoad = false;

			@Override
			public boolean sourceExists() {return false;}

			@Override
			public Configuration loadConfig() throws IOException {
				triedToLoad = true;
				throw new IOException("don't even try");
			}

			@Override
			public boolean didTryToLoad() {return triedToLoad;}
		};
	}

	private static FakeConfigRW createBadConfigRW() {
		return new FakeConfigRW() {
			@Override
			public Configuration loadConfig() throws IOException {
				throw new IOException("lol nope");
			}
		};
	}

	@Test
	public void testDefaultInitialState() {
		App app = new App(CHECKS, null);

		assertNull(app.retrieveConfigLoadEx());
		assertFalse(app.canRunNow());
		assertEquals("", app.getTargetPath());
		assertNoResults(app);
	}

	@Test
	public void testWithConfigInitialState() {
		FakeConfigRW configRW = createFakeConfigRW(CONFIG);
		App app = new App(CHECKS, configRW);

		assertNull(app.retrieveConfigLoadEx());
		assertTrue(app.canRunNow());
		assertEquals(CLASS_DIR_PATH, app.getTargetPath());
		assertNoResults(app);
		assertNull(configRW.retrieveLastSavedConfig());
	}

	@Test
	public void testWithNonexistentConfigInitialState() {
		FakeConfigRW configRW = createNonexistentConfigRW();
		App app = new App(CHECKS, configRW);

		assertNull(app.retrieveConfigLoadEx());
		assertFalse(configRW.didTryToLoad());
		assertFalse(app.canRunNow());
		assertEquals("", app.getTargetPath());
		assertNoResults(app);
	}

	@Test
	public void testWithBadConfigInitialState() {
		FakeConfigRW configRW = createBadConfigRW();
		App app = new App(CHECKS, configRW);

		assertInstanceOf(IOException.class, app.retrieveConfigLoadEx());
		assertFalse(app.canRunNow());
		assertEquals("", app.getTargetPath());
		assertNoResults(app);
	}

	@Test
	public void testRunInBadState() {
		App app = new App(CHECKS, null);
		ReloadCounter reloadCounter = new ReloadCounter();
		app.addReloader(reloadCounter);

		assertFalse(app.canRunNow());
		assertThrows(IllegalStateException.class, () -> {
			app.runChecks();
		});
		assertEquals(0, reloadCounter.getReloadCount());
	}

	@Test
	public void testRunWithBadPath() {
		App app = new App(CHECKS, null);
		ReloadCounter reloadCounter = new ReloadCounter();
		app.addReloader(reloadCounter);
		app.setTargetPath("this is not a real directory");
		assertEquals(1, reloadCounter.getReloadCount());

		assertTrue(app.canRunNow());
		assertThrows(IOException.class, () -> {
			app.runChecks();
		});
		assertEquals(2, reloadCounter.getReloadCount());

		assertNoResults(app);
		assertTrue(app.canRunNow());
	}

	@Test
	public void testRunDefault() throws IOException {
		App app = new App(CHECKS, null);
		ReloadCounter reloadCounter = new ReloadCounter();
		app.addReloader(reloadCounter);
		app.setTargetPath(CLASS_DIR_PATH);
		assertEquals(1, reloadCounter.getReloadCount());

		assertTrue(app.canRunNow());
		app.runChecks();
		assertEquals(2, reloadCounter.getReloadCount());

		assertTrue(app.hasResults());
		assertEquals(CHECKS.length, app.getNumChecksRun());
		assertEquals(Map.of(
			MessageLevel.ERROR, 1,
			MessageLevel.WARNING, 1,
			MessageLevel.INFO, 2
		), app.getMessageTotals());
		assertEquals(Map.of(
			"namingConventions", Set.of(
				new Message(MessageLevel.WARNING, "Constant (badName) Naming Violation", Set.of("otherpkg.Baz"))
			),
			"methodLength", Set.of(
				new Message(MessageLevel.INFO, "Config property \"maxMethodLengthInstrs\" not found; skipping check")
			),
			"parameterCount", Set.of(
				new Message(MessageLevel.INFO, "Config property \"maxNumParameters\" not found; skipping check")
			),
			"noGlobalVariables", Set.of(
				new Message(MessageLevel.ERROR, "Field \"notGreat\" is a global variable", Set.of("otherpkg.Baz"))
			)
		), unorderCheckResults(app.getCheckResults()));
		assertTrue(app.canRunNow());
	}

	@Test
	public void testRunWithConfig() throws IOException {
		FakeConfigRW configRW = createFakeConfigRW(CONFIG);
		App app = new App(CHECKS, configRW);
		ReloadCounter reloadCounter = new ReloadCounter();
		app.addReloader(reloadCounter);

		assertTrue(app.canRunNow());
		app.runChecks();
		assertEquals(1, reloadCounter.getReloadCount());

		assertTrue(app.hasResults());
		assertEquals(CHECKS.length, app.getNumChecksRun());
		assertEquals(Map.of(
			MessageLevel.ERROR, 1,
			MessageLevel.WARNING, 3,
			MessageLevel.INFO, 0
		), app.getMessageTotals());
		assertEquals(Map.of(
			"namingConventions", Set.of(
				new Message(MessageLevel.WARNING, "Constant (DETECT_ME) Naming Violation", Set.of("domain.Foo"))
			),
			"methodLength", Set.of(
				new Message(MessageLevel.WARNING, "Method \"badlyPrint16Lines\" is too long (84 instrs, should be <= 20)", Set.of("otherpkg.Bar"))
			),
			"parameterCount", Set.of(
				new Message(MessageLevel.WARNING, "Method \"takeTooManyArgs\" has too many params (5 params, should be <= 3)", Set.of("domain.Foo"))
			),
			"noGlobalVariables", Set.of(
				new Message(MessageLevel.ERROR, "Field \"notGreat\" is a global variable", Set.of("otherpkg.Baz"))
			)
		), unorderCheckResults(app.getCheckResults()));
		assertTrue(app.canRunNow());
		assertNull(configRW.retrieveLastSavedConfig());
	}

	@Test
	public void testSaveTargetPathToConfig() {
		FakeConfigRW configRW = createFakeConfigRW(CONFIG);
		App app = new App(CHECKS, configRW);
		ReloadCounter reloadCounter = new ReloadCounter();
		app.addReloader(reloadCounter);

		assertTrue(app.canRunNow());
		assertEquals(CLASS_DIR_PATH, app.getTargetPath());
		assertNull(configRW.retrieveLastSavedConfig());
		app.setTargetPath("asdf");
		Configuration savedConfig = configRW.retrieveLastSavedConfig();
		assertNotNull(savedConfig);
		assertEquals("asdf", savedConfig.getString("guiAppTargetPath"));
		assertEquals("asdf", app.getTargetPath());

		assertNoResults(app);
		assertTrue(app.canRunNow());
		assertNull(configRW.retrieveLastSavedConfig());
	}

	private static void assertNoResults(App app) {
		assertFalse(app.hasResults());
		assertEquals(0, app.getNumChecksRun());
		assertNull(app.getMessageTotals());
		assertNull(app.getCheckResults());
	}

	private static Map<String, Set<Message>> unorderCheckResults(List<App.CheckResults> checkResults) {
		Map<String, Set<Message>> unordered = new HashMap<>();
		for (App.CheckResults checkResult : checkResults) {
			unordered.put(checkResult.checkName, Set.copyOf(checkResult.getMessages()));
		}
		return unordered;
	}

	private static interface FakeConfigRW extends ConfigRW {
		@Override
		default boolean sourceExists() {return true;}

		default boolean didTryToLoad() {throw new UnsupportedOperationException();}

		default Configuration retrieveLastSavedConfig() {throw new UnsupportedOperationException();}
	}

	private static class ReloadCounter implements Reloadable {
		private int reloadCount = 0;

		@Override
		public void reload() {reloadCount++;}

		int getReloadCount() {return reloadCount;}
	}
}