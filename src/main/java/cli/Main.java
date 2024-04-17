package cli;

import java.io.IOException;

import datasource.DirLoader;
import datasource.JsonFileConfigLoader;
import domain.CheckRoster;

public class Main {
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			printUsage();
			System.exit(1);
		} else {
			String targetDirPath = args[0];
			String configPath = (args.length > 1) ? args[1] : null;
			App app = new App(
				new DirLoader(targetDirPath),
				(configPath != null) ? new JsonFileConfigLoader(configPath) : null,
				System.out, System.err
			);
			boolean noErrors = app.run(CheckRoster.CHECKS);
			System.exit(noErrors ? 0 : 1);
		}
	}

	private static void printUsage() {
		System.out.println("usage: <command to run LinterProject> <classdir> [<config>]");
	}
}
