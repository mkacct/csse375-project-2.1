package domain.javadata;

final class NameUtil {
	public static boolean isCompilerGenerated(String fullName) {
		return fullName.startsWith("$");
	}
}
