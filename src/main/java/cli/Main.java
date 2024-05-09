package cli;

import java.io.IOException;
import java.text.MessageFormat;

import datasource.DirLoader;
import datasource.JsonFileConfigRW;
import domain.CheckRoster;
import general.ProductInfo;

public final class Main {
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			printInfo();
			System.exit(1);
		} else {
			String targetDirPath = args[0];
			String configPath = (args.length > 1) ? args[1] : null;
			App app = new App(
				new DirLoader(targetDirPath),
				(configPath != null) ? new JsonFileConfigRW(configPath) : null,
				System.out, System.err
			);
			boolean noErrors = app.run(CheckRoster.CHECKS);
			System.exit(noErrors ? 0 : 1);
		}
	}

	private static void printInfo() {
		System.out.println(MessageFormat.format("{0} version {1}", ProductInfo.getName(), ProductInfo.getVersion()));
		System.out.println("usage: <command to run LinterProject> <classdir> [<config>]");
	}
}
