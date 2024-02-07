package domain;

import java.util.Map;
import java.util.Set;

import datasource.Configuration;
import domain.javadata.ClassData;

/**
 * A linter check (be it a cursory style check, principle check, or pattern detector).
 */
public interface Check {
	/**
	 * @return the check's name, in camelCase, for identification in the configuration file
	 */
	String getName();

	/**
	 * @param classes map of class full names to class data
	 * @param config from the configuration file
	 * @return set of messages (errors, warnings, info) generated by the check
	 */
	Set<Message> run(Map<String, ClassData> classes, Configuration config);
}
