package domain.javadata;

public interface FieldData {
	String getName();
	String getTypeFullName();

	AccessModifier getAccessModifier();
	boolean isStatic();
	boolean isFinal();
}
