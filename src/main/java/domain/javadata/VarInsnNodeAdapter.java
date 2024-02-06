package domain.javadata;

import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.VarInsnNode;

class VarInsnNodeAdapter implements LocalVarInstrData {
	private final VarInsnNode insn;
	private final LocalVariableNode localVariableNode;

	VarInsnNodeAdapter(VarInsnNode insn, List<LocalVariableNode> localVars) {
		this.insn = insn;
		this.localVariableNode = localVars.get(insn.var);
	}

	@Override
	public InstrType getInstrType() {
		return InstrType.LOCAL_VARIABLE;
	}

	@Override
	public String getVarName() {
		return this.localVariableNode.name;
	}

	@Override
	public String getVarTypeFullName() {
		return Type.getType(this.localVariableNode.desc).getClassName();
	}

	@Override
	public VariableOperation getOperation() {
		return VariableOperation.parseOpcode(this.insn.getOpcode());
	}
}
