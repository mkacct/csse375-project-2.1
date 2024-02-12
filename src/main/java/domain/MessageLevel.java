package domain;

public enum MessageLevel {
	ERROR("ERR"),
	WARNING("WARN"),
	INFO("INFO");

	public final String abbreviation;

	private MessageLevel(String abbreviation) {
		this.abbreviation = abbreviation;
	}
}
