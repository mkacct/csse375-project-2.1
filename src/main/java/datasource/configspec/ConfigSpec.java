package datasource.configspec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The configuration specification, for generating GUI settings.
 */
public final class ConfigSpec {
	private final List<Section> sections;

	ConfigSpec(List<Section> sections) {
		if (sections == null) {throw new NullPointerException("sections");}
		this.sections = new ArrayList<Section>(sections);
	}

	public List<Section> getSections() {
		return Collections.unmodifiableList(this.sections);
	}

	public static final class Section {
		private static final String DEFAULT_ENTITY_TYPE = "check";

		public final String title;

		private final String checkName;
		private final String entityType;
		private final List<Setting> settings;

		Section(String title, String checkName, String entityTypeOverride, List<Setting> settings) {
			if (title == null) {throw new NullPointerException("title");}
			this.title = title;
			this.checkName = checkName;
			this.entityType = (checkName != null) ? ((entityTypeOverride != null) ? entityTypeOverride : DEFAULT_ENTITY_TYPE) : null;
			this.settings = (settings != null) ? new ArrayList<Setting>(settings) : null;
		}

		public boolean representsCheck() {return this.checkName != null;}

		public String getCheckName() {
			this.validateRepresentsCheck();
			return this.checkName;
		}

		public String getEntityType() {
			this.validateRepresentsCheck();
			return this.entityType;
		}

		private void validateRepresentsCheck() {
			if (this.checkName == null) {throw new IllegalStateException("Section isn't for a specific check");}
		}

		public List<Setting> getSettings() {
			if (this.settings == null) {return List.of();}
			return Collections.unmodifiableList(this.settings);
		}
	}

	public static final class Setting {
		public final String name;
		public final Type type;
		public final String desc;

		private final List<String> options;

		Setting(String name, Type type, String desc, List<String> options) {
			if (name == null) {throw new NullPointerException("name");}
			if (type == null) {throw new NullPointerException("type");}
			this.name = name;
			this.type = type;
			this.desc = desc;
			if (options != null) {
				this.validateCanHaveOptions();
				this.options = new ArrayList<String>(options);
			} else {
				this.options = null;
			}
		}

		public boolean hasStringOptions() {
			this.validateCanHaveOptions();
			return this.options != null;
		}

		public List<String> getStringOptions() {
			this.validateCanHaveOptions();
			if (this.options == null) {throw new IllegalStateException("Setting doesn't have string options");}
			return Collections.unmodifiableList(this.options);
		}

		private void validateCanHaveOptions() {
			if (this.type != Type.STRING) {throw new IllegalStateException("Only String type can have options");}
		}

		public static enum Type {
			BOOLEAN,
			INT,
			STRING,
			LIST_OF_BOOLEAN,
			LIST_OF_INT,
			LIST_OF_STRING;

			static Type parse(String typeName) {
				switch (typeName) {
					case "boolean": return BOOLEAN;
					case "int": return INT;
					case "String": return STRING;
					case "boolean[]": return LIST_OF_BOOLEAN;
					case "int[]": return LIST_OF_INT;
					case "String[]": return LIST_OF_STRING;
				}
				throw new IllegalArgumentException("No such setting type: " + typeName);
			}
		}
	}
}
