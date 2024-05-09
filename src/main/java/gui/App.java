package gui;

import java.io.IOException;
import java.text.MessageFormat;
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
import datasource.configspec.ConfigSpec;
import datasource.configspec.ConfigSpecLoader;
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

	public final ConfigSpec configSpec;

	private final Check[] checks;
	private final ConfigRW configRW; // can be null in tests

	private Configuration config;
	private Exception configLoadEx = null;
	private Exception configSaveEx = null;

	// Fields for check results:
	private Map<MessageLevel, Integer> msgTotals;
	private List<CheckResults> checkResults;

	private Set<Reloadable> reloaders = new HashSet<Reloadable>();

	// To initialize app (including loading config):

	App(Check[] checks, ConfigSpecLoader configSpecLoader, ConfigRW configRW) {
		this.checks = checks;
		this.configRW = configRW;
		this.configSpec = configSpecLoader.loadConfigSpec();
		this.loadConfig();
		this.clearCheckResults();
	}

	private void loadConfig() {
		if ((this.configRW != null) && this.configRW.sourceExists()) {
			try {
				this.config = configRW.loadConfig();
			} catch (IOException ex) {
				this.configLoadEx = ex;
				this.config = new Configuration(Map.of());
			}
		} else {
			this.config = new Configuration(Map.of());
		}
	}

	Exception retrieveConfigLoadEx() {
		Exception ex = this.configLoadEx;
		this.configLoadEx = null;
		return ex;
	}

	private void clearCheckResults() {
		this.msgTotals = null;
		this.checkResults = null;
	}

	// To save config:

	// you should call triggerReload() after saving config (to ensure error messages are displayed)
	private void saveConfig() {
		this.configSaveEx = null;
		if (this.configRW != null) {
			try {
				this.configRW.saveConfig(this.config);
			} catch (IOException ex) {
				this.configSaveEx = ex;
			}
		}
	}

	Exception retrieveConfigSaveEx() {
		Exception ex = this.configSaveEx;
		this.configSaveEx = null;
		return ex;
	}

	// To work with target path:

	String getTargetPath() {
		return this.config.getString(TARGET_PATH_KEY, "");
	}

	boolean canRunNow() {
		return !this.getTargetPath().isEmpty();
	}

	void setTargetPath(String targetPath) {
		this.clearCheckResults();
		this.config = this.config.applyChanges(Map.of(TARGET_PATH_KEY, targetPath));
		this.saveConfig();
		this.triggerReload();
	}

	// To run checks:

	void runChecks() throws IOException {
		if (!this.canRunNow()) {throw new IllegalStateException("App is not in a state to run checks");}
		this.clearCheckResults();
		try {
			FilesLoader filesLoader = new DirLoader(this.getTargetPath());
			Set<byte[]> classFiles;
			try {
				classFiles = filesLoader.loadFiles(CheckUtil.CLASS_FILE_EXT);
			} catch (IllegalStateException ex) { // dir not found
				throw new IOException(ex.getMessage());
			} catch (IOException ex) {
				throw new IOException(MessageFormat.format("Error loading classes: {0}", ex.getMessage()));
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

	// To work with config:

	Configuration getConfig() {
		return this.config;
	}

	void updateConfig(Map<String, Object> changes) {
		this.clearCheckResults();
		this.config = this.config.applyChanges(changes);
		this.saveConfig();
		this.triggerReload();
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
