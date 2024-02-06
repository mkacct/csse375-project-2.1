package datasource;

import java.io.IOException;
import java.util.Set;

/**
 * Loads files from some source as byte arrays.
 */
public interface FilesLoader {
	Set<byte[]> loadFiles() throws IOException;
}
