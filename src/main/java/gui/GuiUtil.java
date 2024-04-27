package gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import general.ProductInfo;

final class GuiUtil {
	public static final int PAD = 8;

	public static String formatTitle(String title) {
		return (title != null) ? MessageFormat.format("{0} – {1}", ProductInfo.NAME, title) : ProductInfo.NAME;
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

	public static JButton createButton(String text, ActionListener action) {
		JButton button = new JButton(text);
		button.addActionListener(action);
		return button;
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
