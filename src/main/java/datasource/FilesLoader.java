package datasource;

import java.io.IOException;
import java.util.Set;

public interface FilesLoader {
	Set<byte[]> loadFiles() throws IOException;
}
