package domain.javadata;

import java.util.List;
import java.util.Set;

/**
 * A representation of a method in a Java class.
 */
public interface MethodData {
	static final String CONSTRUCTOR_NAME = "<init>";
	static final String STATIC_INITIALIZER_NAME = "<clinit>";

	String getName();
	String getReturnTypeFullName();


	TypeStructure getReturnTypeStructure();
	Set<String> getAllReturnTypeFullName();

	AccessModifier getAccessModifier();
	boolean isStatic();
	boolean isFinal();
	boolean isAbstract();

	List<VariableData> getParams();
	Set<String> getExceptionTypeFullNames();

	/**
	 * @return the method's local variables, including any parameters
	 */
	Set<VariableData> getLocalVariables();

	List<InstrData> getInstructions();
}
