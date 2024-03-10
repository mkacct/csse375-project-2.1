package domain;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.ClassType;

import java.util.*;

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
    public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
        //creates list to keep track of unused classes.
        ArrayList<String> unsusedClasses = new ArrayList<String>();
        //iterates through every class
        for (Map.Entry<String, ClassData> entry : classes.entrySet()) {
            //checks if class is an interface. If so, we check if it has any implementors.
            if (entry.getValue().getClassType() == ClassType.INTERFACE) {
                //creates list to keep track of implementors.
                ArrayList<String> implementors = new ArrayList<String>();
                Map<String, ClassData> newMap = new HashMap<String, ClassData>(classes);
                newMap.remove(entry.getKey());
                //iterates through every class except itself.
                for (Map.Entry<String, ClassData> newEntry : newMap.entrySet()) {
                    //check if class implements the interface. If so, add it to the implementor list
                    if (newEntry.getValue().getInterfaceFullNames().contains(entry.getKey())) {
                        implementors.add(newEntry.getKey());
                    }

                }
                //If no implementors are found, add the interface to the unused classes list
                if (implementors.isEmpty()) {
                    unsusedClasses.add(entry.getKey());
                    continue;
                }

            }
            //checks if class is an abstract class. If so, we check if it has any extendors.
            else if (entry.getValue().isAbstract()) {
                //creates a list to keep track of extendors.
                ArrayList<String> extenders = new ArrayList<String>();
                Map<String, ClassData> newMap = new HashMap<String, ClassData>(classes);
                newMap.remove(entry.getKey());
                //iterates through every class except itself
                for (Map.Entry<String, ClassData> newEntry : newMap.entrySet()) {
                    //check if class extends abstract class. If so, add it to the extender list
                    if (newEntry.getValue().getSuperFullName().equals(entry.getKey())) {
                        extenders.add(newEntry.getKey());
                    }
                }
                //if so classes extend the abstract class, add the class t the unused classes list.
                if (extenders.isEmpty()) {
                    unsusedClasses.add(entry.getKey());
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
