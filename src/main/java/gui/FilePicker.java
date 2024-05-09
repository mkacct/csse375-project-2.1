package gui;

import java.awt.BorderLayout;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.formdev.flatlaf.FlatClientProperties;

public class FilePicker extends JPanel {
	private static final int TEXT_FIELD_MIN_COLS = 20;
	private static final String PATH_FIELD_PLACEHOLDER_TEXT = "Path to directory";
	private static final String BROWSE_BUTTON_LABEL = "Browse…";

	private final Consumer<String> pathUpdateHandler;

	private final JTextField pathField;

	public FilePicker(String targetPath, Consumer<String> pathUpdateHandler) {
		super(new BorderLayout(GuiUtil.PAD, GuiUtil.PAD));
		this.pathUpdateHandler = pathUpdateHandler;

		this.pathField = this.initPathField(targetPath);
		this.initBrowseButton();
	}

	private JTextField initPathField(String targetPath) {
		JTextField pathField = new JTextField(targetPath, TEXT_FIELD_MIN_COLS);
		pathField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, PATH_FIELD_PLACEHOLDER_TEXT);
		this.add(pathField, BorderLayout.CENTER);
		GuiUtil.addTextFieldDocumentUpdateListener(pathField, (e) -> {
			this.pathUpdateHandler.accept(this.getPath());
		});
		return pathField;
	}

	private JButton initBrowseButton() {
		JButton browseButton = GuiUtil.createButton(BROWSE_BUTTON_LABEL, (e) -> {this.browse();});
		this.add(browseButton, BorderLayout.LINE_END);
		return browseButton;
	}

	private String getPath() {return this.pathField.getText();}
	private void setPath(String text) {this.pathField.setText(text);} // triggers pathUpdateHandler

	private void browse() {
		File startingDir = new File(this.getPath());
		if (!startingDir.isDirectory()) {startingDir = FileUtil.currentWorkingDirectory();}
		JFileChooser folderChooser = new JFileChooser(startingDir);
		folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = folderChooser.showOpenDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) {return;}
		this.setPath(folderChooser.getSelectedFile().getAbsolutePath());
	}
}
