package domain;

import java.util.Set;

import domain.javadata.AccessModifier;
import domain.javadata.ClassData;
import domain.javadata.ClassType;
import domain.javadata.FieldData;
import domain.javadata.MethodData;

class StubClassData implements ClassData {
	private final String fullName;

	public StubClassData(String fullName) {
		this.fullName = fullName;
	}

	@Override
	public String getFullName() {
		return this.fullName;
	}

	@Override
	public String getSimpleName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPackageName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public AccessModifier getAccessModifier() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ClassType getClassType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAbstract() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isStatic() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isFinal() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSuperFullName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getInterfaceFullNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<FieldData> getFields() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<MethodData> getMethods() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getContainingClassFullName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getInnerClassFullNames() {
		throw new UnsupportedOperationException();
	}
}
