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
public class JsonFileConfigRW implements ConfigRW {
	private final String path;

	public JsonFileConfigRW(String path) {
		this.path = path;
	}

	@Override
	public boolean sourceExists() {
		return Files.isRegularFile(Path.of(this.path));
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
