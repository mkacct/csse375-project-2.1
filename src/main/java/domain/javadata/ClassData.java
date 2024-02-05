package domain.javadata;

import java.util.List;
import java.util.Set;

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
}
