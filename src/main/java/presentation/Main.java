package presentation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import datasource.DirLoader;
import datasource.FilesLoader;
import datasource.JsonFileConfigLoader;
import domain.AdapterPatternCheck;
import domain.Check;
import domain.ClassGraph;
import domain.InformationHidingCheck;
import domain.LowCouplingCheck;
import domain.MethodLengthCheck;
import domain.NamingConventionsCheck;
import domain.ObserverPatternCheck;
import domain.ProgramToInterfaceNotImplementationCheck;
import domain.StrategyPatternCheck;
import domain.UnusedAbstractionsCheck;
import domain.javadata.ClassData;
import domain.javadata.ClassReaderUtil;
import domain.javadata.FieldData;
import domain.javadata.MethodData;
import domain.javadata.VariableData;

public class Main {
	private static final Check[] CHECKS = {
		new NamingConventionsCheck(),
		new MethodLengthCheck(),
		new UnusedAbstractionsCheck(),
		new InformationHidingCheck(),
		new ProgramToInterfaceNotImplementationCheck(),
		new LowCouplingCheck(),
		new StrategyPatternCheck(),
		new ObserverPatternCheck(),
		new AdapterPatternCheck()
	};

	public static void main(String[] args) throws IOException {
		FilesLoader fl = new DirLoader("src/test/resources/TypeParamaterTest");
        Set<byte[]> files = fl.loadFiles("class");
		Map<String, ClassData> map = new HashMap<String, ClassData>();
        Iterator<byte[]> it = files.iterator();
        ClassData temp;
        while (it.hasNext()) {
            temp = ClassReaderUtil.read(it.next());
            map.put(temp.getFullName(), temp);
        }

		Iterator<String> it2 = map.keySet().iterator();
		while (it2.hasNext()) {
			ClassData eee = map.get(it2.next());
			for (FieldData f : eee.getFields()) {
				System.out.println(f.getTypeFullName());
				System.out.println(f.getAllTypeFullName());
				System.out.println(f.getName());
				System.out.println("------------------");
			}
			for (MethodData m : eee.getMethods()) {
				System.out.println("-----Method-----");
				System.out.println(m.getName());
				System.out.println(m.getAllReturnTypeFullName());
				System.out.println("-Params-");
				for (VariableData v : m.getParams()) {
					System.out.println(v.name);
					System.out.println(v.getAllTypeFullName());
				}
				System.out.println("-Lvars-");
				for (VariableData v : m.getLocalVariables()) {
					System.out.println(v.name);
					System.out.println(v.getAllTypeFullName());
				}
			}
		}
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
