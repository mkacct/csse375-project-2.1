package domain.javadata;

/**
 * A representation of a Java bytecode instruction.
 */
public interface InstrData {
	/**
	 * @return the type of instruction OR NULL if the instruction is of an unsupported type
	 */
	InstrType getInstrType();
}
