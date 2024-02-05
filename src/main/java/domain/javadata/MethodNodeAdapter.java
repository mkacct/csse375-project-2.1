package domain.javadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

class MethodNodeAdapter implements MethodData {
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
			LocalVariableNode asmParam = this.methodNode.localVariables.get(i);
			Type asmParamType = asmParamTypes[i];
			params.add(new VariableData(
				asmParam.name,
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
		Set<VariableData> localVariables = new HashSet<VariableData>();
		for (LocalVariableNode localVariable : this.methodNode.localVariables) {
			localVariables.add(new VariableData(
				localVariable.name,
				Type.getType(localVariable.desc).getClassName()
			));
		}
		return localVariables;
	}
}
