package domain.javadata;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;

class FieldInsnNodeAdapter implements FieldInstrData {
	private final FieldInsnNode insn;

	FieldInsnNodeAdapter(FieldInsnNode insn) {
		this.insn = insn;
	}

	@Override
	public InstrType getInstrType() {
		return InstrType.FIELD;
	}

	@Override
	public String getFieldOwnerFullName() {
		return Type.getObjectType(this.insn.owner).getClassName();
	}

	@Override
	public String getFieldName() {
		return this.insn.name;
	}

	@Override
	public String getFieldTypeFullName() {
		return Type.getType(this.insn.desc).getClassName();
	}

	@Override
	public VariableOperation getOperation() {
		return VariableOperation.parseOpcode(this.insn.getOpcode());
	}
}
