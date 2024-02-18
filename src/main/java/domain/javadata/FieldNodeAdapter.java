package domain.javadata;


import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldNode;

class FieldNodeAdapter implements FieldData {
	private final FieldNode fieldNode;

	FieldNodeAdapter(FieldNode fieldNode) {
		this.fieldNode = fieldNode;
	}

	@Override
	public String getName() {
		return this.fieldNode.name;
	}

	@Override
	public String getTypeFullName() {
		return Type.getType(this.fieldNode.desc).getClassName();
	}

	@Override
	public AccessModifier getAccessModifier() {
		return AccessModifier.parseOpcodes(this.fieldNode.access);
	}

	@Override
	public boolean isStatic() {
		return (this.fieldNode.access & Opcodes.ACC_STATIC) != 0;
	}

	@Override
	public boolean isFinal() {
		return (this.fieldNode.access & Opcodes.ACC_FINAL) != 0;
	}

	@Override
	public TypeStructure typeParam() {
		String sig = this.fieldNode.signature; // signature stores information about the type paramaters
		if (sig == null) {
			String noArrays = getTypeFullName().replace("[]", "");
			return new TypeStructure(noArrays, (getTypeFullName().length() - noArrays.length()) / 2);
		} else {
			return new TypeStructure(sig);
		}
	}

	@Override
	public Set<String> getAllTypeFullName() {
		return typeParam().getAllFullTypeNames();
	}
}
