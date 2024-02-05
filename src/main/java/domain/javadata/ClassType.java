package domain.javadata;

import org.objectweb.asm.Opcodes;

public enum ClassType {
	CLASS,
	INTERFACE,
	ENUM;

	static ClassType parseOpcodes(int access) {
		if ((access & Opcodes.ACC_INTERFACE) != 0) {
			return ClassType.INTERFACE;
		} else if ((access & Opcodes.ACC_ENUM) != 0) {
			return ClassType.ENUM;
		} else {
			return ClassType.CLASS;
		}
	}
}
