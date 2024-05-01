package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import datasource.ConfigRW;
import datasource.Configuration;
import datasource.DirLoader;
import datasource.FilesLoader;
import domain.Check;
import domain.CheckUtil;
import domain.Message;
import domain.MessageLevel;
import domain.javadata.ClassDataCollection;

/**
 * Sits between the GUI implementation and the domain layer.
 * Independent of the framework used to implement the GUI (ie. this file knows nothing about Swing).
 */
class App {
	static final String CONFIG_PATH = ".lpguiconfig.json";

	private static final String TARGET_PATH_KEY = "guiAppTargetPath";

	private final Check[] checks;
	private final ConfigRW configLoader;

	private Configuration config;
	private String targetPath;
	private Exception configLoadEx = null;

	// Fields for check results:
	private Map<MessageLevel, Integer> msgTotals;
	private List<CheckResults> checkResults;

	private Set<Reloadable> reloaders = new HashSet<>();

	// To initialize app:

	App(Check[] checks, ConfigRW configLoader) {
		this.checks = checks;
		this.configLoader = configLoader;
		this.loadConfig();
		this.clearCheckResults();
	}

	private void loadConfig() {
		if ((this.configLoader != null) && this.configLoader.sourceExists()) {
			try {
				this.config = configLoader.loadConfig();
			} catch (IOException ex) {
				this.configLoadEx = ex;
				this.config = new Configuration(Map.of());
			}
		} else {
			this.config = new Configuration(Map.of());
		}

		this.targetPath = this.config.getString(TARGET_PATH_KEY, "");
	}

	Exception getConfigLoadEx() {
		return this.configLoadEx;
	}

	private void clearCheckResults() {
		this.msgTotals = null;
		this.checkResults = null;
	}

	// To work with target path:

	boolean canRunNow() {
		return !this.targetPath.isEmpty();
	}

	String getTargetPath() {
		return this.targetPath;
	}

	void setTargetPath(String targetPath) {
		this.clearCheckResults();
		this.targetPath = targetPath;
		// TODO: save to config (valid or not)
		this.triggerReload();
	}

	// To run checks:

	void runChecks() throws IOException {
		if (!this.canRunNow()) {throw new IllegalStateException("App is not in a state to run checks");}
		this.clearCheckResults();
		try {
			FilesLoader filesLoader = new DirLoader(this.targetPath);
			Set<byte[]> classFiles;
			try {
				classFiles = filesLoader.loadFiles(CheckUtil.CLASS_FILE_EXT);
			} catch (IllegalStateException ex) { // dir not found
				throw new IOException(ex.getMessage());
			} catch (IOException ex) {
				throw new IOException("Error loading classes: " + ex.getMessage());
			}

			ClassDataCollection classes = CheckUtil.readInClasses(classFiles);
			this.msgTotals = new HashMap<MessageLevel, Integer>();
			this.checkResults = new ArrayList<CheckResults>();
			CheckUtil.runAllChecks(checks, classes, this.config, this.msgTotals, (checkName, msgs) -> {
				this.checkResults.add(new CheckResults(checkName, List.copyOf(msgs)));
			});
		} finally {
			this.triggerReload();
		}
	}

	// To get results:

	boolean hasResults() {
		return this.checkResults != null;
	}

	int getNumChecksRun() {
		if (!this.hasResults()) {return 0;}
		return this.checkResults.size();
	}

	Map<MessageLevel, Integer> getMessageTotals() {
		if (!this.hasResults()) {return null;}
		return Collections.unmodifiableMap(this.msgTotals);
	}

	List<CheckResults> getCheckResults() {
		if (!this.hasResults()) {return null;}
		return Collections.unmodifiableList(this.checkResults);
	}

	class CheckResults {
		public final String checkName;
		private final List<Message> msgs;

		CheckResults(String checkName, List<Message> msgs) {
			this.checkName = checkName;
			this.msgs = msgs;
		}

		public List<Message> getMessages() {
			return Collections.unmodifiableList(this.msgs);
		}
	}

	// To manage GUI reloading:

	void addReloader(Reloadable reloader) {
		this.reloaders.add(reloader);
	}

	void removeReloader(Reloadable reloader) {
		this.reloaders.remove(reloader);
	}

	private void triggerReload() {
		for (Reloadable reloader : this.reloaders) {
			reloader.reload();
		}
	}
}
