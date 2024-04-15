package domain;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.ClassDataCollection;
import domain.javadata.MethodData;
import domain.javadata.VariableData;

public class ParameterCountCheck extends Check {
	private static final String NAME = "parameterCount";

	private static final String MAX_NUM_PARAMS_KEY = "maxNumParameters";

	public ParameterCountCheck() {
		super(NAME);
	}

	@Override
	public Set<Message> run(ClassDataCollection classes, Configuration config) {
		CountCheckPropertyValidator validator = new CountCheckPropertyValidator();
		Integer maxNumParams = validator.validateGetInt(config, MAX_NUM_PARAMS_KEY);
		if (maxNumParams == null) {
			return Set.of(validator.getValidationFailureMessage());
		}
		// maxNumParams != null

		Set<Message> messages = new HashSet<>();
		for (ClassData classData : classes) {
			this.checkClass(classData, maxNumParams, messages);
		}
		return messages;
	}

	private void checkClass(ClassData classData, int maxNumParams, Set<Message> messages) {
		for (MethodData method : classData.getMethods()) {
			List<VariableData> params = method.getParams();
			if (params.size() > maxNumParams) {
				messages.add(new Message(
					MessageLevel.WARNING,
					MessageFormat.format(
						"Method \"{0}\" has too many params ({1} params, should be <= {2})",
						method.getName(), params.size(), maxNumParams
					),
					classData.getFullName()
				));
			}
		}
	}
}
