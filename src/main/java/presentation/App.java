package presentation;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import datasource.ConfigLoader;
import datasource.Configuration;
import datasource.FilesLoader;
import domain.Check;
import domain.Message;
import domain.MessageLevel;
import domain.javadata.ClassData;
import domain.javadata.ClassNodeAdapter;

class App {
	private FilesLoader filesLoader;
	private ConfigLoader configLoader;

	App(FilesLoader filesLoader, ConfigLoader configLoader) {
		this.filesLoader = filesLoader;
		this.configLoader = configLoader;
	}

	boolean run(Check[] checks) {
		Set<byte[]> classFiles;
		Configuration config;
		try {
			classFiles = this.filesLoader.loadFiles();
		} catch (IOException ex) {
			System.err.println("Error loading classes: " + ex.getMessage());
			return false;
		}
		if (configLoader == null) {
			config = new Configuration(Map.of());
		} else {
			try {
				config = this.configLoader.loadConfig();
			} catch (IOException ex) {
				System.err.println("Error loading config: " + ex.getMessage());
				return false;
			}
		}

		Map<String, ClassData> classes = readInClasses(classFiles);
		Map<MessageLevel, Integer> msgTotals = runAllChecksAndPrintResults(checks, classes, config);
		return msgTotals.get(MessageLevel.ERROR) == 0;
	}

	private static Map<String, ClassData> readInClasses(Set<byte[]> classFiles) {
		Map<String, ClassData> classes = new HashMap<>();
		for (byte[] classFile : classFiles) {
			ClassData classData = new ClassNodeAdapter(classFile);
			classes.put(classData.getFullName(), classData);
		}
		return classes;
	}

	private static Map<MessageLevel, Integer> runAllChecksAndPrintResults(
		Check[] checks, Map<String, ClassData> classes, Configuration config
	) {
		Map<MessageLevel, Integer> msgTotals = initMsgTotals();
		for (Check check : checks) {
			runCheckAndPrintResults(check, classes, config, msgTotals);
		}
		printTotals(msgTotals);
		return msgTotals;
	}

	private static void runCheckAndPrintResults(
		Check check, Map<String, ClassData> classes, Configuration config, Map<MessageLevel, Integer> msgTotals
	) {
		Set<Message> generatedMsgs = check.run(classes, config);
			System.out.println(MessageFormat.format("Check {0}:", check.getName()));
			for (Message msg : generatedMsgs) {
				msgTotals.put(msg.level, msgTotals.get(msg.level) + 1);
				System.out.println("\t" + msg);
			}
			if (generatedMsgs.isEmpty()) {
				System.out.println("\t(no messages)");
			}
			System.out.println();
	}

	private static void printTotals(Map<MessageLevel, Integer> msgTotals) {
		System.out.println(MessageFormat.format(
			"Totals: {0} errors, {1} warnings, {2} info",
			msgTotals.get(MessageLevel.ERROR),
			msgTotals.get(MessageLevel.WARNING),
			msgTotals.get(MessageLevel.INFO)
		));
	}

	private static Map<MessageLevel, Integer> initMsgTotals() {
		Map<MessageLevel, Integer> msgTotals = new HashMap<>();
		for (MessageLevel level : MessageLevel.values()) {
			msgTotals.put(level, 0);
		}
		return msgTotals;
	}
}
