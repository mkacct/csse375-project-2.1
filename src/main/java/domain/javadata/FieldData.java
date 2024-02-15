package domain.javadata;

import java.util.Set;

/**
 * A representation of a field in a Java class.
 */
public interface FieldData {
	String getName();
	String getTypeFullName();

	AccessModifier getAccessModifier();
	boolean isStatic();
	boolean isFinal();

	Set<String> getTypeParams();
}
