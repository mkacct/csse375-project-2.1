package domain;

public enum MessageLevel {
	ERROR("error", "err"),
	WARNING("warning", "warn"),
	INFO("info", "info");

	public final String name;
	public final String abbreviation;

	private MessageLevel(String name, String abbreviation) {
		this.name = name;
		this.abbreviation = abbreviation;
	}
}
