package gui;

import java.io.File;

final class FileUtil {
	public static File currentWorkingDirectory() {
		return new File(System.getProperty("user.dir"));
	}
}
