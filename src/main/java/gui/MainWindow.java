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
import javax.swing.border.EmptyBorder;

import domain.MessageLevel;
import general.ProductInfo;

class MainWindow extends JFrame {
	private static final int MIN_WIDTH = 640, MIN_HEIGHT = 360;

	private App app;

	private Header header;
	private MainPanel mainPanel;
	private Footer footer;

	MainWindow(App app) {
		super(GuiUtil.formatTitle(null));
		this.app = app;
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		this.initContents();
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.checkForConfigLoadException();
	}

	private void initContents() {
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(GuiUtil.PAD, GuiUtil.PAD, GuiUtil.PAD, GuiUtil.PAD));
		contentPane.setLayout(new BorderLayout(GuiUtil.PAD, GuiUtil.PAD));
		this.setContentPane(contentPane);

		this.header = new Header();
		this.add(this.header, BorderLayout.PAGE_START);
		this.mainPanel = new MainPanel();
		this.add(this.mainPanel, BorderLayout.CENTER);
		this.footer = new Footer();
		this.add(this.footer, BorderLayout.PAGE_END);

		this.getRootPane().setDefaultButton(this.mainPanel.runButton);
	}

	private void exit(int status) {
		this.dispose();
		System.exit(status);
	}

	private void checkForConfigLoadException() {
		Exception ex = this.app.getConfigLoadEx();
		if (ex != null) {
			boolean stay = this.askIfProceedWithBadConfig(ex);
			if (!stay) {
				this.exit(1);
				return;
			}
		}
	}

	private boolean askIfProceedWithBadConfig(Exception ex) {
		int choice = JOptionPane.showOptionDialog(
			this,
			MessageFormat.format(
				"Could not load configuration file {0}. The file may not be valid JSON.\n"
				+ "Select \"Quit\" if you want to fix the issue manually. "
				+ "If you select \"Continue anyway\", any changes will overwrite the file.\n\n"
				+ "Error message:\n"
				+ "{1}",
				App.CONFIG_PATH, ex.getMessage()
			),
			GuiUtil.formatTitle(null),
			JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null,
			new String[] {"Quit", "Continue anyway"}, "Quit"
		);
		return choice == 1;
	}

	private void openSettings() {
		// TODO: make settings dialog
		// new SettingsDialog(this);
		GuiUtil.showError(this, "NYI");
	}

	private class Header extends JPanel {
		private static final String SETTINGS_BUTTON_LABEL = "Settings…";

		private Header() {
			super();
			this.initContents();
		}

		private void initContents() {
			this.setLayout(new BorderLayout(GuiUtil.PAD, GuiUtil.PAD));
			this.add(new FilePicker(MainWindow.this.app.getTargetPath(), MainWindow.this.app::setTargetPath), BorderLayout.CENTER);
			this.add(GuiUtil.createButton(SETTINGS_BUTTON_LABEL, (e) -> {MainWindow.this.openSettings();}), BorderLayout.LINE_END);
		}
	}

	private class MainPanel extends JPanel implements Reloadable {
		private static final String NO_RESULTS_MSG = "No results yet";
		private static final String RUN_BUTTON_LABEL = "Run checks";

		private JLabel messageSummary;
		private JButton runButton;
		private MessageDisplay messageDisplay;

		private MainPanel() {
			super();
			this.initContents();
			MainWindow.this.app.addReloader(this);
		}

		private void initContents() {
			this.setLayout(new BorderLayout(GuiUtil.PAD, GuiUtil.PAD));

			JPanel header = new JPanel();
			header.setLayout(new BorderLayout(GuiUtil.PAD, GuiUtil.PAD));
			this.messageSummary = new JLabel();
			header.add(this.messageSummary, BorderLayout.LINE_START);
			this.runButton = GuiUtil.createButton(RUN_BUTTON_LABEL, (e) -> {this.runChecks();});
			header.add(this.runButton, BorderLayout.LINE_END);
			this.add(header, BorderLayout.PAGE_START);

			this.messageDisplay = new MessageDisplay();
			this.add(this.messageDisplay, BorderLayout.CENTER);

			this.reload();
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
			super();
			this.initContents();
		}

		private void initContents() {
			this.setLayout(new BorderLayout(GuiUtil.PAD, GuiUtil.PAD));
			JLabel verLabel = new JLabel(MessageFormat.format("version {0}", ProductInfo.VERSION));
			verLabel.setHorizontalAlignment(JLabel.CENTER);
			this.add(verLabel, BorderLayout.CENTER);
		}
	}
}
