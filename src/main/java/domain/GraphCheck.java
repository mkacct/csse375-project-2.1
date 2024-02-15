package domain;

import java.util.Map;
import java.util.Set;


import datasource.Configuration;
import domain.javadata.ClassData;

public abstract class GraphCheck implements Check {
    protected ClassGraph graph;
    @Override
    public final Set<Message> run(Map<String, ClassData> classes, Configuration config) {
        graph = new ClassGraph(classes);
        return gRun(config);
    }

    public abstract Set<Message> gRun(Configuration config);
}
