package domain.javadata;

class NameUtil {
	public static boolean isCompilerGenerated(String fullName) {
		return fullName.startsWith("$");
	}
}
