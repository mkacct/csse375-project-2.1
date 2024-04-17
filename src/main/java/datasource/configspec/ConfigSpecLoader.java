package datasource.configspec;

import java.io.IOException;

/**
 * Loads GUI configuration specification from some source.
 */
public interface ConfigSpecLoader {
	ConfigSpec loadConfigSpec() throws IOException;
}
