package domain;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.InstrData;
import domain.javadata.MethodData;

public class MethodLengthCheck implements Check {
	private static final String NAME = "methodLength";

	private static final String MAX_METHOD_LENGTH_KEY = "maxMethodLengthInstrs";

	@Override
	public String getName() {return NAME;}

	@Override
	public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
		int maxMethodLengthInstrs;
		try {
			maxMethodLengthInstrs = config.getInt(MAX_METHOD_LENGTH_KEY);
		} catch (ClassCastException ex) {
			return Set.of(new Message(
				MessageLevel.ERROR,
				MessageFormat.format(
					"Config property \"{0}\" must be an integer; could not run check",
					MAX_METHOD_LENGTH_KEY
				)
			));
		} catch (IllegalArgumentException ex) {
			return Set.of(new Message(
				MessageLevel.INFO,
				MessageFormat.format(
					"Config property \"{0}\" not found; skipping check",
					MAX_METHOD_LENGTH_KEY
				)
			));
		}

		Set<Message> messages = new HashSet<>();
		for (ClassData classData : classes.values()) {
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
