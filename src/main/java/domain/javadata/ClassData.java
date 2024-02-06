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

	/**
	 * @return the fully qualified name of the superclass, or null if this class is java.lang.Object
	 */
	String getSuperFullName();

	Set<String> getInterfaceFullNames();

	Set<FieldData> getFields();
	Set<MethodData> getMethods();

	/**
	 * @return the fully qualified name of the outer class, or null if this class is not an inner class
	 */
	String getContainingClassFullName();

	Set<String> getInnerClassFullNames();
}
