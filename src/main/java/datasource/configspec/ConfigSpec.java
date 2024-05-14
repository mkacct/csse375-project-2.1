package datasource.configspec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import general.CmpUtil;

/**
 * The configuration specification, for generating GUI settings. Immutable.
 */
public final class ConfigSpec {
	private final List<Section> sections;

	public ConfigSpec(List<Section> sections) {
		if (sections == null) {throw new NullPointerException("sections");}
		this.sections = new ArrayList<Section>(sections);
	}

	public List<Section> getSections() {
		return Collections.unmodifiableList(this.sections);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {return true;}
		if (obj == null || !(obj instanceof ConfigSpec)) {return false;}
		ConfigSpec other = (ConfigSpec)obj;
		return CmpUtil.areEqual(this.sections, other.sections);
	}

	@Override
	public int hashCode() {
		return this.sections.hashCode();
	}

	public static final class Section {
		private static final String DEFAULT_ENTITY_TYPE = "check";

		public final String title;

		private final String checkName;
		private final String entityType;
		private final List<Setting> settings;
		private final String desc;

		public Section(String title, String checkName, String desc, String entityTypeOverride, List<Setting> settings) {
			if (title == null) {throw new NullPointerException("title");}
			this.title = title;
			this.checkName = checkName;
			this.desc = desc;
			this.entityType = (checkName != null) ? ((entityTypeOverride != null) ? entityTypeOverride : DEFAULT_ENTITY_TYPE) : null;
			this.settings = (settings != null) ? new ArrayList<Setting>(settings) : null;
		}

		public boolean representsCheck() {return this.checkName != null;}

		public String getCheckName() {
			this.validateRepresentsCheck();
			return this.checkName;
		}

		public String getDescName() {
			this.validateRepresentsCheck();
			return this.desc;
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

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {return true;}
			if (obj == null || !(obj instanceof Section)) {return false;}
			Section other = (Section)obj;
			return CmpUtil.areEqual(this.title, other.title)
				&& CmpUtil.areEqual(this.checkName, other.checkName)
				&& CmpUtil.areEqual(this.desc, other.desc)
				&& CmpUtil.areEqual(this.entityType, other.entityType)
				&& CmpUtil.areEqual(this.settings, other.settings);
		}

		@Override
		public int hashCode() {
			return this.title.hashCode();
		}
	}

	public static final class Setting {
		public final String name;
		public final Type type;
		public final String desc;

		private final List<String> options;

		public Setting(String name, Type type, String desc, List<String> options) {
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

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {return true;}
			if (obj == null || !(obj instanceof Setting)) {return false;}
			Setting other = (Setting)obj;
			return CmpUtil.areEqual(this.name, other.name)
				&& CmpUtil.areEqual(this.type, other.type)
				&& CmpUtil.areEqual(this.desc, other.desc)
				&& CmpUtil.areEqual(this.options, other.options);
		}

		@Override
		public int hashCode() {
			return this.name.hashCode();
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
