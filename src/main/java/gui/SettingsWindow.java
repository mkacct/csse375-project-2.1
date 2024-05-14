package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import datasource.Configuration;
import datasource.configspec.ConfigSpec;
import domain.CheckUtil;

class SettingsWindow extends JDialog {
	private static final String TITLE = "Settings";
	private static final Dimension MIN_SIZE = new Dimension(640, 360);
	private static final int INIT_HEIGHT = 480;
	private static final int CTRL_PAD = 4;

	private static final Boolean[] BOOLEAN_OPTIONS = {false, null, true};
	private static final String[] BOOLEAN_OPTION_NAMES = {"No", "Default", "Yes"};
	private static final String NO_SELECTION_STRING = "[no selection]";

	private final App app;

	private final MainPanel mainPanel;

	SettingsWindow(Window parent, App app) {
		super(parent, GuiUtil.formatTitle(TITLE), ModalityType.APPLICATION_MODAL);
		this.app = app;
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				SettingsWindow.this.onCloseButton();
			}
		});

		GuiUtil.initPaddedContentPane(this);
		this.initSidebar();
		this.mainPanel = this.initMainPanel();
		Footer footer = this.initFooter();
		this.getRootPane().setDefaultButton(footer.saveButton);

		GuiUtil.initWindow(this, parent, MIN_SIZE, INIT_HEIGHT);
	}

	private Sidebar initSidebar() {
		Sidebar sidebar = new Sidebar();
		this.add(sidebar, BorderLayout.LINE_START);
		return sidebar;
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

	private String[] getSectionNames() {
		ConfigSpec configSpec = this.app.configSpec;
		String[] sectionNames = new String[configSpec.getSections().size()];
		for (int i = 0; i < sectionNames.length; i++) {
			sectionNames[i] = configSpec.getSections().get(i).title;
		}
		return sectionNames;
	}

	private void saveAndClose() {
		Map<String, Object> changes = new HashMap<String, Object>();
		for (SettingControl settingControl : this.mainPanel.getAllSettingControls()) {
			if (settingControl.doesSupplySettingValue()) {
				changes.put(settingControl.getSettingName(), settingControl.getSettingValue());
			}
		}
		this.app.updateConfig(changes);
		this.dispose();
	}

	private void onCloseButton() {
		int choice = JOptionPane.showConfirmDialog(
			this, "Save changes to configuration?", GuiUtil.formatTitle(null), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE
		);
		if (choice == JOptionPane.YES_OPTION) {
			this.saveAndClose();
		} else if (choice == JOptionPane.NO_OPTION) {
			this.dispose();
		}
	}

	private class Sidebar extends JPanel {
		private Sidebar() {
			super(new BorderLayout(GuiUtil.PAD, GuiUtil.PAD));

			JList<String> sectionList = this.initSectionList();
			this.add(new JScrollPane(
				sectionList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
			), BorderLayout.CENTER);
		}

		private JList<String> initSectionList() {
			JList<String> sectionList = new JList<String>(SettingsWindow.this.getSectionNames());
			sectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			sectionList.setSelectedIndex(0);
			sectionList.addListSelectionListener((e) -> {
				if (sectionList.getSelectedIndex() == -1) {return;}
				SettingsWindow.this.mainPanel.setSection(sectionList.getSelectedValue());
			});
			return sectionList;
		}
	}

	private class MainPanel extends JPanel {
		private final Set<SettingsSectionPanel> sectionPanels = new HashSet<SettingsSectionPanel>();

		private MainPanel() {
			super(new CardLayout());

			ConfigSpec configSpec = SettingsWindow.this.app.configSpec;
			this.initSectionPanels();
			this.setSection(configSpec.getSections().get(0).title);
		}

		private void initSectionPanels() {
			ConfigSpec configSpec = SettingsWindow.this.app.configSpec;
			for (ConfigSpec.Section section : configSpec.getSections()) {
				SettingsSectionPanel sectionPanel = new SettingsSectionPanel(section, SettingsWindow.this.app.getConfig());
				this.sectionPanels.add(sectionPanel);
				this.add(sectionPanel, section.title);
			}
		}

		void setSection(String sectionName) {
			((CardLayout)this.getLayout()).show(this, sectionName);
		}

		Set<SettingControl> getAllSettingControls() {
			Set<SettingControl> allSettingControls = new HashSet<SettingControl>();
			for (SettingsSectionPanel sectionPanel : this.sectionPanels) {
				allSettingControls.addAll(sectionPanel.getSettingControls());
			}
			return allSettingControls;
		}
	}

	private class SettingsSectionPanel extends JScrollPane {
		private Set<SettingControl> settingControls = new HashSet<SettingControl>();

		private SettingsSectionPanel(ConfigSpec.Section section, Configuration initialConfig) {
			super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			JPanel innerPanel = this.initInnerPanel();

			this.initTitleLabel(innerPanel, section);
			if (section.representsCheck()) {
				this.initEnableControl(innerPanel, section, initialConfig);
			}
			this.initSettingControls(innerPanel, section, initialConfig);
		}

		private JPanel initInnerPanel() {
			JPanel innerPanel = new JPanel();
			innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
			innerPanel.setBorder(new EmptyBorder(GuiUtil.PAD, GuiUtil.PAD, GuiUtil.PAD, GuiUtil.PAD));
			this.setViewportView(innerPanel);
			return innerPanel;
		}

		private JLabel initTitleLabel(JPanel innerPanel, ConfigSpec.Section section) {
			JLabel titleLabel = GuiUtil.createHeading(section.title);
			JLabel descLabel = new JLabel(section.getDesc());
			innerPanel.add(titleLabel);
			innerPanel.add(descLabel);
			innerPanel.add(Box.createVerticalStrut(GuiUtil.PAD));
			return titleLabel;
		}

		private void initEnableControl(JPanel innerPanel, ConfigSpec.Section section, Configuration initialConfig) {
			SettingControl enableControl = new SettingControl(new ConfigSpec.Setting(
				CheckUtil.ENABLE_KEY_PREFIX + section.getCheckName(),
				ConfigSpec.Setting.Type.BOOLEAN,
				MessageFormat.format("Enable this {0}?", section.getEntityType()),
				null
			), initialConfig);
			this.settingControls.add(enableControl);
			innerPanel.add(enableControl);
			innerPanel.add(Box.createVerticalStrut(GuiUtil.PAD));
		}

		private void initSettingControls(JPanel innerPanel, ConfigSpec.Section section, Configuration initialConfig) {
			for (ConfigSpec.Setting setting : section.getSettings()) {
				SettingControl settingControl = new SettingControl(setting, initialConfig);
				if (settingControl.doesSupplySettingValue()) {
					this.settingControls.add(settingControl);
				}
				innerPanel.add(settingControl);
				innerPanel.add(Box.createVerticalStrut(GuiUtil.PAD));
			}
		}

		Set<SettingControl> getSettingControls() {
			return Collections.unmodifiableSet(this.settingControls);
		}
	}

	private class SettingControl extends JPanel {
		private final ConfigSpec.Setting setting;
		private final Supplier<Object> valueSupplier;

		private SettingControl(ConfigSpec.Setting setting, Configuration initialConfig) {
			super();
			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			this.setting = setting;
			this.setAlignmentX(LEFT_ALIGNMENT);

			this.initTitleLabel();
			if (setting.desc != null) {
				this.initDescLabel();
			}

			JPanel inputPanel = this.initInputPanel();
			switch (setting.type) {
				case BOOLEAN:
					Boolean initBool = initialConfig.getBoolean(setting.name, null);
					this.valueSupplier = this.initBooleanControl(inputPanel, initBool);
					break;
				case INT:
					Integer initInt = initialConfig.getInt(setting.name, null);
					this.valueSupplier = this.initIntegerControl(inputPanel, initInt);
					break;
				case STRING:
					String initStr = initialConfig.getString(setting.name, null);
					if (setting.hasStringOptions()) {
						if (!setting.getStringOptions().contains(initStr)) {initStr = null;}
						this.valueSupplier = this.initSelectStringControl(inputPanel, initStr);
					} else {
						this.valueSupplier = this.initStringControl(inputPanel, initStr);
					}
					break;
				default:
					inputPanel.add(new JLabel("This setting cannot be edited using the GUI."));
					this.valueSupplier = null;
			}
		}

		private JLabel initTitleLabel() {
			JLabel titleLabel = new JLabel(setting.name);
			titleLabel.setAlignmentX(LEFT_ALIGNMENT);
			titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
			this.add(titleLabel);
			return titleLabel;
		}

		private JLabel initDescLabel() {
			JLabel descLabel = new JLabel(setting.desc);
			this.add(descLabel);
			return descLabel;
		}

		private JPanel initInputPanel() {
			JPanel inputPanel = new JPanel();
			inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.LINE_AXIS));
			inputPanel.setAlignmentX(LEFT_ALIGNMENT);
			inputPanel.setBorder(new EmptyBorder(CTRL_PAD, CTRL_PAD, CTRL_PAD, CTRL_PAD));
			this.add(inputPanel);
			return inputPanel;
		}

		private Supplier<Object> initBooleanControl(JPanel inputPanel, Boolean initVal) {
			Map<JRadioButton, Boolean> radioOptions = new HashMap<JRadioButton, Boolean>();
			ButtonGroup radioGroup = new ButtonGroup();
			for (int i = 0; i < BOOLEAN_OPTIONS.length; i++) {
				JRadioButton radioButton = new JRadioButton(BOOLEAN_OPTION_NAMES[i], BOOLEAN_OPTIONS[i] == initVal);
				radioOptions.put(radioButton, BOOLEAN_OPTIONS[i]);
				radioGroup.add(radioButton);
				inputPanel.add(radioButton);
				inputPanel.add(Box.createRigidArea(new Dimension(GuiUtil.PAD, GuiUtil.PAD)));
			}
			return () -> {
				for (Map.Entry<JRadioButton, Boolean> entry : radioOptions.entrySet()) {
					if (entry.getKey().isSelected()) {return entry.getValue();}
				}
				return null;
			};
		}

		private Supplier<Object> initIntegerControl(JPanel inputPanel, Integer initVal) {
			JCheckBox enabledCheckBox = new JCheckBox("", initVal != null);
			JSpinner valueSpinner = new JSpinner(
				new SpinnerNumberModel(initVal != null ? initVal : 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1)
			);
			valueSpinner.setMaximumSize(new Dimension(valueSpinner.getPreferredSize().width, valueSpinner.getPreferredSize().height));
			valueSpinner.addChangeListener((e) -> {enabledCheckBox.setSelected(true);});
			inputPanel.add(enabledCheckBox);
			inputPanel.add(Box.createRigidArea(new Dimension(GuiUtil.PAD, GuiUtil.PAD)));
			inputPanel.add(valueSpinner);
			return () -> {
				if (!enabledCheckBox.isSelected()) {return null;}
				return valueSpinner.getValue();
			};
		}

		private Supplier<Object> initStringControl(JPanel inputPanel, String initVal) {
			JCheckBox strEnabledCheckBox = new JCheckBox("", initVal != null);
			JTextField valueField = new JTextField((initVal != null) ? initVal : "");
			valueField.setMaximumSize(new Dimension(Short.MAX_VALUE, valueField.getPreferredSize().height));
			GuiUtil.addTextFieldDocumentUpdateListener(valueField, (e) -> {strEnabledCheckBox.setSelected(true);});
			inputPanel.add(strEnabledCheckBox);
			inputPanel.add(Box.createRigidArea(new Dimension(GuiUtil.PAD, GuiUtil.PAD)));
			inputPanel.add(valueField);
			return () -> {
				if (!strEnabledCheckBox.isSelected()) {return null;}
				return valueField.getText();
			};
		}

		private Supplier<Object> initSelectStringControl(JPanel inputPanel, String initVal) {
			List<String> options = new ArrayList<String>(List.of(NO_SELECTION_STRING));
			options.addAll(setting.getStringOptions());
			JComboBox<String> optionComboBox = new JComboBox<String>(options.toArray(new String[0]));
			optionComboBox.setSelectedItem((initVal != null) ? initVal : NO_SELECTION_STRING);
			inputPanel.add(optionComboBox);
			return () -> {
				if (optionComboBox.getSelectedIndex() == 0) {return null;}
				return optionComboBox.getSelectedItem();
			};
		}

		String getSettingName() {return this.setting.name;}
		boolean doesSupplySettingValue() {return this.valueSupplier != null;}
		Object getSettingValue() {return this.valueSupplier.get();}
	}

	private class Footer extends JPanel {
		private final JButton saveButton;

		private Footer() {
			super(new BorderLayout(GuiUtil.PAD, GuiUtil.PAD));

			this.saveButton = this.initSaveButton();
		}

		private JButton initSaveButton() {
			JButton saveButton = GuiUtil.createButton("Save", (e) -> {SettingsWindow.this.saveAndClose();});
			this.add(saveButton, BorderLayout.LINE_END);
			return saveButton;
		}
	}
}
