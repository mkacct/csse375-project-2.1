package gui;

import java.awt.Component;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import com.formdev.flatlaf.icons.FlatOptionPaneErrorIcon;
import com.formdev.flatlaf.icons.FlatOptionPaneInformationIcon;
import com.formdev.flatlaf.icons.FlatOptionPaneWarningIcon;

import domain.Message;
import domain.MessageLevel;
import gui.App.CheckResults;

class MessageDisplay extends JScrollPane {
	private static final String NO_RESULTS_TEXT = "Messages will be displayed here once checks have been run.";
	private static final String NO_CHECKS_RUN_TEXT = "No checks were run. Please enable at least one check to see output.";
	private static final String NO_MESSAGES_TEXT = "(no messages)";

	private final JTree tree;

	MessageDisplay() {
		super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		this.tree = this.initTree();

		this.clearMessages();
	}

	private JTree initTree() {
		JTree tree = new JTree();
		this.setViewportView(tree);
		ToolTipManager.sharedInstance().registerComponent(tree);
		tree.setCellRenderer(new MessageCellRenderer());
		return tree;
	}

	void clearMessages() {
		this.displayErrorMessageOnTree(NO_RESULTS_TEXT);
	}

	void displayMessages(List<CheckResults> checkResults) {
		if (checkResults.isEmpty()) {
			this.displayErrorMessageOnTree(NO_CHECKS_RUN_TEXT);
			return;
		}
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		createNodes(root, checkResults);
		this.tree.setModel(new DefaultTreeModel(root));
		this.tree.setRootVisible(false);
		this.tree.setShowsRootHandles(true);
		this.tree.setEnabled(true);
		for (int i = 0; i < this.tree.getRowCount(); i++) {
			this.tree.expandRow(i);
		}
		this.getVerticalScrollBar().setValue(0);
	}

	private void displayErrorMessageOnTree(String errorMessage) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(errorMessage);
		this.tree.setModel(new DefaultTreeModel(root));
		this.tree.setRootVisible(true);
		this.tree.setShowsRootHandles(false);
		this.tree.setEnabled(false);
	}

	private static void createNodes(DefaultMutableTreeNode root, List<CheckResults> checkResults) {
		for (CheckResults resultsEntry : checkResults) {
			DefaultMutableTreeNode checkNode = new DefaultMutableTreeNode(formatCheckFolderText(resultsEntry));
			root.add(checkNode);
			List<Message> messages = resultsEntry.getMessages();
			if (!messages.isEmpty()) {
				for (Message message : messages) {
					checkNode.add(new DefaultMutableTreeNode(message));
				}
			} else {
				checkNode.add(new DefaultMutableTreeNode(NO_MESSAGES_TEXT));
			}
		}
	}

	private static String formatCheckFolderText(CheckResults resultsEntry) {
		return MessageFormat.format("{0} ({1})", resultsEntry.checkName, resultsEntry.getMessages().size());
	}

	private static class MessageCellRenderer extends DefaultTreeCellRenderer {
		private static final Map<MessageLevel, Icon> ICON_MAP = Map.of(
			MessageLevel.ERROR, GuiUtil.genScaledIcon(new FlatOptionPaneErrorIcon(), 0.5),
			MessageLevel.WARNING, GuiUtil.genScaledIcon(new FlatOptionPaneWarningIcon(), 0.5),
			MessageLevel.INFO, GuiUtil.genScaledIcon(new FlatOptionPaneInformationIcon(), 0.5)
		);

		@Override
		public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean isSelected,
			boolean isExpanded,
			boolean isLeaf,
			int row,
			boolean hasFocus
		) {
			super.getTreeCellRendererComponent(tree, value, isSelected, isExpanded, isLeaf, row, hasFocus);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			Object userObject = node.getUserObject();
			if (userObject instanceof Message) {
				Message message = (Message)userObject;
				this.setIcon(ICON_MAP.get(message.level));
				String text = message.toStringWithoutLevel();
				this.setText(text);
				this.setToolTipText(text);
			} else {
				this.setToolTipText(null);
			}
			return this;
		}
	}
}
