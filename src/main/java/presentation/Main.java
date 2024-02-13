package presentation;

import datasource.DirLoader;
import datasource.JsonFileConfigLoader;
import domain.AdapterPatternCheck;
import domain.Check;
import domain.MethodLengthCheck;
import domain.ProgramToInterfaceNotImplementationCheck;

public class Main {
	private static final Check[] CHECKS = {
		new MethodLengthCheck(),
		new ProgramToInterfaceNotImplementationCheck(),
		new AdapterPatternCheck()
	}; // TODO: add all checks

	public static void main(String[] args) {
		if (args.length == 0) {
			printUsage();
			System.exit(1);
		} else {
			String targetDirPath = args[0];
			String configPath = (args.length > 1) ? args[1] : null;
			App app = new App(
				new DirLoader(targetDirPath),
				(configPath != null) ? new JsonFileConfigLoader(configPath) : null
			);
			boolean noErrors = app.run(CHECKS);
			System.exit(noErrors ? 0 : 1);
		}
	}

	private static void printUsage() {
		System.out.println("usage: <command to run LinterProject> <classdir> [<config>]");
	}
}
