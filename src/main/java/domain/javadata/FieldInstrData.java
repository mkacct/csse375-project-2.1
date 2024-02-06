package domain.javadata;

public interface FieldInstrData extends InstrData {
	String getFieldOwnerFullName();
	String getFieldName();
	String getFieldTypeFullName();

	VariableOperation getOperation();
}
