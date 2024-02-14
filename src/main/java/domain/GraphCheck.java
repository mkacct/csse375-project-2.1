package domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.ClassReaderUtil;

public abstract class GraphCheck implements Check {
    protected ClassGraph graph;
    @Override
    public final Set<Message> run(Map<String, ClassData> classes, Configuration config) {
        graph = new ClassGraph(classes);
        return gRun(config);
    }

    public abstract Set<Message> gRun(Configuration config);
}
