package domain.javadata;

import java.util.Objects;

/**
 * Represents a variable with its name and fully qualified type name.
 * Immutable. Just a data class. You can compare them with equals().
 */
public final class VariableData {
	/**
	 * The variable's name, or null if it is unknown
	 */
	public final String name;

	/**
	 * The full name of the variable's type
	 */
	public final String typeFullName;

	public VariableData(String name, String typeFullName) {
		this.name = name;
		this.typeFullName = typeFullName;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {return true;}
		if (obj == null || this.getClass() != obj.getClass()) {return false;}
		VariableData other = (VariableData)obj;
		return this.name.equals(other.name) && this.typeFullName.equals(other.typeFullName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.typeFullName);
	}
}
