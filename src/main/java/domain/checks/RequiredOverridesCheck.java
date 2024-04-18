package domain.checks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import datasource.Configuration;
import domain.Check;
import domain.Message;
import domain.MessageLevel;
import domain.javadata.ClassData;
import domain.javadata.ClassDataCollection;
import domain.javadata.MethodData;
import domain.javadata.VariableData;

public class RequiredOverridesCheck extends Check {
	private static final String NAME = "requiredOverrides";

	private static final String[] TYPES_EMPTY = new String[0];
	private static final String[] TYPES_1_OBJECT = new String[] {"java.lang.Object"};

	public RequiredOverridesCheck() {
		super(NAME);
	}

	@Override
	public Set<Message> run(ClassDataCollection classes, Configuration config) {
		Set<Message> messages = new HashSet<>();
		for (ClassData classData : classes) {
			this.validateCompareToImpliesEquals(classData, messages);
			this.validateEqualsImpliesHashCode(classData, messages);
		}
		return messages;
	}

	private void validateCompareToImpliesEquals(ClassData classData, Set<Message> messages) {
		if (classData.getInterfaceFullNames().contains("java.lang.Comparable")) {
			String[] compareToParamTypes = new String[] {classData.getFullName()};
			boolean hasCompareTo = classHasMethod(classData, "compareTo", compareToParamTypes);
			boolean hasEquals = classHasMethod(classData, "equals", TYPES_1_OBJECT);
			if (hasCompareTo && !hasEquals) {
				messages.add(new Message(
					MessageLevel.WARNING,
					"Class implementing Comparable overrides compareTo but not equals",
					classData.getFullName()
				));
			}
		}
	}

	private void validateEqualsImpliesHashCode(ClassData classData, Set<Message> messages) {
		boolean hasEquals = classHasMethod(classData, "equals", TYPES_1_OBJECT);
		boolean hasHashCode = classHasMethod(classData, "hashCode", TYPES_EMPTY);
		if (hasEquals && !hasHashCode) {
			messages.add(new Message(
				MessageLevel.ERROR,
				"Class overrides equals but not hashCode",
				classData.getFullName()
			));
		}
	}

	private static boolean classHasMethod(ClassData classData, String methodName, String[] paramTypes) {
		for (MethodData method : classData.getMethods()) {
			if (method.getName().equals(methodName) && paramTypesMatch(method.getParams(), paramTypes)) {
				return true;
			}
		}
		return false;
	}

	private static boolean paramTypesMatch(List<VariableData> params, String[] paramTypes) {
		if (params.size() != paramTypes.length) {return false;}
		for (int i = 0; i < paramTypes.length; i++) {
			if (!params.get(i).typeFullName.equals(paramTypes[i])) {return false;}
		}
		return true;
	}
}
