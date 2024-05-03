package datasource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

/**
 * Test JsonFileConfigLoader, as well as the Configuration class itself.
 */
public class JsonFileConfigRWTest {
	private static final String EXAMPLE_JSON_PATH = "src/test/resources/example.json";
	private static final String OUTPUT_JSON_PATH = "src/test/resources/output.json";

	@Test
	public void testWithBadPath() {
		ConfigRW rw = new JsonFileConfigRW("src/test/this-does-not-exist/lol.json");
		assertFalse(rw.sourceExists());
		assertThrows(IOException.class, () -> {
			rw.loadConfig();
		});
	}

	@Test
	public void testLoading() throws IOException {
		ConfigRW rw = new JsonFileConfigRW(EXAMPLE_JSON_PATH);
		assertTrue(rw.sourceExists());
		Configuration config = rw.loadConfig();

		assertEquals(true, config.getBoolean("isGood"));
		assertEquals(10, config.getInt("numTentacles"));
		assertEquals("Jason", config.getString("name"));

		assertEquals(true, config.getBoolean("isGood", false));
		assertEquals(10, config.getInt("numTentacles", 0));
		assertEquals("Jason", config.getString("name", "Billy"));

		assertThrows(IllegalArgumentException.class, () -> {
			config.getBoolean("nonexistentField");
		});

		assertEquals(false, config.getBoolean("nonexistentField", false));
		assertEquals(-2, config.getInt("nonexistentField", -2));
		assertEquals("Foo", config.getString("nonexistentField", "Foo"));
	}

	@Test
	public void testLoadingArrays() throws IOException {
		ConfigRW rw = new JsonFileConfigRW(EXAMPLE_JSON_PATH);
		assertTrue(rw.sourceExists());
		Configuration config = rw.loadConfig();

		assertEquals(
			List.of(true, false, true, true, false),
			config.getListOfBoolean("switchValues")
		);
		assertEquals(
			List.of(7, 13, 21, 42),
			config.getListOfInt("luckyNumbers")
		);
		assertEquals(
			List.of("pink", "green", "blue"),
			config.getListOfString("colors")
		);

		assertEquals(
			List.of(true, false, true, true, false),
			config.getListOfBoolean("switchValues", List.of())
		);
		assertEquals(
			List.of(7, 13, 21, 42),
			config.getListOfInt("luckyNumbers", List.of())
		);
		assertEquals(
			List.of("pink", "green", "blue"),
			config.getListOfString("colors", List.of())
		);

		assertThrows(IllegalArgumentException.class, () -> {
			config.getListOfString("nonexistentField");
		});

		assertEquals(
			List.of(false),
			config.getListOfBoolean("nonexistentField", List.of(false))
		);
		assertEquals(
			List.of(1, 2, 3),
			config.getListOfInt("nonexistentField", List.of(1, 2, 3))
		);
		assertEquals(
			List.of("A", "B"),
			config.getListOfString("nonexistentField", List.of("A", "B"))
		);
	}

	@Test
	public void testSaving() throws IOException {
		Files.deleteIfExists(Path.of(OUTPUT_JSON_PATH));
		assertFalse(Files.exists(Path.of(OUTPUT_JSON_PATH)));

		ConfigRW rw = new JsonFileConfigRW(OUTPUT_JSON_PATH);
		assertFalse(rw.sourceExists());

		Map<String, Object> expected = Map.of(
			"one", "foo",
			"two", 2,
			"three", true,
			"four", List.of(1, 2, 3, 6),
			"five", List.of("A", "B", "C"),
			"six", List.of(true, false, true)
		);
		Configuration config = new Configuration(expected);

		rw.saveConfig(config);

		assertTrue(rw.sourceExists());
		String json = String.join("\n", Files.readAllLines(Path.of(OUTPUT_JSON_PATH)));
		JSONObject jsonObject = new JSONObject(json);
		assertEquals(expected, jsonObject.toMap());
	}
}
