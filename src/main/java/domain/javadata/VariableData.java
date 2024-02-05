package domain.javadata;

import java.util.Objects;

public class VariableData {
	public final String name;
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
		return Objects.hash(name, typeFullName);
	}
}
