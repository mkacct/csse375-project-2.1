package domain.javadata;

import java.util.List;

import org.objectweb.asm.tree.LocalVariableNode;

final class LocalVariableUtil {
	static LocalVariableNode findLocalVariableNode(int var, List<LocalVariableNode> localVars) {
		if (localVars == null) {return null;}
		try {
			return localVars.get(var);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
}
