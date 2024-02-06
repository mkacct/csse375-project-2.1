package domain.javadata;

import org.objectweb.asm.Opcodes;

/**
 * One of the four access modifiers in Java.
 */
public enum AccessModifier {
	PUBLIC,
	PROTECTED,
	PACKAGE_PRIVATE,
	PRIVATE;

	static AccessModifier parseOpcodes(int access) {
		if ((access & Opcodes.ACC_PUBLIC) != 0) {
			return AccessModifier.PUBLIC;
		} else if ((access & Opcodes.ACC_PROTECTED) != 0) {
			return AccessModifier.PROTECTED;
		} else if ((access & Opcodes.ACC_PRIVATE) != 0) {
			return AccessModifier.PRIVATE;
		} else {
			return AccessModifier.PACKAGE_PRIVATE;
		}
	}
}
