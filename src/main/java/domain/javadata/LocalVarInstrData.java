package domain.javadata;

public interface LocalVarInstrData extends InstrData {
	String getVarName();
	String getVarTypeFullName();

	VariableOperation getOperation();
}
