package datasource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The linter configuration read from the JSON file. Immutable.
 */
public final class Configuration {
	private final Map<String, Object> data;

	/**
	 * Note: Do not mutate any of data's value objects after construction.
	 */
	public Configuration(Map<String, Object> data) {
		this.data = new HashMap<String, Object>(data);
	}

	public boolean getBoolean(String key) throws IllegalArgumentException, ClassCastException {
		this.checkKey(key);
		return (boolean)this.data.get(key);
	}

	public int getInt(String key) throws IllegalArgumentException, ClassCastException {
		this.checkKey(key);
		return (int)this.data.get(key);
	}

	public String getString(String key) throws IllegalArgumentException, ClassCastException {
		this.checkKey(key);
		return (String)this.data.get(key);
	}

	@SuppressWarnings("unchecked")
	public List<Boolean> getListOfBoolean(String key) throws IllegalArgumentException, ClassCastException {
		this.checkKey(key);
		return (List<Boolean>)this.data.get(key);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getListOfInt(String key) throws IllegalArgumentException, ClassCastException {
		this.checkKey(key);
		return (List<Integer>)this.data.get(key);
	}

	@SuppressWarnings("unchecked")
	public List<String> getListOfString(String key) throws IllegalArgumentException, ClassCastException {
		this.checkKey(key);
		return (List<String>)this.data.get(key);
	}

	public Boolean getBoolean(String key, Boolean fallback) {
		try {
			return this.getBoolean(key);
		} catch (IllegalArgumentException | ClassCastException ex) {
			return fallback;
		}
	}

	public Integer getInt(String key, Integer fallback) {
		try {
			return this.getInt(key);
		} catch (IllegalArgumentException | ClassCastException ex) {
			return fallback;
		}
	}

	public String getString(String key, String fallback) {
		try {
			return this.getString(key);
		} catch (IllegalArgumentException | ClassCastException ex) {
			return fallback;
		}
	}

	public List<Boolean> getListOfBoolean(String key, List<Boolean> fallback) {
		try {
			return this.getListOfBoolean(key);
		} catch (IllegalArgumentException | ClassCastException ex) {
			return fallback;
		}
	}

	public List<Integer> getListOfInt(String key, List<Integer> fallback) {
		try {
			return this.getListOfInt(key);
		} catch (IllegalArgumentException | ClassCastException ex) {
			return fallback;
		}
	}

	public List<String> getListOfString(String key, List<String> fallback) {
		try {
			return this.getListOfString(key);
		} catch (IllegalArgumentException | ClassCastException ex) {
			return fallback;
		}
	}

	private void checkKey(String key) throws IllegalArgumentException {
		if (!this.data.containsKey(key)) {throw new IllegalArgumentException("Key not found in configuration: " + key);}
	}

	public Configuration applyChanges(Map<String, Object> changes) {
		Map<String, Object> newData = new HashMap<String, Object>(this.data);
		for (Map.Entry<String, Object> entry : changes.entrySet()) {
			if (entry.getValue() == null) {
				newData.remove(entry.getKey());
			} else {
				newData.put(entry.getKey(), entry.getValue());
			}
		}
		return new Configuration(newData);
	}

	Map<String, Object> getData() {
		return Collections.unmodifiableMap(this.data);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {return true;}
		if (!(obj instanceof Configuration)) {return false;}
		Configuration other = (Configuration)obj;
		return this.data.equals(other.data);
	}

	@Override
	public int hashCode() {
		return this.data.hashCode();
	}
}
