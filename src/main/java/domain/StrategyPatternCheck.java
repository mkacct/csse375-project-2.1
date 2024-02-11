package domain;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.ClassType;
import domain.javadata.FieldData;

import java.util.Map;
import java.util.Set;

public class StrategyPatternCheck implements Check {
    @Override
    public String getName() {
        return "strategyPattern";
    }

    @Override
    public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
        for(Map.Entry<String,ClassData> entry : classes.entrySet()) {
            if(entry.getValue().isAbstract()) {
                Set<FieldData> fields = entry.getValue().getFields();
                for(FieldData field : fields) {
                    //TODO: fix this logic?
                    ClassData strategy = getClassFromName(field.getName(), classes);
                    if(strategy != null) {
                        if(strategy.isAbstract() || strategy.getClassType() == ClassType.INTERFACE) {
                            //more conditional logic here
                            return Set.of(new Message(MessageLevel.INFO,"Strategy Pattern Detected!"));
                        }
                    }
                }
            }

        }
        return Set.of(new Message(MessageLevel.INFO,"No strategy Pattern Detected!"));
    }

    public ClassData getClassFromName(String className, Map<String, ClassData> classes) {
        for(Map.Entry<String,ClassData> entry : classes.entrySet()) {
            if(entry.getKey().equals(className)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
