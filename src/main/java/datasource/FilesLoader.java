package datasource;

import java.io.IOException;
import java.util.Set;

/**
 * Loads files from some source as byte arrays.
 */
public interface FilesLoader {
	/**
	 * Load files from the source.
	 * @param ext if not null, only files with this extension will be loaded
	 * @return byte arrays of the files
	 * @throws IllegalStateException if the source is not valid
	 * @throws IOException
	 */
	Set<byte[]> loadFiles(String ext) throws IOException, IllegalStateException;
}
