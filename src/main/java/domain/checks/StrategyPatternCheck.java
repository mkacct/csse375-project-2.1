package domain.checks;

import java.util.ArrayList;
import java.util.Set;

import datasource.Configuration;
import domain.Check;
import domain.Message;
import domain.MessageLevel;
import domain.javadata.ClassData;
import domain.javadata.ClassDataCollection;
import domain.javadata.ClassType;
import domain.javadata.FieldData;

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
    public Set<Message> run(ClassDataCollection classes, Configuration config) {
        //iterates through every class
        for(ClassData classData : classes) {
            //checks if the class is an abstract class.
            if(classData.isAbstract() && classData.getClassType() == ClassType.CLASS) {
                Set<FieldData> fields = classData.getFields();
                //iterates through every field.
                for(FieldData field : fields) {

                    //checks if the type of the field is an interface. (Strategy)
                    ClassData strategy = getClassFromName(field.getName(), classes);
                    if(strategy != null) {
                        if(strategy.isAbstract() || strategy.getClassType() == ClassType.INTERFACE) {
                            //gets a list of all classes that implement the strategy.
                            ArrayList<String> implementors = new ArrayList<String>();
                            ClassDataCollection newClasses = new ClassDataCollection(classes);
                            newClasses.remove(strategy);
                            for (ClassData newClassData : newClasses) {
                                //check if class implements the interface
                                if (newClassData.getInterfaceFullNames().contains(strategy.getFullName())) {
                                    implementors.add(newClassData.getFullName());
                                }

                            }
                            //gets a list of all classes that extend the abstract class.
                            ArrayList<String> extenders = new ArrayList<String>();
                            newClasses = new ClassDataCollection(classes);
                            newClasses.remove(classData);
                            for (ClassData newClassData : newClasses) {
                                //check if class extends the abstract class.
                                if (newClassData.getSuperFullName().equals(classData.getFullName())) {
                                    extenders.add(newClassData.getFullName());
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

    private static ClassData getClassFromName(String className, ClassDataCollection classes) {
        for(ClassData classData : classes) {
            if(classData.getFullName().toLowerCase().equals(className)) {
                return classData;
            }
        }
        return null;
    }
}
