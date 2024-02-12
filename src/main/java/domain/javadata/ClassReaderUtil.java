package domain.javadata;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class ClassReaderUtil {
	public static ClassData read(byte[] javaBytecode) {
		ClassNode classNode = new ClassNode();
		ClassReader reader = new ClassReader(javaBytecode);
		reader.accept(classNode, ClassReader.EXPAND_FRAMES);
		return new ClassNodeAdapter(classNode);
	}
}
