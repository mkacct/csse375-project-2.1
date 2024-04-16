package datasource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
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
	public Set<byte[]> loadFiles(String ext) throws IOException, IllegalStateException {
		Set<byte[]> files = new HashSet<byte[]>();
		File dir = validatePath(ext);
		addFilesFromDir(files, dir, ext);
		return files;
	}

	private void checkForEmptyPath() {
		if (this.path.isEmpty()) {
			throw new InvalidParameterException("Empty path is not allowed");
		}
	}

	private File validatePath(String ext) {
		this.checkForEmptyPath();
		File dir = new File(this.path);
		handlePathAsNonDirectory(dir);
		handleEmptyDirectory(dir);
		handleDirectoryWithoutClassFiles(dir, ext);
		return dir;
	}

	private void handleEmptyDirectory(File dir) {
		if (dir.listFiles() == null || Objects.requireNonNull(dir.listFiles()).length == 0) {
			throw new IllegalStateException(MessageFormat.format("Provided path is empty: {0}", this.path));
		}
	}

	private void handleDirectoryWithoutClassFiles(File dir, String ext) {
		for (File file : Objects.requireNonNull(dir.listFiles())) {
			handleNonClassFile(file, ext);
		}
	}

	private void handleNonClassFile(File file, String ext) {
		boolean isNotClassFile = !getExtensionByStringHandling(file.getName()).equals(ext);
		if (isNotClassFile) {
			throw new IllegalStateException(MessageFormat.format("{0} is not a class file", path + "/" + file.getName()));
		}
	}

	// Source: https://www.baeldung.com/java-file-extension
	private String getExtensionByStringHandling(String filename) {
		Optional<String> extension = Optional.ofNullable(filename)
						.filter(f -> f.contains("."))
						.map(f -> f.substring(filename.lastIndexOf(".") + 1));
		return convertOptionalToString(extension);
	}

	// Source: https://mkyong.com/java8/java-8-convert-optionalstring-to-string/
	private String convertOptionalToString(Optional<String> extension) {
		return extension.stream()
						.filter(x -> x.length() == 1)
						.findFirst()  // returns Optional
						.map(Object::toString)
						.orElse("");
	}


	private void handlePathAsNonDirectory(File dir) {
		if (!dir.isDirectory()) {
			throw new IllegalStateException(MessageFormat.format("Provided path is not a directory: {0}", this.path));
		}
	}

	private void addFilesFromDir(Set<byte[]> files, File dir, String ext) throws IOException {
		for (File file : Objects.requireNonNull(dir.listFiles())) {
			parseFileObject(files, ext, file);
		}
	}

	private void parseFileObject(Set<byte[]> files, String ext, File file) throws IOException {
		if (file.isDirectory()) {
			addFilesFromDir(files, file, ext);
		} else {
			addClassFile(files, ext, file);
		}
	}

	private void addClassFile(Set<byte[]> files, String ext, File file) throws IOException {
		if (ext == null || file.getName().endsWith("." + ext)) {
			files.add(readFile(file));
		}
	}

	private byte[] readFile(File file) throws IOException {
		try (FileInputStream in = new FileInputStream(file)) {
			return in.readAllBytes();
		}
	}
}
