package domain.checks;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import datasource.Configuration;
import domain.Check;
import domain.Message;
import domain.MessageLevel;
import domain.javadata.ClassData;
import domain.javadata.ClassDataCollection;
import domain.javadata.InstrData;
import domain.javadata.MethodData;

public class MethodLengthCheck extends Check {
	private static final String NAME = "methodLength";

	private static final String MAX_METHOD_LENGTH_KEY = "maxMethodLengthInstrs";

	public MethodLengthCheck() {
		super(NAME);
	}

	@Override
	public Set<Message> run(ClassDataCollection classes, Configuration config) {
		CountCheckPropertyValidator validator = new CountCheckPropertyValidator();
		Integer maxMethodLengthInstrs = validator.validateGetInt(config, MAX_METHOD_LENGTH_KEY);
		if (maxMethodLengthInstrs == null) {
			return Set.of(validator.getValidationFailureMessage());
		}
		// maxMethodLengthInstrs != null

		Set<Message> messages = new HashSet<>();
		for (ClassData classData : classes) {
			this.checkClass(classData, maxMethodLengthInstrs, messages);
		}
		return messages;
	}

	private void checkClass(ClassData classData, int maxMethodLengthInstrs, Set<Message> messages) {
		for (MethodData method : classData.getMethods()) {
			List<InstrData> instrs = method.getInstructions();
			if (instrs.size() > maxMethodLengthInstrs) {
				messages.add(new Message(
					MessageLevel.WARNING,
					MessageFormat.format(
						"Method \"{0}\" is too long ({1} instrs, should be <= {2})",
						method.getName(), instrs.size(), maxMethodLengthInstrs
					),
					classData.getFullName()
				));
			}
		}
	}
}
