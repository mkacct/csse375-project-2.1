package gui;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarkLaf;

import datasource.JsonFileConfigRW;
import datasource.configspec.JsonFileConfigSpecLoader;
import domain.CheckRoster;

public final class Main {
	public static void main(String[] args) {
		FlatDarkLaf.setup();
		UIManager.put("OptionPane.isYesLast", true);
		App app = new App(
			CheckRoster.CHECKS,
			new JsonFileConfigSpecLoader(JsonFileConfigSpecLoader.CONFIG_SPEC_RES_PATH),
			new JsonFileConfigRW(App.CONFIG_PATH)
		);
		new MainWindow(app);
	}
}
