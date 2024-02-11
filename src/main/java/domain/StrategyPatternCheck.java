package domain;

import datasource.Configuration;
import domain.javadata.ClassData;

import java.util.Map;
import java.util.Set;

public class StrategyPatternCheck implements Check {
    @Override
    public String getName() {
        return "strategyPattern";
    }

    @Override
    public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
        return null;
    }
}
