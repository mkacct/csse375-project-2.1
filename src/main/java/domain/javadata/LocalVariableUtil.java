package domain.javadata;

import java.util.List;

import org.objectweb.asm.tree.LocalVariableNode;

class LocalVariableUtil {
	static LocalVariableNode findLocalVariableNode(int var, List<LocalVariableNode> localVars) {
		try {
			return localVars.get(var);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
}
