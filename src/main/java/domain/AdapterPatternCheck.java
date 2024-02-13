package domain;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.text.MessageFormat;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.FieldData;
import domain.javadata.MethodData;
import domain.javadata.VariableData;

public class AdapterPatternCheck implements Check {
	private static final String NAME = "adapterPattern";

	private static final String ADAPTER_CLASS_NAME_REGEX_KEY = "adapterClassNameRegex";
	private static final String DEFAULT_ADAPTER_CLASS_NAME_REGEX = "Adapter$";

	@Override
	public String getName() {return NAME;}

	@Override
	public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
		Pattern adapterNamePattern = Pattern.compile(config.getString(ADAPTER_CLASS_NAME_REGEX_KEY, DEFAULT_ADAPTER_CLASS_NAME_REGEX));

		Set<Message> messages = new HashSet<Message>();
		Set<String> adapterFullNames = findAdapters(classes, adapterNamePattern, messages);
		validateUsageOfAdapters(classes, adapterFullNames, messages);
		return messages;
	}

	private static Set<String> findAdapters(Map<String, ClassData> classes, Pattern adapterNamePattern, Set<Message> messages) {
		Set<String> adapterFullNames = new HashSet<String>();
		for (ClassData classData : classes.values()) {
			if (adapterNamePattern.matcher(classData.getSimpleName()).find()) {
				adapterFullNames.add(classData.getFullName());
				if (classData.getInterfaceFullNames().size() == 0) {
					messages.add(new Message(
						MessageLevel.WARNING,
						"Adapter class does not implement any interface",
						classData.getFullName()
					));
				}
			}
		}
		return adapterFullNames;
	}

	private static void validateUsageOfAdapters(Map<String, ClassData> classes, Set<String> adapterFullNames, Set<Message> messages) {
		for (ClassData classData : classes.values()) {
			for (FieldData field : classData.getFields()) {
				if (adapterFullNames.contains(field.getTypeFullName())) {
					messages.add(new Message(
						MessageLevel.WARNING,
						MessageFormat.format(
							"Field \"{0}\" is of adapter type \"{1}\"",
							field.getName(), field.getTypeFullName()
						),
						classData.getFullName()
					));
				}
			}
			for (MethodData method : classData.getMethods()) {
				if (adapterFullNames.contains(method.getReturnTypeFullName())) {
					messages.add(new Message(
						MessageLevel.WARNING,
						MessageFormat.format(
							"Method \"{0}\" has adapter return type \"{1}\"",
							method.getName(), method.getReturnTypeFullName()
						),
						classData.getFullName()
					));
				}
				for (VariableData param : method.getParams()) {
					if (adapterFullNames.contains(param.typeFullName)) {
						messages.add(new Message(
							MessageLevel.WARNING,
							MessageFormat.format(
								"Method \"{0}\" has parameter \"{1}\" of adapter type \"{2}\"",
								method.getName(), param.name, param.typeFullName
							),
							classData.getFullName()
						));
					}
				}
			}
		}
	}
}
