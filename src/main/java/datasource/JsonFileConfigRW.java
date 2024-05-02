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
	private static final int JSON_INDENT = 4;

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

	@Override
	public void saveConfig(Configuration config) throws IOException {
		String output;
		try {
			JSONObject jsonObject = new JSONObject(config.getData());
			output = jsonObject.toString(JSON_INDENT);
		} catch (JSONException | NullPointerException ex) {
			throw new IOException(ex.getMessage());
		}
		Files.writeString(Path.of(this.path), output);
	}
}
