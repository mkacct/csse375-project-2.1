package datasource.configspec;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Loads the GUI configuration specification from the JSON file.
 */
public class JsonFileConfigSpecLoader implements ConfigSpecLoader {
	public static final String CONFIG_SPEC_RES_PATH = "/config-spec.json";

	private final String configSpecResPath;

	public JsonFileConfigSpecLoader(String configSpecPath) {
		this.configSpecResPath = configSpecPath;
	}

	@Override
	public ConfigSpec loadConfigSpec() {
		InputStream inputStream = this.getClass().getResourceAsStream(configSpecResPath);
		String json;
		try {
			json = new String(inputStream.readAllBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		JSONObject jsonObject = new JSONObject(json);
		Map<String, List<String>> selects = readSelects(jsonObject.getJSONObject("selects"));
		List<ConfigSpec.Section> sections = readSections(jsonObject.getJSONArray("sections"), selects);
		return new ConfigSpec(sections);
	}

	private Map<String, List<String>> readSelects(JSONObject selectsJson) {
		Map<String, List<String>> selects = new HashMap<String, List<String>>();
		for (String key : selectsJson.keySet()) {
			JSONArray optionsJson = selectsJson.getJSONArray(key);
			List<String> options = new ArrayList<String>();
			for (int i = 0; i < optionsJson.length(); i++) {
				options.add(optionsJson.getString(i));
			}
			selects.put(key, options);
		}
		return selects;
	}

	private List<ConfigSpec.Section> readSections(JSONArray sectionsJson, Map<String, List<String>> selects) {
		List<ConfigSpec.Section> sections = new ArrayList<ConfigSpec.Section>();
		for (int i = 0; i < sectionsJson.length(); i++) {
			JSONObject sectionJson = sectionsJson.getJSONObject(i);
			sections.add(new ConfigSpec.Section(
				sectionJson.getString("title"),
				getStringOrNull(sectionJson, "checkName"),
				getStringOrNull(sectionJson, "entityTypeOverride"),
				readSettings(sectionJson, selects)
			));
		}
		return sections;
	}

	private List<ConfigSpec.Setting> readSettings(JSONObject sectionJson, Map<String, List<String>> selects) {
		JSONArray settingsJson;
		try {
			settingsJson = sectionJson.getJSONArray("settings");
		} catch (JSONException e) {
			return null;
		}
		List<ConfigSpec.Setting> settings = new ArrayList<ConfigSpec.Setting>();
		for (int i = 0; i < settingsJson.length(); i++) {
			JSONObject settingJson = settingsJson.getJSONObject(i);
			settings.add(readSetting(settingJson, selects));
		}
		return settings;
	}

	private ConfigSpec.Setting readSetting(JSONObject settingJson, Map<String, List<String>> selects) {
		return new ConfigSpec.Setting(
			settingJson.getString("name"),
			ConfigSpec.Setting.Type.parse(settingJson.getString("type")),
			getStringOrNull(settingJson, "desc"),
			selects.get(getStringOrNull(settingJson, "select"))
		);
	}

	private static String getStringOrNull(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getString(key);
		} catch (JSONException e) {
			return null;
		}
	}
}
