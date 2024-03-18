package domain;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import domain.javadata.ClassData;
import domain.javadata.FieldData;
import domain.javadata.MethodData;
import domain.javadata.VariableData;

/**
 * Performs the type-validating behavior of checks
 * such as AdapterPatternCheck and ProgramToInterfaceNotImplementationCheck.
 */
class TypeValidator {
	private final Function<String, Boolean> validationFunc;
	private final MessageLevel messageLevel;

	private Function<ClassData, Boolean> classExemptionFunc = (classData) -> {return false;};

	private String fieldMessagePattern = "Field \"{0}\" is of type \"{1}\"";
	private String methodMessagePattern = "Method \"{0}\" has return type \"{1}\"";
	private String paramMessagePattern = "Method \"{0}\" has parameter \"{1}\" of type \"{2}\"";

	/**
	 * @param validationFunc A function that takes a type's full name and returns true if it's okay or false if a message should be generated
	 * @param messageLevel The level of any messages generated
	 */
	public TypeValidator(Function<String, Boolean> validationFunc, MessageLevel messageLevel) {
		this.validationFunc = validationFunc;
		this.messageLevel = messageLevel;
	}

	/**
	 * Set the class exemption function (which by default just returns false)
	 * @param classExemptionFunc A function that takes a class and returns true if it should be exempt from validation
	 */
	public void setClassExemptionFunc(Function<ClassData, Boolean> classExemptionFunc) {
		this.classExemptionFunc = classExemptionFunc;
	}

	/**
	 * Set the message patterns for the generated messages
	 * @param fieldMessagePattern {0} is the field name, {1} is the type full name
	 * @param methodMessagePattern {0} is the method name, {1} is the return type full name
	 * @param paramMessagePattern {0} is the method name, {1} is the parameter name, {2} is the type full name
	 */
	public void setMessagePatterns(String fieldMessagePattern, String methodMessagePattern, String paramMessagePattern) {
		this.fieldMessagePattern = fieldMessagePattern;
		this.methodMessagePattern = methodMessagePattern;
		this.paramMessagePattern = paramMessagePattern;
	}

	/**
	 * Check all the field, method, and parameter types in the given classes
	 * and generate messages for any that don't pass the validation function
	 * @param allClasses The classes to check types within
	 * @param messages The set to update with any generated messages
	 */
	public void validateTypes(Collection<ClassData> allClasses, Set<Message> messages) {
		for (ClassData classData : allClasses) {
			if (this.classExemptionFunc.apply(classData)) {continue;}
			this.validateFieldTypes(classData, messages);
			this.validateMethodTypes(classData, messages);
		}
	}

	private void validateFieldTypes(ClassData classData, Set<Message> messages) {
		for (FieldData field : classData.getFields()) {
			if (!this.validationFunc.apply(field.getTypeFullName())) {
				messages.add(new Message(
					this.messageLevel,
					MessageFormat.format(this.fieldMessagePattern, field.getName(), field.getTypeFullName()),
					classData.getFullName()
				));
			}
		}
	}

	private void validateMethodTypes(ClassData classData, Set<Message> messages) {
		for (MethodData method : classData.getMethods()) {
			if (!this.validationFunc.apply(method.getReturnTypeFullName())) {
				messages.add(new Message(
					this.messageLevel,
					MessageFormat.format(this.methodMessagePattern, method.getName(), method.getReturnTypeFullName()),
					classData.getFullName()
				));
			}
			this.validateParamTypes(classData, method, messages);
		}
	}

	private void validateParamTypes(ClassData classData, MethodData method, Set<Message> messages) {
		for (VariableData param : method.getParams()) {
			if (!this.validationFunc.apply(param.typeFullName)) {
				messages.add(new Message(
					this.messageLevel,
					MessageFormat.format(this.paramMessagePattern, method.getName(), param.name, param.typeFullName),
					classData.getFullName()
				));
			}
		}
	}
}
