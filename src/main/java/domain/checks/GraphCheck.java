package domain.checks;

import java.util.Set;

import datasource.Configuration;
import domain.Check;
import domain.ClassGraph;
import domain.Message;
import domain.javadata.ClassDataCollection;

public abstract class GraphCheck extends Check {
    protected ClassGraph graph;

    /**
	 * @param name The check's name, in camelCase, for identification in the configuration file
	 */
	GraphCheck(String name) {
		this(name, true);
	}

    /**
	 * @param name The check's name, in camelCase, for identification in the configuration file
	 * @param isEnabledByDefault Whether the check is enabled by default (true for general checks, false for specialized tools and such)
	 */
    GraphCheck(String name, boolean isEnabledByDefault) {
        super(name, isEnabledByDefault);
    }

    @Override
    public final Set<Message> run(ClassDataCollection classes, Configuration config) {
        graph = new ClassGraph(classes);
        return gRun(config);
    }

    public abstract Set<Message> gRun(Configuration config);
}
