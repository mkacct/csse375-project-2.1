package datasource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Test JsonFileConfigLoader, as well as the Configuration class itself.
 */
public class JsonFileConfigLoaderTest {
	private static final String EXAMPLE_JSON_PATH = "src/test/resources/example.json";

	@Test
	public void test() throws IOException {
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
}
