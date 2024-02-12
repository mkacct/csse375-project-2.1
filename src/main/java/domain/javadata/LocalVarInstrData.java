package domain.javadata;

public interface LocalVarInstrData extends InstrData {
	/**
	 * @return the variable's name, or null if unknown
	 */
	String getVarName();

	/**
	 * @return the full name of the variable's type, or null if unknown
	 */
	String getVarTypeFullName();

	VariableOperation getOperation();
}
