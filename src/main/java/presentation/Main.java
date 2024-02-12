package presentation;

import java.io.IOException;

import datasource.DirLoader;
import datasource.FilesLoader;
import datasource.JsonFileConfigLoader;
import domain.Check;
import domain.ClassGraph;
import domain.GraphCheck;

public class Main {
	private static final Check[] CHECKS = {}; // TODO: add all checks

	public static void main(String[] args) throws IOException {
        FilesLoader fl = new DirLoader("target/classes/presentation");
        ClassGraph graph = new ClassGraph(GraphCheck.getMap(fl.loadFiles("class")));
        System.out.println(graph.getNumClasses());
        System.out.println(graph.getClasses().keySet().iterator().next());
		// if (args.length == 0) {
		// 	printUsage();
		// 	System.exit(1);
		// } else {
		// 	String targetDirPath = args[0];
		// 	String configPath = (args.length > 1) ? args[1] : null;
		// 	App app = new App(
		// 		new DirLoader(targetDirPath),
		// 		(configPath != null) ? new JsonFileConfigLoader(configPath) : null
		// 	);
		// 	boolean noErrors = app.run(CHECKS);
		// 	System.exit(noErrors ? 0 : 1);
		// }
	}

	private static void printUsage() {
		System.out.println("usage: <command to run LinterProject> <classdir> [<config>]");
	}
}
