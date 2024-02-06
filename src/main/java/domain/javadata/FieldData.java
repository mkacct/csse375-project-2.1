package domain.javadata;

/**
 * A representation of a field in a Java class.
 */
public interface FieldData {
	String getName();
	String getTypeFullName();

	AccessModifier getAccessModifier();
	boolean isStatic();
	boolean isFinal();
}
