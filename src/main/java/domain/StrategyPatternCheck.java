package domain;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.ClassType;
import domain.javadata.FieldData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StrategyPatternCheck extends Check {
    private static final String NAME = "strategyPattern";

    public StrategyPatternCheck() {
        super(NAME, false);
    }

    /**
     * This
     * @param classes map of class full names to class data
     * @param config from the configuration file
     * @return set of a message indicating if the design utilizes Strategy Pattern.
     */
    @Override
    public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
        //iterates through every class
        for(Map.Entry<String,ClassData> entry : classes.entrySet()) {
            //checks if the class is an abstract class.
            if(entry.getValue().isAbstract() && entry.getValue().getClassType() == ClassType.CLASS) {
                Set<FieldData> fields = entry.getValue().getFields();
                //iterates through every field.
                for(FieldData field : fields) {

                    //checks if the type of the field is an interface. (Strategy)
                    ClassData strategy = getClassFromName(field.getName(), classes);
                    if(strategy != null) {
                        if(strategy.isAbstract() || strategy.getClassType() == ClassType.INTERFACE) {
                            //gets a list of all classes that implement the strategy.
                            ArrayList<String> implementors = new ArrayList<String>();
                            Map<String, ClassData> newMap = new HashMap<String, ClassData>(classes);
                            newMap.remove(strategy.getFullName());
                            for (Map.Entry<String, ClassData> newEntry : newMap.entrySet()) {
                                //check if class implements the interface
                                if (newEntry.getValue().getInterfaceFullNames().contains(strategy.getFullName())) {
                                    implementors.add(newEntry.getKey());
                                }

                            }
                            //gets a list of all classes that extend the abstract class.
                            ArrayList<String> extenders = new ArrayList<String>();
                            newMap = new HashMap<String, ClassData>(classes);
                            newMap.remove(entry.getKey());
                            for (Map.Entry<String, ClassData> newEntry : newMap.entrySet()) {
                                //check if class extends the abstract class.
                                if (newEntry.getValue().getSuperFullName().equals(entry.getKey())) {
                                    extenders.add(newEntry.getKey());
                                }
                            }
                            //if both the abstract class and interface have at least one concrete class,
                            // it qualifies as Strategy Pattern.
                            if(implementors.size() > 0 & extenders.size() > 0) {
                                return Set.of(new Message(MessageLevel.INFO,"Strategy Pattern Detected!"));
                            }




                        }
                    }
                }
            }

        }
        return Set.of();
    }

    private static ClassData getClassFromName(String className, Map<String, ClassData> classes) {
        for(Map.Entry<String,ClassData> entry : classes.entrySet()) {
            if(entry.getKey().toLowerCase().equals(className)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
