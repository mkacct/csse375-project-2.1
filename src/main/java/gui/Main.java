package gui;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;

import datasource.JsonFileConfigLoader;
import domain.CheckRoster;

public final class Main {
	public static void main(String[] args) {
		FlatDarkLaf.setup();
		UIManager.put("OptionPane.isYesLast", true);
		App app = new App(CheckRoster.CHECKS, new JsonFileConfigLoader(App.CONFIG_PATH));
		new MainWindow(app);
	}
}
