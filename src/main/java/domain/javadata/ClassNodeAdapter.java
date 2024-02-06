package domain.javadata;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassNodeAdapter implements ClassData {
	private final ClassNode classNode;

	public ClassNodeAdapter(byte[] javaBytecode) {
		this(new ClassNode());
		ClassReader reader = new ClassReader(javaBytecode);
		reader.accept(this.classNode, ClassReader.EXPAND_FRAMES);
	}

	ClassNodeAdapter(ClassNode classNode) {
		this.classNode = classNode;
	}

	@Override
	public String getFullName() {
		return Type.getObjectType(this.classNode.name).getClassName();
	}

	@Override
	public AccessModifier getAccessModifier() {
		return AccessModifier.parseOpcodes(this.classNode.access);
	}

	@Override
	public ClassType getClassType() {
		return ClassType.parseOpcodes(this.classNode.access);
	}

	@Override
	public boolean isAbstract() {
		return (this.classNode.access & Opcodes.ACC_ABSTRACT) != 0;
	}

	@Override
	public boolean isStatic() {
		return (this.classNode.access & Opcodes.ACC_STATIC) != 0;
	}

	@Override
	public boolean isFinal() {
		return (this.classNode.access & Opcodes.ACC_FINAL) != 0;
	}

	@Override
	public String getSuperFullName() {
		return Type.getObjectType(this.classNode.superName).getClassName();
	}

	@Override
	public Set<String> getInterfaceFullNames() {
		Set<String> interfaceFullNames = new HashSet<String>();
		for (String interfaceName : this.classNode.interfaces) {
			interfaceFullNames.add(Type.getObjectType(interfaceName).getClassName());
		}
		return interfaceFullNames;
	}

	@Override
	public Set<FieldData> getFields() {
		Set<FieldData> fields = new HashSet<FieldData>();
		for (FieldNode fieldNode : this.classNode.fields) {
			fields.add(new FieldNodeAdapter(fieldNode));
		}
		return fields;
	}

	@Override
	public Set<MethodData> getMethods() {
		Set<MethodData> methods = new HashSet<MethodData>();
		for (MethodNode methodNode : this.classNode.methods) {
			methods.add(new MethodNodeAdapter(methodNode));
		}
		return methods;
	}

	@Override
	public String getContainingClassFullName() {
		if (this.classNode.outerClass == null) {return null;}
		return Type.getObjectType(this.classNode.outerClass).getClassName();
	}

	@Override
	public Set<String> getInnerClassFullNames() {
		Set<String> innerClassNames = new HashSet<String>();
		for (InnerClassNode innerClassNode : this.classNode.innerClasses) {
			innerClassNames.add(Type.getObjectType(innerClassNode.name).getClassName());
		}
		return innerClassNames;
	}
}
