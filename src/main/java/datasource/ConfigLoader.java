package datasource;

import java.io.IOException;

/**
 * Loads user configuration from some source.
 */
public interface ConfigLoader {
	Configuration loadConfig() throws IOException;
}
