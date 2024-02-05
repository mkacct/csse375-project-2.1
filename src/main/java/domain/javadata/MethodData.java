package domain.javadata;

import java.util.List;
import java.util.Set;

public interface MethodData {
	String getName();
	String getReturnTypeFullName();

	AccessModifier getAccessModifier();
	boolean isStatic();
	boolean isFinal();
	boolean isAbstract();

	List<VariableData> getParams();
	Set<String> getExceptionTypeFullNames();

	Set<VariableData> getLocalVariables();
	// TODO: add instruction functionality
}
