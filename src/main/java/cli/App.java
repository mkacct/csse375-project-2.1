package cli;

import java.io.IOException;
import java.io.PrintStream;
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
import domain.javadata.ClassDataCollection;
import domain.javadata.ClassReaderUtil;

class App {
	private static final String CLASS_FILE_EXT = "class";
	private static final String ENABLE_KEY_PREFIX = "enable_";
	private static final String SKIP_UNMARKED_CHECKS_KEY = "skipUnmarkedChecks";

	private static final Map<MessageLevel, TerminalTextColor> MESSAGE_LEVEL_COLORS = Map.of(
		MessageLevel.ERROR, TerminalTextColor.RED,
		MessageLevel.WARNING, TerminalTextColor.YELLOW,
		MessageLevel.INFO, TerminalTextColor.BLUE
	);

	private FilesLoader filesLoader;
	private ConfigLoader configLoader;

	private PrintStream outStream;
	private PrintStream errStream;

	App(FilesLoader filesLoader, ConfigLoader configLoader, PrintStream outStream, PrintStream errStream) {
		this.filesLoader = filesLoader;
		this.configLoader = configLoader;
		this.outStream = outStream;
		this.errStream = errStream;
	}

	boolean run(Check[] checks) {
		Set<byte[]> classFiles;
		Configuration config;
		try {
			classFiles = this.filesLoader.loadFiles(CLASS_FILE_EXT);
		} catch (IllegalStateException ex) { // dir not found
			this.errStream.println(ex.getMessage());
			return false;
		} catch (IOException ex) {
			this.errStream.println("Error loading classes: " + ex.getMessage());
			return false;
		}
		if (configLoader == null) {
			config = new Configuration(Map.of());
		} else {
			try {
				config = this.configLoader.loadConfig();
			} catch (IOException ex) {
				this.errStream.println("Error loading config: " + ex.getMessage());
				return false;
			}
		}

		ClassDataCollection classes = readInClasses(classFiles);
		Map<MessageLevel, Integer> msgTotals = this.runAllChecksAndPrintResults(checks, classes, config);
		return msgTotals.get(MessageLevel.ERROR) == 0;
	}

	private static ClassDataCollection readInClasses(Set<byte[]> classFiles) {
		ClassDataCollection classes = new ClassDataCollection();
		for (byte[] classFile : classFiles) {
			ClassData classData = ClassReaderUtil.read(classFile);
			classes.add(classData);
		}
		return classes;
	}

	private Map<MessageLevel, Integer> runAllChecksAndPrintResults(
		Check[] checks, ClassDataCollection classes, Configuration config
	) {
		boolean skipUnmarked = Boolean.TRUE.equals(this.configBoolOrNull(config, SKIP_UNMARKED_CHECKS_KEY));
		Map<MessageLevel, Integer> msgTotals = initMsgTotals();
		int numChecksRun = 0;
		for (Check check : checks) {
			Boolean configVal = this.configBoolOrNull(config, ENABLE_KEY_PREFIX + check.name);
			if (skipUnmarked ? Boolean.TRUE.equals(configVal) : check.isEnabled(configVal)) {
				this.runCheckAndPrintResults(check, classes, config, msgTotals);
				numChecksRun++;
			}
		}
		this.outStream.println(MessageFormat.format("Checks run: {0}", numChecksRun));
		this.printTotals(msgTotals);
		return msgTotals;
	}

	private Boolean configBoolOrNull(Configuration config, String key) {
		try {
			return config.getBoolean(key);
		} catch (IllegalArgumentException ex) {
			return null;
		} catch (ClassCastException ex) {
			this.errStream.println(MessageFormat.format(
				"Error in config: property \"{0}\", if present, should be boolean",
				key
			));
			return null;
		}
	}

	private void runCheckAndPrintResults(
		Check check, ClassDataCollection classes, Configuration config, Map<MessageLevel, Integer> msgTotals
	) {
		Set<Message> generatedMsgs = check.run(classes, config);
			this.outStream.println(MessageFormat.format("Check {0}:", check.name));
			for (Message msg : generatedMsgs) {
				msgTotals.put(msg.level, msgTotals.get(msg.level) + 1);
				this.outStream.println(MessageFormat.format("\t{0}", colorMessageTag(msg)));
			}
			if (generatedMsgs.isEmpty()) {
				this.outStream.println(MessageFormat.format("\t{0}",TerminalTextColor.BLACK.applyTo("(no messages)")));
			}
			this.outStream.println();
	}

	private static String colorMessageTag(Message msg) {
		String[] parts = msg.toString().split(" ", 2);
		String tag = parts[0];
		String rest = parts[1];
		return MessageFormat.format("{0} {1}", MESSAGE_LEVEL_COLORS.get(msg.level).applyTo(tag), rest);
	}

	private void printTotals(Map<MessageLevel, Integer> msgTotals) {
		int totalMsgs = 0;
		for (int subtotal : msgTotals.values()) {
			totalMsgs += subtotal;
		}
		if (totalMsgs == 0) {
			this.outStream.println(TerminalTextColor.GREEN.applyTo("No messages generated."));
			return;
		} else {
			List<String> totalsTerms = new ArrayList<>();
			generateTotalsTerm(totalsTerms, MessageLevel.ERROR, msgTotals.get(MessageLevel.ERROR));
			generateTotalsTerm(totalsTerms, MessageLevel.WARNING, msgTotals.get(MessageLevel.WARNING));
			generateTotalsTerm(totalsTerms, MessageLevel.INFO, msgTotals.get(MessageLevel.INFO));
			this.outStream.println(MessageFormat.format("Totals: {0}", String.join(", ", totalsTerms)));
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
