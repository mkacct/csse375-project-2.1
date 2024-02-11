package domain;

import datasource.Configuration;
import domain.javadata.AccessModifier;
import domain.javadata.ClassData;
import domain.javadata.FieldData;
import domain.javadata.MethodData;

import java.lang.reflect.Method;
import java.util.*;

public class InformationHidingCheck implements Check {
    @Override
    public String getName() {
        return "informationHiding";
    }

    @Override
    public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
        //map for field to class
        Map<String, ArrayList<String>> publicFieldsToClass = new HashMap<String, ArrayList<String>>();

        for (Map.Entry<String, ClassData> entry : classes.entrySet()) {
            ClassData currentClass = entry.getValue();
            Set<FieldData> fields = entry.getValue().getFields();
            //check if fields are public
            for (FieldData field : fields) {
                //checks if a field is public
                if (field.getAccessModifier() == AccessModifier.PUBLIC) {
                    if (publicFieldsToClass.containsKey(currentClass)) {
                        ArrayList<String> newList = publicFieldsToClass.get(currentClass);
                        newList.add(field.getName());
                        publicFieldsToClass.replace(currentClass.getFullName(), newList);
                    } else {
                        ArrayList<String> newList = new ArrayList<String>();
                        newList.add(field.getName());
                        publicFieldsToClass.replace(currentClass.getFullName(), newList);
                    }

                    continue;
                }
                //checks if a getter/setter method exists for a field.
                //TODO: consider using InstructionData for this action, ask about later.
                Set<MethodData> methods = entry.getValue().getMethods();
                for (MethodData method : methods) {
                    String methodName = method.getName().toLowerCase();
                    String string1 = methodName.substring(0, 3);
                    String string2 = methodName.substring(3, methodName.length());
                    if ((string1 == "get" || string1 == "set") & string2 == field.getName().toLowerCase()) {
                        if (publicFieldsToClass.containsKey(currentClass)) {
                            ArrayList<String> newList = publicFieldsToClass.get(currentClass);
                            newList.add(field.getName());
                            publicFieldsToClass.replace(currentClass.getFullName(), newList);
                        } else {
                            ArrayList<String> newList = new ArrayList<String>();
                            newList.add(field.getName());
                            publicFieldsToClass.replace(currentClass.getFullName(), newList);
                        }
                    }


                }
            }
        }

        Set<Message> messages = new HashSet<Message>();
        for (Map.Entry<String, ArrayList<String>> entry : publicFieldsToClass.entrySet()) {
            Set<String> fields1 = new HashSet<String>(entry.getValue());
            Message result = new Message(MessageLevel.WARNING, "The class " + entry.getKey() +
                    "Fields that violate information hiding: ", fields1);
            messages.add(result);
        }

        return messages;
    }
}
