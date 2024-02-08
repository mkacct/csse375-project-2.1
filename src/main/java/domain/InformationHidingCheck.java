package domain;

import datasource.Configuration;
import domain.javadata.ClassData;

import java.util.Map;
import java.util.Set;

public class InformationHidingCheck implements Check {
    @Override
    public String getName() {
        return "informationHiding";
    }

    @Override
    public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
        return null;
    }
}
