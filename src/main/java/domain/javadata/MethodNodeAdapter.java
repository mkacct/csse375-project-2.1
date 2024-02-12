package domain.javadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

class MethodNodeAdapter implements MethodData {
	private static final String THIS = "this";

	private final MethodNode methodNode;

	MethodNodeAdapter(MethodNode methodNode) {
		this.methodNode = methodNode;
	}

	@Override
	public String getName() {
		return this.methodNode.name;
	}

	@Override
	public String getReturnTypeFullName() {
		return Type.getReturnType(this.methodNode.desc).getClassName();
	}

	@Override
	public AccessModifier getAccessModifier() {
		return AccessModifier.parseOpcodes(this.methodNode.access);
	}

	@Override
	public boolean isStatic() {
		return (this.methodNode.access & Opcodes.ACC_STATIC) != 0;
	}

	@Override
	public boolean isFinal() {
		return (this.methodNode.access & Opcodes.ACC_FINAL) != 0;
	}

	@Override
	public boolean isAbstract() {
		return (this.methodNode.access & Opcodes.ACC_ABSTRACT) != 0;
	}

	public List<VariableData> getParams() {
		List<VariableData> params = new ArrayList<VariableData>();
		Type[] asmParamTypes = Type.getArgumentTypes(this.methodNode.desc);
		for (int i = 0; i < asmParamTypes.length; i++) {
			int varIndex = i + (this.isStatic() ? 0 : 1); // Skip "this" for non-static methods
			LocalVariableNode asmParam = LocalVariableUtil.findLocalVariableNode(varIndex, this.methodNode.localVariables);
			Type asmParamType = asmParamTypes[i];
			params.add(new VariableData(
				(asmParam != null) ? asmParam.name : null,
				asmParamType.getClassName()
			));
		}
		return params;
	}

	@Override
	public Set<String> getExceptionTypeFullNames() {
		Set<String> exceptions = new HashSet<String>();
		for (String exception : this.methodNode.exceptions) {
			exceptions.add(Type.getObjectType(exception).getClassName());
		}
		return exceptions;
	}

	@Override
	public Set<VariableData> getLocalVariables() {
		if (this.methodNode.localVariables == null) {return Set.of();}
		Set<VariableData> localVariables = new HashSet<VariableData>();
		for (LocalVariableNode localVariable : this.methodNode.localVariables) {
			if (localVariable.name.equals(THIS)) {continue;}
			localVariables.add(new VariableData(
				localVariable.name,
				Type.getType(localVariable.desc).getClassName()
			));
		}
		return localVariables;
	}

	@Override
	public List<InstrData> getInstructions() {
		List<InstrData> instrs = new ArrayList<InstrData>();
		for (AbstractInsnNode insn : this.methodNode.instructions) {
			instrs.add(this.createInsnNodeAdapter(insn));
		}
		return instrs;
	}

	private InstrData createInsnNodeAdapter(AbstractInsnNode insn) {
		switch (insn.getType()) {
			case AbstractInsnNode.METHOD_INSN:
				return new MethodInsnNodeAdapter((MethodInsnNode)insn);
			case AbstractInsnNode.VAR_INSN:
				return new VarInsnNodeAdapter((VarInsnNode)insn, this.methodNode.localVariables);
			case AbstractInsnNode.FIELD_INSN:
				return new FieldInsnNodeAdapter((FieldInsnNode)insn);
			default:
				return new OtherInsnNodeAdapter();
		}
	}
}
