package domain.javadata;

import java.util.Set;

/**
 * A representation of a Java class (or other top-level entity such as an interface).
 */
public interface ClassData {
	String getFullName();

	AccessModifier getAccessModifier();
	ClassType getClassType();
	boolean isAbstract();
	boolean isStatic();
	boolean isFinal();

	// List<String> getTypeParamFullNames();
	String getSuperFullName();
	Set<String> getInterfaceFullNames();

	Set<FieldData> getFields();
	Set<MethodData> getMethods();

	String getContainingClassFullName();
	Set<String> getInnerClassFullNames();
}
