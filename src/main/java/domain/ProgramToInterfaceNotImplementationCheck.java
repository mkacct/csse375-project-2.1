package domain;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.ClassType;
import domain.javadata.FieldData;
import domain.javadata.MethodData;
import domain.javadata.VariableData;

public class ProgramToInterfaceNotImplementationCheck implements Check {
	private static final String NAME = "programToInterface";

	private static final String DOMAIN_PKG_NAME_KEY = "domainPackageName";
	private static final String DEFAULT_DOMAIN_PKG_NAME = "domain";
	private static final String ADAPTER_CLASS_NAME_REGEX_KEY = "adapterClassNameRegex";
	private static final String DEFAULT_ADAPTER_CLASS_NAME_REGEX = "Adapter$";
	private static final String ALLOWED_DEPENDENCIES_KEY = "allowedDependencies";

	private static final Set<String> PRIMITIVE_TYPES = Set.of( // actual primitives (and void of course)
		"byte", "char", "short", "int", "long", "float", "double", "boolean", "void"
	);
	private static final String OBJECT_TYPE = "java.lang.Object";
	private static final Set<String> PRIMITIVE_CLASSES = Set.of( // things from java.lang that are as good as primitives
		"java.lang.Byte",
		"java.lang.Character",
		"java.lang.Short",
		"java.lang.Integer",
		"java.lang.Long",
		"java.lang.Float",
		"java.lang.Double",
		"java.lang.Boolean",
		"java.lang.String",
		"java.lang.Void"
	);
	private static final Set<String> COMMON_INTERFACES = Set.of( // popular data structure interfaces
		"java.util.Collection",
		"java.util.Set",
		"java.util.List",
		"java.util.Map",
		"java.util.Queue"
	);

	@Override
	public String getName() {return NAME;}

	@Override
	public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
		String domainPkgName = config.getString(DOMAIN_PKG_NAME_KEY, DEFAULT_DOMAIN_PKG_NAME);
		Pattern adapterNamePattern = Pattern.compile(config.getString(ADAPTER_CLASS_NAME_REGEX_KEY, DEFAULT_ADAPTER_CLASS_NAME_REGEX));
		Set<String> allowedDeps = Set.copyOf(config.getListOfString(ALLOWED_DEPENDENCIES_KEY, List.of()));

		Set<Message> messages = new HashSet<Message>();
		for (ClassData classData : classes.values()) {
			if (!classData.getFullName().startsWith(domainPkgName + ".")) {continue;} // only check domain classes
			if (adapterNamePattern.matcher(classData.getSimpleName()).find()) {continue;} // ignore adapter classes
			for (FieldData field : classData.getFields()) {
				if (!isTypeOkay(field.getTypeFullName(), classes, domainPkgName, allowedDeps)) {
					messages.add(new Message(
						MessageLevel.WARNING,
						MessageFormat.format(
							"Field \"{0}\" is of type \"{1}\"",
							field.getName(), field.getTypeFullName()
						),
						classData.getFullName()
					));
				}
			}
			for (MethodData method : classData.getMethods()) {
				if (!isTypeOkay(method.getReturnTypeFullName(), classes, domainPkgName, allowedDeps)) {
					messages.add(new Message(
						MessageLevel.WARNING,
						MessageFormat.format(
							"Method \"{0}\" has return type \"{1}\"",
							method.getName(), method.getReturnTypeFullName()
						),
						classData.getFullName()
					));
				}
				for (VariableData param : method.getParams()) {
					if (!isTypeOkay(param.typeFullName, classes, domainPkgName, allowedDeps)) {
						messages.add(new Message(
							MessageLevel.WARNING,
							MessageFormat.format(
								"Method \"{0}\" has parameter \"{1}\" of type \"{2}\"",
								method.getName(), param.name, param.typeFullName
							),
							classData.getFullName()
						));
					}
				}
			}
		}
		return messages;
	}

	private static boolean isTypeOkay(String typeFullName, Map<String, ClassData> classes, String domainPkgName, Set<String> allowedDeps) {
		typeFullName = stripArrayIndicators(typeFullName); // we don't care whether it's an array
		if (typeFullName.startsWith(domainPkgName + ".")) {return true;} // type is itself in domain
		if (typeFullName.equals(OBJECT_TYPE)) {return true;} // can't program to implementation if there is none
		if (PRIMITIVE_TYPES.contains(typeFullName)) {return true;} // type is primitive
		if (PRIMITIVE_CLASSES.contains(typeFullName)) {return true;} // type is equivalent to primitive
		if (COMMON_INTERFACES.contains(typeFullName)) {return true;} // type is one of the popular interfaces
		if (allowedDeps.contains(typeFullName)) {return true;} // type is in user's allowed dependencies
		if (classes.containsKey(typeFullName)) {
			ClassData typeClass = classes.get(typeFullName);
			if (typeClass.getClassType() != ClassType.CLASS) {return true;} // type is an interface/enum from the project
		}
		return false;
	}

	private static String stripArrayIndicators(String typeFullName) {
		while (typeFullName.endsWith("[]")) {
			typeFullName = typeFullName.substring(0, typeFullName.length() - 2);
		}
		return typeFullName;
	}
}
