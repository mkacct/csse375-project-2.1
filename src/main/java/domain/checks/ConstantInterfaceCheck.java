package domain.checks;

import java.util.HashSet;
import java.util.Set;

import datasource.Configuration;
import domain.Check;
import domain.Message;
import domain.MessageLevel;
import domain.javadata.ClassData;
import domain.javadata.ClassDataCollection;
import domain.javadata.ClassType;

public class ConstantInterfaceCheck extends Check {
	private static final String NAME = "constantInterface";

	private static final String ALLOW_MARKER_INTERFACES_KEY = "allowMarkerInterfaces";

	public ConstantInterfaceCheck() {
		super(NAME);
	}

	@Override
	public Set<Message> run(ClassDataCollection classes, Configuration config) {
		boolean allowMarkerInterfaces = config.getBoolean(ALLOW_MARKER_INTERFACES_KEY, false);
		Set<Message> messages = new HashSet<>();
		for (ClassData classData : classes) {
			if (classData.getClassType() != ClassType.INTERFACE) {continue;}
			if (classData.getMethods().size() == 0) {
				boolean isMarker = classData.getFields().size() == 0;
				if (allowMarkerInterfaces && isMarker) {continue;}
				messages.add(new Message(
					MessageLevel.WARNING,
					isMarker ? "Empty interface" : "Constant interface",
					classData.getFullName()
				));
			}
		}
		return messages;
	}
}
