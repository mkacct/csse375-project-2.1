package domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.ClassDataCollection;
import domain.javadata.ClassType;

public class UnusedAbstractionsCheck extends Check {
    private static final String NAME = "unusedAbstractions";

    public UnusedAbstractionsCheck() {
        super(NAME);
    }

    /**
     * This
     * @param classes map of class full names to class data
     * @param config from the configuration file
     * @return set of messages containing abstractions that are not used by other classes.
     */
    @Override
    public Set<Message> run(ClassDataCollection classes, Configuration config) {
        //creates list to keep track of unused classes.
        ArrayList<String> unsusedClasses = new ArrayList<String>();
        //iterates through every class
        for (ClassData classData : classes) {
            //checks if class is an interface. If so, we check if it has any implementors.
            if (classData.getClassType() == ClassType.INTERFACE) {
                //creates list to keep track of implementors.
                ArrayList<String> implementors = new ArrayList<String>();
                ClassDataCollection newClasses = new ClassDataCollection(classes);
                newClasses.remove(classData);
                //iterates through every class except itself.
                for (ClassData newClassData : newClasses) {
                    //check if class implements the interface. If so, add it to the implementor list
                    if (newClassData.getInterfaceFullNames().contains(classData.getFullName())) {
                        implementors.add(newClassData.getFullName());
                    }

                }
                //If no implementors are found, add the interface to the unused classes list
                if (implementors.isEmpty()) {
                    unsusedClasses.add(classData.getFullName());
                    continue;
                }

            }
            //checks if class is an abstract class. If so, we check if it has any extendors.
            else if (classData.isAbstract()) {
                //creates a list to keep track of extendors.
                ArrayList<String> extenders = new ArrayList<String>();
                ClassDataCollection newClasses = new ClassDataCollection(classes);
                newClasses.remove(classData);
                //iterates through every class except itself
                for (ClassData newClassData : newClasses) {
                    //check if class extends abstract class. If so, add it to the extender list
                    if (newClassData.getSuperFullName().equals(classData.getFullName())) {
                        extenders.add(newClassData.getFullName());
                    }
                }
                //if so classes extend the abstract class, add the class t the unused classes list.
                if (extenders.isEmpty()) {
                    unsusedClasses.add(classData.getFullName());
                }
            }
        }
        //creates set of messages.
        Set<Message> messages = new HashSet<Message>();
        //for each unused class, a messages is generated saying the class is not used.
        for (String badClass : unsusedClasses) {
            Message result = new Message(MessageLevel.WARNING, "The Following class is not used, please" +
                    " consider using them or deleting them: ", badClass);
            messages.add(result);
        }
        //return all messages generated
        return messages;
    }
}
