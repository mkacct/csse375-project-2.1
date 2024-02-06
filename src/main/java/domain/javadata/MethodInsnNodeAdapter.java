package domain.javadata;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodInsnNode;

class MethodInsnNodeAdapter implements MethodInstrData {
	private final MethodInsnNode insn;

	MethodInsnNodeAdapter(MethodInsnNode insn) {
		this.insn = insn;
	}

	@Override
	public InstrType getInstrType() {
		return InstrType.METHOD;
	}

	@Override
	public String getMethodOwnerFullName() {
		return Type.getObjectType(this.insn.owner).getClassName();
	}

	@Override
	public String getMethodName() {
		return this.insn.name;
	}

	@Override
	public String getMethodReturnTypeFullName() {
		return Type.getReturnType(this.insn.desc).getClassName();
	}
}
