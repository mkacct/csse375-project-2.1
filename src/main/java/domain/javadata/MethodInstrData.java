package domain.javadata;

public interface MethodInstrData extends InstrData {
	String getMethodOwnerFullName();
	String getMethodName();
	String getMethodReturnTypeFullName();
}
