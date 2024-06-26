package domain;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Set;

/**
 * An error, warning, or info message generated by a check.
 */
public final class Message {
	public final MessageLevel level;
	public final String text;
	private final Set<String> classFullNames;

	public Message(MessageLevel level, String text, Set<String> classFullNames) {
		this.level = level;
		this.text = text;
		this.classFullNames = classFullNames;
	}

	public Message(MessageLevel level, String text, String classFullName) {
		this(level, text, Set.of(classFullName));
	}

	public Message(MessageLevel level, String text) {
		this(level, text, Set.of());
	}

	public Set<String> getClassFullNames() {
		return Set.copyOf(this.classFullNames);
	}

	@Override
	public String toString() {
		return MessageFormat.format(
			"[{0}] {1}",
			this.level.abbreviation.toUpperCase(), this.toStringWithoutLevel()
		);
	}

	public String toStringWithoutLevel() {
		String msg = this.text;
		String classFullNamesStr = String.join(", ", this.classFullNames.toArray(new String[this.classFullNames.size()]));
		if (!classFullNamesStr.isEmpty()) {
			msg = MessageFormat.format(
				"{0} ({1})",
				msg, classFullNamesStr
			);
		}
		return msg;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {return true;}
		if (obj == null || !(obj instanceof Message)) {return false;}
		Message other = (Message)obj;
		return (this.level == other.level) && this.text.equals(other.text) && this.classFullNames.equals(other.classFullNames);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.level, this.text, this.classFullNames);
	}
}
