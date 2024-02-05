package domain.javadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.ParameterNode;

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

	@Override
	public List<VariableData> getParams() {
		if (this.methodNode.parameters == null) {throw new UnsupportedOperationException("Parameter names not available");} // TODO: is this really what is happening?
		List<VariableData> params = new ArrayList<VariableData>();
		Type[] asmParamTypes = Type.getArgumentTypes(this.methodNode.desc);
		System.out.println("asmParamTypes: " + asmParamTypes.length);
		System.out.println("this.methodNode.parameters: " + this.methodNode.parameters.size());
		for (int i = 0; i < this.methodNode.parameters.size(); i++) {
			ParameterNode asmParam = this.methodNode.parameters.get(i);
			Type asmParamType = asmParamTypes[i];
			params.add(new VariableData(
				asmParam.name,
				asmParamType.getClassName(),
				(asmParam.access & Opcodes.ACC_FINAL) != 0
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
}
