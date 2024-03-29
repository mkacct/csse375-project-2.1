package domain;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.FieldData;

public class ImmutableExceptionsCheck extends Check {
	private static final String NAME = "immutableExceptions";

	private static final String EXCEPTION_CLASS_NAME_REGEX = "(?:Exception|Error)$";

	public ImmutableExceptionsCheck() {
		super(NAME);
	}

	@Override
	public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
		Pattern exceptionNamePattern = Pattern.compile(EXCEPTION_CLASS_NAME_REGEX);

		Set<Message> messages = new HashSet<Message>();
		for (ClassData classData : classes.values()) {
			if (exceptionNamePattern.matcher(classData.getSimpleName()).find()) {
				checkExceptionClass(classData, messages);
			}
		}
		return messages;
	}

	private static void checkExceptionClass(ClassData classData, Set<Message> messages) {
		Set<String> nonFinalFieldNames = new HashSet<String>();
		for (FieldData field : classData.getFields()) {
			if (!field.isFinal()) {
				nonFinalFieldNames.add(field.getName());
			}
		}
		if (!nonFinalFieldNames.isEmpty()) {
			messages.add(new Message(
				MessageLevel.WARNING,
				MessageFormat.format(
					"Exception class has non-final field(s): {0}",
					String.join(", ", nonFinalFieldNames)
				),
				classData.getFullName()
			));
		}
	}
}
