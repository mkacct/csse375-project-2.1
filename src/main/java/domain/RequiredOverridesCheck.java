package domain;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.MethodData;

public class RequiredOverridesCheck extends Check {
	private static final String NAME = "requiredOverrides";

	public RequiredOverridesCheck() {
		super(NAME);
	}

	@Override
	public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
		Set<Message> messages = new HashSet<>();
		for (ClassData classData : classes.values()) {
			this.validateCompareToImpliesEquals(classData, messages);
			this.validateEqualsImpliesHashCode(classData, messages);
		}
		return messages;
	}

	private void validateCompareToImpliesEquals(ClassData classData, Set<Message> messages) {
		if (classData.getInterfaceFullNames().contains("java.lang.Comparable")) {
			if (classHasMethod(classData, "compareTo") && !classHasMethod(classData, "equals")) {
				messages.add(new Message(
					MessageLevel.WARNING,
					"Class implementing Comparable overrides compareTo but not equals",
					classData.getFullName()
				));
			}
		}
	}

	private void validateEqualsImpliesHashCode(ClassData classData, Set<Message> messages) {
		if (classHasMethod(classData, "equals") && !classHasMethod(classData, "hashCode")) {
			messages.add(new Message(
				MessageLevel.ERROR,
				"Class overrides equals but not hashCode",
				classData.getFullName()
			));
		}
	}

	private static boolean classHasMethod(ClassData classData, String methodName) {
		for (MethodData method : classData.getMethods()) {
			if (method.getName().equals(methodName)) {
				return true;
			}
		}
		return false;
	}
}
