package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import domain.MessageLevel;
import general.ProductInfo;

class MainWindow extends JFrame implements Reloadable {
	private static final Dimension MIN_SIZE = new Dimension(640, 360);
	private static final int INIT_HEIGHT = 480;

	private final App app;

	MainWindow(App app) {
		super(GuiUtil.formatTitle(null));
		this.app = app;
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		GuiUtil.initPaddedContentPane(this);
		this.initHeader();
		MainPanel mainPanel = this.initMainPanel();
		this.initFooter();
		this.getRootPane().setDefaultButton(mainPanel.runButton);

		GuiUtil.initWindow(this, null, MIN_SIZE, INIT_HEIGHT);
		this.app.addReloader(this);
		this.checkForConfigLoadException();
	}

	private Header initHeader() {
		Header header = new Header();
		this.add(header, BorderLayout.PAGE_START);
		return header;
	}

	private MainPanel initMainPanel() {
		MainPanel mainPanel = new MainPanel();
		this.add(mainPanel, BorderLayout.CENTER);
		return mainPanel;
	}

	private Footer initFooter() {
		Footer footer = new Footer();
		this.add(footer, BorderLayout.PAGE_END);
		return footer;
	}

	private void exit(int status) {
		this.dispose();
		System.exit(status);
	}

	private void checkForConfigLoadException() {
		Exception configLoadEx = this.app.retrieveConfigLoadEx();
		if (configLoadEx != null) {
			boolean stay = this.askWhetherToProceedWithBadConfig(configLoadEx);
			if (!stay) {
				this.exit(1);
				return;
			}
		}
	}

	private boolean askWhetherToProceedWithBadConfig(Exception configLoadEx) {
		int choice = JOptionPane.showOptionDialog(
			this,
			MessageFormat.format(
				"Could not load configuration file {0}. The file may not be valid JSON.\n"
				+ "Select \"Quit\" if you want to fix the issue manually. "
				+ "If you select \"Continue anyway\", any changes will overwrite the file.\n\n"
				+ "Error message:\n"
				+ "{1}",
				App.CONFIG_PATH, configLoadEx.getMessage()
			),
			GuiUtil.formatTitle(null),
			JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null,
			new String[] {"Quit", "Continue anyway"}, "Quit"
		);
		return choice == 1;
	}

	private void openSettings() {
		new SettingsWindow(this, this.app);
	}

	@Override
	public void reload() {
		this.checkForConfigSaveException();
	}

	private void checkForConfigSaveException() {
		Exception configSaveEx = this.app.retrieveConfigSaveEx();
		if (configSaveEx != null) {
			GuiUtil.showError(this, MessageFormat.format(
				"Failed to save changes to configuration file {0}:\n{1}",
				App.CONFIG_PATH, configSaveEx.getMessage()
			));
		}
	}

	private class Header extends JPanel {
		private static final String SETTINGS_BUTTON_LABEL = "Settings…";

		private Header() {
			super(new BorderLayout(GuiUtil.PAD, GuiUtil.PAD));

			this.initFilePicker();
			this.initSettingsButton();
		}

		private FilePicker initFilePicker() {
			FilePicker filePicker = new FilePicker(MainWindow.this.app.getTargetPath(), MainWindow.this.app::setTargetPath);
			this.add(filePicker, BorderLayout.CENTER);
			return filePicker;
		}

		private JButton initSettingsButton() {
			JButton settingsButton = GuiUtil.createButton(SETTINGS_BUTTON_LABEL, (e) -> {MainWindow.this.openSettings();});
			this.add(settingsButton, BorderLayout.LINE_END);
			return settingsButton;
		}
	}

	private class MainPanel extends JPanel implements Reloadable {
		private static final String NO_RESULTS_MSG = "No results yet";
		private static final String RUN_BUTTON_LABEL = "Run checks";

		private final JLabel messageSummary;
		private final JButton runButton;
		private final MessageDisplay messageDisplay;

		private MainPanel() {
			super(new BorderLayout(GuiUtil.PAD, GuiUtil.PAD));

			JPanel header = new JPanel(new BorderLayout(GuiUtil.PAD, GuiUtil.PAD));
			this.messageSummary = this.initMessageSummary(header);
			this.runButton = this.initRunButton(header);
			this.add(header, BorderLayout.PAGE_START);

			this.messageDisplay = this.initMessageDisplay();

			this.reload();

			MainWindow.this.app.addReloader(this);
		}

		private JLabel initMessageSummary(JPanel header) {
			JLabel messageSummary = new JLabel();
			header.add(messageSummary, BorderLayout.LINE_START);
			return messageSummary;
		}

		private JButton initRunButton(JPanel header) {
			JButton runButton = GuiUtil.createButton(RUN_BUTTON_LABEL, (e) -> {this.runChecks();});
			header.add(runButton, BorderLayout.LINE_END);
			return runButton;
		}

		private MessageDisplay initMessageDisplay() {
			MessageDisplay messageDisplay = new MessageDisplay();
			this.add(messageDisplay, BorderLayout.CENTER);
			return messageDisplay;
		}

		private void runChecks() {
			GuiUtil.withWaitCursor(MainWindow.this, () -> {
				try {
					MainWindow.this.app.runChecks();
				} catch (IOException ex) {
					GuiUtil.showError(MainWindow.this, ex.getMessage());
				}
			});
		}

		@Override
		public void reload() {
			this.runButton.setEnabled(MainWindow.this.app.canRunNow());

			if (MainWindow.this.app.hasResults()) {
				String summaryText = MessageFormat.format(
					"{0} checks run; {1}",
					MainWindow.this.app.getNumChecksRun(),
					this.formatTotals(MainWindow.this.app.getMessageTotals())
				);
				this.messageSummary.setText(summaryText);
				this.messageDisplay.displayMessages(MainWindow.this.app.getCheckResults());
			} else {
				this.messageSummary.setText(NO_RESULTS_MSG);
				this.messageDisplay.clearMessages();
			}
		}

		private String formatTotals(Map<MessageLevel, Integer> msgTotals) {
			int totalMsgs = 0;
			for (int subtotal : msgTotals.values()) {
				totalMsgs += subtotal;
			}
			if (totalMsgs == 0) {
				return "No messages generated";
			} else {
				List<String> totalsTerms = new ArrayList<>();
				this.generateTotalsTerm(totalsTerms, MessageLevel.ERROR, msgTotals.get(MessageLevel.ERROR));
				this.generateTotalsTerm(totalsTerms, MessageLevel.WARNING, msgTotals.get(MessageLevel.WARNING));
				this.generateTotalsTerm(totalsTerms, MessageLevel.INFO, msgTotals.get(MessageLevel.INFO));
				return String.join(", ", totalsTerms);
			}
		}

		private void generateTotalsTerm(List<String> totalsTerms, MessageLevel level, int count) {
			if (count > 0) {
				totalsTerms.add(MessageFormat.format("{0} {1}", count, level.abbreviation));
			}
		}
	}

	private class Footer extends JPanel {
		private Footer() {
			super(new BorderLayout(GuiUtil.PAD, GuiUtil.PAD));

			this.initVerLabel();
		}

		private JLabel initVerLabel() {
			JLabel verLabel = new JLabel(MessageFormat.format("version {0}", ProductInfo.VERSION));
			verLabel.setHorizontalAlignment(JLabel.CENTER);
			this.add(verLabel, BorderLayout.CENTER);
			return verLabel;
		}
	}
}
