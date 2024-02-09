package presentation;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private static final String ENABLE_KEY_PREFIX = "enable_";
	private static final String SKIP_UNMARKED_CHECKS_KEY = "skipUnmarkedChecks";

	private static final Map<MessageLevel, TerminalTextColor> MESSAGE_LEVEL_COLORS = Map.of(
		MessageLevel.ERROR, TerminalTextColor.RED,
		MessageLevel.WARNING, TerminalTextColor.YELLOW,
		MessageLevel.INFO, TerminalTextColor.BLUE
	);

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
		boolean skipUnmarked = readConfigBoolAndFallbackIfWrongType(config, SKIP_UNMARKED_CHECKS_KEY, false);
		Map<MessageLevel, Integer> msgTotals = initMsgTotals();
		int numChecksRun = 0;
		for (Check check : checks) {
			if (readConfigBoolAndFallbackIfWrongType(config, ENABLE_KEY_PREFIX + check.getName(), !skipUnmarked)) {
				runCheckAndPrintResults(check, classes, config, msgTotals);
				numChecksRun++;
			}
		}
		System.out.println(MessageFormat.format("Checks run: {0}", numChecksRun));
		printTotals(msgTotals);
		return msgTotals;
	}

	private static boolean readConfigBoolAndFallbackIfWrongType(Configuration config, String key, boolean fallback) {
		try {
			return config.getBoolean(key, fallback);
		} catch (ClassCastException ex) {
			System.err.println(MessageFormat.format(
				"Error in config: property \"{0}\", if present, should be boolean; defaulting to {1}",
				key, fallback
			));
			return fallback;
		}
	}

	private static void runCheckAndPrintResults(
		Check check, Map<String, ClassData> classes, Configuration config, Map<MessageLevel, Integer> msgTotals
	) {
		Set<Message> generatedMsgs = check.run(classes, config);
			System.out.println(MessageFormat.format("Check {0}:", check.getName()));
			for (Message msg : generatedMsgs) {
				msgTotals.put(msg.level, msgTotals.get(msg.level) + 1);
				System.out.println(MessageFormat.format("\t{0}", colorMessageTag(msg)));
			}
			if (generatedMsgs.isEmpty()) {
				System.out.println(MessageFormat.format("\t{0}",TerminalTextColor.BLACK.applyTo("(no messages)")));
			}
			System.out.println();
	}

	private static String colorMessageTag(Message msg) {
		String[] parts = msg.toString().split(" ", 2);
		String tag = parts[0];
		String rest = parts[1];
		return MessageFormat.format("{0} {1}", MESSAGE_LEVEL_COLORS.get(msg.level).applyTo(tag), rest);
	}

	private static void printTotals(Map<MessageLevel, Integer> msgTotals) {
		int totalMsgs = 0;
		for (int subtotal : msgTotals.values()) {
			totalMsgs += subtotal;
		}
		if (totalMsgs == 0) {
			System.out.println(TerminalTextColor.GREEN.applyTo("No messages generated."));
			return;
		} else {
			List<String> totalsTerms = new ArrayList<>();
			generateTotalsTerm(totalsTerms, MessageLevel.ERROR, msgTotals.get(MessageLevel.ERROR));
			generateTotalsTerm(totalsTerms, MessageLevel.WARNING, msgTotals.get(MessageLevel.WARNING));
			generateTotalsTerm(totalsTerms, MessageLevel.INFO, msgTotals.get(MessageLevel.INFO));
			System.out.println(MessageFormat.format("Totals: {0}", String.join(", ", totalsTerms)));
		}
	}

	private static void generateTotalsTerm(List<String> totalsTerms, MessageLevel level, int count) {
		if (count > 0) {
			totalsTerms.add(MESSAGE_LEVEL_COLORS.get(level).applyTo(
				MessageFormat.format("{0} {1}", count, level.abbreviation)
			));
		}
	}

	private static Map<MessageLevel, Integer> initMsgTotals() {
		Map<MessageLevel, Integer> msgTotals = new HashMap<>();
		for (MessageLevel level : MessageLevel.values()) {
			msgTotals.put(level, 0);
		}
		return msgTotals;
	}
}
