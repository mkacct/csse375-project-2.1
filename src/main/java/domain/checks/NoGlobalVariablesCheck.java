package domain.checks;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import datasource.Configuration;
import domain.Check;
import domain.Message;
import domain.MessageLevel;
import domain.javadata.ClassData;
import domain.javadata.ClassDataCollection;
import domain.javadata.FieldData;

public class NoGlobalVariablesCheck extends Check {
	private static final String NAME = "noGlobalVariables";

	public NoGlobalVariablesCheck() {
		super(NAME);
	}

	@Override
	public Set<Message> run(ClassDataCollection classes, Configuration config) {
		Set<Message> messages = new HashSet<>();
		for (ClassData classData : classes) {
			for (FieldData field : classData.getFields()) {
				if (field.isStatic() && !field.isFinal()) {
					messages.add(new Message(
						MessageLevel.ERROR,
						MessageFormat.format(
							"Field \"{0}\" is a global variable",
							field.getName()
						),
						classData.getFullName()
					));
				}
			}
		}
		return messages;
	}
}
