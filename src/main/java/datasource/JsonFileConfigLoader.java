package datasource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Loads user configuration from a JSON file.
 */
public class JsonFileConfigLoader implements ConfigLoader {
	private final String path;

	public JsonFileConfigLoader(String path) {
		this.path = path;
	}

	@Override
	public Configuration loadConfig() throws IOException {
		List<String> lines = Files.readAllLines(Path.of(this.path));
		String json = String.join("\n", lines);
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);
		} catch (JSONException ex) {
			throw new IOException(ex.getMessage());
		}
		return new Configuration(jsonObject.toMap());
	}
}
