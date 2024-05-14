package general;

public final class CmpUtil {
	/**
	 * Checks equality in a null-safe manner. Good for use in equals() methods.
	 */
	public static <T> boolean areEqual(T a, T b) {
		if (a == null) {return b == null;}
		return a.equals(b);
	}
}
