package gui;

import java.awt.BorderLayout;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.formdev.flatlaf.FlatClientProperties;

public class FilePicker extends JPanel {
	private static final int TEXT_FIELD_MIN_COLS = 20;
	private static final String PATH_FIELD_PLACEHOLDER_TEXT = "Path to directory";
	private static final String BROWSE_BUTTON_LABEL = "Browse…";

	private final Consumer<String> pathUpdateHandler;

	private JTextField pathField;

	public FilePicker(String targetPath, Consumer<String> pathUpdateHandler) {
		this.pathUpdateHandler = pathUpdateHandler;
		this.initContents(targetPath);
	}

	private void initContents(String targetPath) {
		this.setLayout(new BorderLayout(GuiUtil.PAD, GuiUtil.PAD));
		this.pathField = new JTextField(targetPath, TEXT_FIELD_MIN_COLS);
		this.pathField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, PATH_FIELD_PLACEHOLDER_TEXT);
		GuiUtil.addTextFieldDocumentUpdateListener(this.pathField, (e) -> {
			this.pathUpdateHandler.accept(this.getPath());
		});
		this.add(this.pathField, BorderLayout.CENTER);
		this.add(GuiUtil.createButton(BROWSE_BUTTON_LABEL, (e) -> {this.browse();}), BorderLayout.LINE_END);
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
