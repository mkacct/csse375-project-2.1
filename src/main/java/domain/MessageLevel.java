package domain;

public enum MessageLevel {
	ERROR("err"),
	WARNING("warn"),
	INFO("info");

	public final String abbreviation;

	private MessageLevel(String abbrev) {
		this.abbreviation = abbrev;
	}
}
