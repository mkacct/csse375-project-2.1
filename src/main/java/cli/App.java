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
import domain.CheckUtil;
import domain.Message;
import domain.MessageLevel;
import domain.javadata.ClassDataCollection;

class App {
	private static final Map<MessageLevel, TerminalTextColor> MESSAGE_LEVEL_COLORS = Map.of(
		MessageLevel.ERROR, TerminalTextColor.RED,
		MessageLevel.WARNING, TerminalTextColor.YELLOW,
		MessageLevel.INFO, TerminalTextColor.BLUE
	);

	private final FilesLoader filesLoader;
	private final ConfigLoader configLoader;

	private final PrintStream outStream;
	private final PrintStream errStream;

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
			classFiles = this.filesLoader.loadFiles(CheckUtil.CLASS_FILE_EXT);
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

		ClassDataCollection classes = CheckUtil.readInClasses(classFiles);
		Map<MessageLevel, Integer> msgTotals = new HashMap<MessageLevel, Integer>();
		int numChecksRun = CheckUtil.runAllChecks(checks, classes, config, msgTotals, this::printCheckResults);
		this.outStream.println(MessageFormat.format("Checks run: {0}", numChecksRun));
		this.printTotals(msgTotals);
		return msgTotals.get(MessageLevel.ERROR) == 0;
	}

	private void printCheckResults(String checkName, Set<Message> generatedMsgs) {
		this.outStream.println(MessageFormat.format("Check {0}:", checkName));
		for (Message msg : generatedMsgs) {
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
}
