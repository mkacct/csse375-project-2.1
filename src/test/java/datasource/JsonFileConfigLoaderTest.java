package datasource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test JsonFileConfigLoader, as well as the Configuration class itself.
 */
public class JsonFileConfigLoaderTest {
	private static final String EXAMPLE_JSON_PATH = "src/test/resources/example.json";

	@Test
	public void testBasicUsage() throws IOException {
		ConfigLoader loader = new JsonFileConfigLoader(EXAMPLE_JSON_PATH);
		Configuration config = loader.loadConfig();

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
	public void testArrays() throws IOException {
		ConfigLoader loader = new JsonFileConfigLoader(EXAMPLE_JSON_PATH);
		Configuration config = loader.loadConfig();

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
}
