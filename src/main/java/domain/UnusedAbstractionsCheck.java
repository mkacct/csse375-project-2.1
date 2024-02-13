package domain;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.ClassType;

import java.util.*;

public class UnusedAbstractionsCheck implements Check {
    @Override
    public String getName() {
        return "unusedAbstractions";
    }

    @Override
    public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
        //ArrayList<ClassData> classlist = (ArrayList<ClassData>) classes.values();
        ArrayList<String> unsusedClasses = new ArrayList<String>();
        for (Map.Entry<String, ClassData> entry : classes.entrySet()) {
            if (entry.getValue().getClassType() == ClassType.INTERFACE) {
                //does a thing
                ArrayList<String> implementors = new ArrayList<String>();
                Map<String, ClassData> newMap = new HashMap<String, ClassData>(classes);
                newMap.remove(entry.getKey());
                for (Map.Entry<String, ClassData> newEntry : newMap.entrySet()) {
                    //check if class implements the interface
                    if (newEntry.getValue().getInterfaceFullNames().contains(entry.getKey())) {
                        implementors.add(newEntry.getKey());
                    }

                }
                if (implementors.isEmpty()) {
                    unsusedClasses.add(entry.getKey());
                    continue;
                }

            }

            else if (entry.getValue().isAbstract()) {
                ArrayList<String> extenders = new ArrayList<String>();
                Map<String, ClassData> newMap = new HashMap<String, ClassData>(classes);
                newMap.remove(entry.getKey());
                for (Map.Entry<String, ClassData> newEntry : newMap.entrySet()) {
                    if (newEntry.getValue().getSuperFullName().equals(entry.getKey())) {
                        extenders.add(newEntry.getKey());
                    }
                }
                if (extenders.isEmpty()) {
                    unsusedClasses.add(entry.getKey());
                }
            }
        }
        //do something with the list of classes
        Set<Message> messages = new HashSet<Message>();
        for (String badClass : unsusedClasses) {
            Message result = new Message(MessageLevel.WARNING, "The Following class is not used, please" +
                    " consider using them or deleting them: ", badClass);
            messages.add(result);
        }

        return messages;
    }
}
