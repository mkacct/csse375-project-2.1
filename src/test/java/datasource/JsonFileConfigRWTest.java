package datasource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test JsonFileConfigLoader, as well as the Configuration class itself.
 */
public class JsonFileConfigRWTest {
	private static final String EXAMPLE_JSON_PATH = "src/test/resources/example.json";

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
}
