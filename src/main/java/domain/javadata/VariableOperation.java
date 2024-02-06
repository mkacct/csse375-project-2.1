package domain.javadata;

import org.objectweb.asm.Opcodes;

/**
 * One of the operations that can be performed in an instruction involving a variable.
 */
public enum VariableOperation {
	GET,
	SET,
	RETURN;

	static VariableOperation parseOpcode(int opcode) {
		switch (opcode) {
			case Opcodes.ILOAD:
			case Opcodes.LLOAD:
			case Opcodes.FLOAD:
			case Opcodes.DLOAD:
			case Opcodes.ALOAD:
			case Opcodes.GETSTATIC:
			case Opcodes.GETFIELD:
				return GET;
			case Opcodes.ISTORE:
			case Opcodes.LSTORE:
			case Opcodes.FSTORE:
			case Opcodes.DSTORE:
			case Opcodes.ASTORE:
			case Opcodes.PUTSTATIC:
			case Opcodes.PUTFIELD:
				return SET;
			case Opcodes.RET:
				return RETURN;
		}
		throw new IllegalArgumentException("Unexpected opcode: " + opcode);
	}
}
