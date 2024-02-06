package domain;

import java.util.Map;

/**
 * The linter configuration read from the JSON file.
 */
public final class Configuration {
	private final Map<String, Object> data;

	public Configuration(Map<String, Object> data) {
		this.data = data;
	}

	public boolean getBoolean(String key) {
		if (!data.containsKey(key)) {throw new IllegalArgumentException("Key not found in configuration: " + key);}
		return (boolean)data.get(key);
	}

	public int getInt(String key) {
		if (!data.containsKey(key)) {throw new IllegalArgumentException("Key not found in configuration: " + key);}
		return (int)data.get(key);
	}

	public String getString(String key) {
		if (!data.containsKey(key)) {throw new IllegalArgumentException("Key not found in configuration: " + key);}
		return (String)data.get(key);
	}

	public boolean getBoolean(String key, boolean fallback) {
		try {
			return this.getBoolean(key);
		} catch (IllegalArgumentException ex) {
			return fallback;
		}
	}

	public int getInt(String key, int fallback) {
		try {
			return this.getInt(key);
		} catch (NullPointerException ex) {
			return fallback;
		}
	}

	public String getString(String key, String fallback) {
		try {
			return this.getString(key);
		} catch (NullPointerException ex) {
			return fallback;
		}
	}
}
