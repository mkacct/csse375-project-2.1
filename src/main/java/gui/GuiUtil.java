package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import general.ProductInfo;

final class GuiUtil {
	public static final int PAD = 8;

	public static String formatTitle(String title) {
		return (title != null) ? MessageFormat.format("{0} – {1}", ProductInfo.getName(), title) : ProductInfo.getName();
	}

	public static void showError(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, GuiUtil.formatTitle(null), JOptionPane.ERROR_MESSAGE);
	}

	public static void withWaitCursor(Component component, Runnable func) {
		component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			func.run();
		} finally {
			component.setCursor(Cursor.getDefaultCursor());
		}
	}

	public static void initWindow(Window window, Window parent, Dimension minSize, int initHeight) {
		window.setMinimumSize(minSize);
		window.pack();
		window.setSize(new Dimension(window.getWidth(), initHeight));
		window.setLocationRelativeTo(parent);
		window.setVisible(true);
	}

	public static JLabel createHeading(String text) {
		JLabel label = new JLabel(text);
		Font origFont = label.getFont();
		label.setFont(origFont.deriveFont(origFont.getSize() * 1.5f).deriveFont(Font.BOLD));
		return label;
	}

	public static JButton createButton(String text, ActionListener action) {
		JButton button = new JButton(text);
		button.addActionListener(action);
		return button;
	}

	public static void initPaddedContentPane(RootPaneContainer component) {
		JPanel contentPane = new JPanel(new BorderLayout(GuiUtil.PAD, GuiUtil.PAD));
		contentPane.setBorder(new EmptyBorder(GuiUtil.PAD, GuiUtil.PAD, GuiUtil.PAD, GuiUtil.PAD));
		component.setContentPane(contentPane);
	}

	public static void addTextFieldDocumentUpdateListener(JTextField textField, Consumer<DocumentEvent> func) {
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {func.accept(e);}

			@Override
			public void removeUpdate(DocumentEvent e) {func.accept(e);}

			@Override
			public void changedUpdate(DocumentEvent e) {func.accept(e);}
		});
	}

	public static Icon genScaledIcon(Icon icon, double scaleFactor) {
		BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		icon.paintIcon(null, g2, 0,0);
		double width = img.getWidth() * scaleFactor;
		double height = img.getHeight() * scaleFactor;
		BufferedImage scaledImg = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(scaleFactor, scaleFactor);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		scaledImg = scaleOp.filter(img, scaledImg);
		return new ImageIcon(scaledImg);
	}
}
