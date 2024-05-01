package datasource;

import java.io.IOException;

/**
 * Loads and saves configuration to/from some source.
 */
public interface ConfigRW {
	/**
	 * Check whether the source exists. Could return false if, for example, the source is a file and there is no file at the path.
	 * Does not necessarily mean that the source is valid.
	 * @return true iff the source exists
	 */
	boolean sourceExists();

	/**
	 * Load configuration from the source.
	 * @return the loaded configuration
	 * @throws IOException if the source does not exist, is not valid, etc.
	 */
	Configuration loadConfig() throws IOException;
}
