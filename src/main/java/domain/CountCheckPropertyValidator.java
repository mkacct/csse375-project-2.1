package domain;

import datasource.Configuration;
import java.text.MessageFormat;

/**
 * Validates the integer config property used by count checks
 * such as MethodLengthCheck and ParameterCountCheck.
 */
class CountCheckPropertyValidator {
	private Message generatedMessage = null;

	/**
	 * Reads an integer from the config for a count check, generating an appropriate message if it fails.
	 * @param config The config to read from
	 * @param key The integer key to read
	 * @return The integer value, OR NULL if an error occurred
	 */
	public Integer validateGetInt(Configuration config, String key) {
		Integer value;
		try {
			value = config.getInt(key);
		} catch (ClassCastException ex) {
			this.generatedMessage = new Message(
				MessageLevel.ERROR,
				MessageFormat.format("Config property \"{0}\" must be an integer; could not run check", key)
			);
			return null;
		} catch (IllegalArgumentException ex) {
			this.generatedMessage = new Message(
				MessageLevel.INFO,
				MessageFormat.format("Config property \"{0}\" not found; skipping check", key)
			);
			return null;
		}
		this.generatedMessage = null;
		return value;
	}

	/**
	 * @return the last error message generated
	 */
	public Message getValidationFailureMessage() {
		if (this.generatedMessage == null) {throw new IllegalStateException("There is no message");}
		return this.generatedMessage;
	}
}
