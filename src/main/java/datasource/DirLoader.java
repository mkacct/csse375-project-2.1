package datasource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Loads files from a given directory as byte arrays.
 */
public class DirLoader implements FilesLoader {
	private final String path;

	public DirLoader(String path) {
		this.path = path;
	}

	@Override
	public Set<byte[]> loadFiles(String ext) throws IOException {
		Set<byte[]> files = new HashSet<byte[]>();
		addFilesFromDir(files, new File(this.path), ext);
		return files;
	}

	private void addFilesFromDir(Set<byte[]> files, File dir, String ext) throws IOException {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				addFilesFromDir(files, file, ext);
			} else {
				if (ext == null || file.getName().endsWith("." + ext)) {
					files.add(readFile(file));
				}
			}
		}
	}

	private byte[] readFile(File file) throws IOException {
		try (FileInputStream in = new FileInputStream(file)) {
			return in.readAllBytes();
		}
	}
}
