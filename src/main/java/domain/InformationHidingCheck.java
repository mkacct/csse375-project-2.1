package domain;

import datasource.Configuration;
import domain.javadata.AccessModifier;
import domain.javadata.ClassData;
import domain.javadata.FieldData;
import domain.javadata.MethodData;

import java.util.*;

public class InformationHidingCheck implements Check {
    @Override
    public String getName() {
        return "informationHiding";
    }


    /**
     * This
     * @param classes map of class full names to class data
     * @param config from the configuration file
     * @return set of messages containing which fields violate information hiding.
     */
    @Override
    public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
        //creates map of classes to a list of its fields that violate information hiding.
        Map<String, ArrayList<String>> publicFieldsToClass = new HashMap<String, ArrayList<String>>();
        //iterate through every class
        for (Map.Entry<String, ClassData> entry : classes.entrySet()) {
            ClassData currentClass = entry.getValue();
            //get the set of fields from the class
            Set<FieldData> fields = entry.getValue().getFields();
            //iterates through the fields of the class
            for (FieldData field : fields) {
                //checks if a field is public
                if (field.getAccessModifier() == AccessModifier.PUBLIC) {
                    //checks if the class the field is in is already in the map.
                    //adds the field to the map.
                    if (publicFieldsToClass.containsKey(currentClass.getFullName())) {
                        ArrayList<String> newList = publicFieldsToClass.get(currentClass.getFullName());
                        newList.add(field.getName());
                        publicFieldsToClass.replace(currentClass.getFullName(), newList);
                    } else {
                        ArrayList<String> newList = new ArrayList<String>();
                        newList.add(field.getName());
                        publicFieldsToClass.put(currentClass.getFullName(), newList);
                    }


                }
                //checks if the field contains any gettter or setter functions in the class
                else {

                    Set<MethodData> methods = entry.getValue().getMethods();

                    for (MethodData method : methods) {
                        String methodName = method.getName().toLowerCase();
                        String string1 = methodName.substring(0, 3);
                        String string2 = methodName.substring(3, methodName.length());
                        //checks if the method name matches the field.
                        if ((string1.equals("get") || string1.equals("set")) & string2.equals(field.getName().toLowerCase())) {
                            //checks if the class the field is in is already in the map.
                            //adds the field to the map.
                            if (publicFieldsToClass.containsKey(currentClass.getFullName())) {
                                ArrayList<String> newList = publicFieldsToClass.get(currentClass.getFullName());
                                newList.add(field.getName());
                                publicFieldsToClass.replace(currentClass.getFullName(), newList);
                            } else {
                                ArrayList<String> newList = new ArrayList<String>();
                                newList.add(field.getName());
                                publicFieldsToClass.put(currentClass.getFullName(), newList);
                            }
                        }


                    }
                }

            }
        }
        //creates the set of messages.
        Set<Message> messages = new HashSet<Message>();
        //for each class, a message is generated with the fields that violate information hiding.
        for (Map.Entry<String, ArrayList<String>> entry : publicFieldsToClass.entrySet()) {
            Set<String> fields1 = new HashSet<String>(entry.getValue());
            Message result = new Message(MessageLevel.WARNING, "The class " + entry.getKey()
                    + " contains the following fields that violate information hiding: " + fields1);
            messages.add(result);
        }
        //returns all messages generated.
        return messages;
    }
}
