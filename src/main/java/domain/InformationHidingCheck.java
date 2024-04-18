package domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import datasource.Configuration;
import domain.javadata.AccessModifier;
import domain.javadata.ClassData;
import domain.javadata.ClassDataCollection;
import domain.javadata.FieldData;
import domain.javadata.MethodData;

public class InformationHidingCheck extends Check {
    private static final String NAME = "informationHiding";

    public InformationHidingCheck() {
        super(NAME);
    }

    /**
     * This
     * @param classes map of class full names to class data
     * @param config from the configuration file
     * @return set of messages containing which fields violate information hiding.
     */
    @Override
    public Set<Message> run(ClassDataCollection classes, Configuration config) {
        Map<String, ArrayList<String>> publicFieldsToClass = new HashMap<String, ArrayList<String>>();
        informationHidingClassCheck(classes, publicFieldsToClass);
        return indicateFieldsWithInformationHiding(publicFieldsToClass);
    }

    private static Set<Message> indicateFieldsWithInformationHiding(Map<String, ArrayList<String>> publicFieldsToClass) {
        Set<Message> messages = new HashSet<Message>();
        for (Map.Entry<String, ArrayList<String>> entry : publicFieldsToClass.entrySet()) {
            Set<String> fields1 = new HashSet<String>(entry.getValue());
            Message result = new Message(MessageLevel.WARNING, "The class " + entry.getKey()
                    + " contains the following fields that violate information hiding: " + fields1);
            messages.add(result);
        }
        return messages;
    }

    private void informationHidingClassCheck(ClassDataCollection classes, Map<String, ArrayList<String>> publicFieldsToClass) {
        for (ClassData currentClass : classes) {
            Set<FieldData> fields = currentClass.getFields();
            checkFieldModifiers(fields, publicFieldsToClass, currentClass);
        }
    }

    private void checkFieldModifiers(Set<FieldData> fields, Map<String, ArrayList<String>> publicFieldsToClass, ClassData currentClass) {
        for (FieldData field : fields) {
            boolean publicField = field.getAccessModifier() == AccessModifier.PUBLIC;
            if (publicField) {
                handlePublicFields(publicFieldsToClass, currentClass, field);
            } else {
                checkForGettersAndSetters(field, publicFieldsToClass, currentClass);
            }
        }
    }

    private static final int GETTER_SETTER_LENGTH = 3;

    private void checkForGettersAndSetters(FieldData field, Map<String, ArrayList<String>> publicFieldsToClass, ClassData currentClass) {
        Set<MethodData> methods = currentClass.getMethods();
        for (MethodData method : methods) {
            String methodName = method.getName().toLowerCase();
            boolean isNotGetterSetter = methodName.length() > GETTER_SETTER_LENGTH;
            if (isNotGetterSetter) {
                String string1 = methodName.substring(0, GETTER_SETTER_LENGTH);
                String string2 = methodName.substring(GETTER_SETTER_LENGTH, methodName.length());
                matchingMethodAndFieldName(field, string1, string2, publicFieldsToClass, currentClass);
            }
        }
    }

    private void matchingMethodAndFieldName(FieldData field, String string1, String string2, Map<String, ArrayList<String>> publicFieldsToClass, ClassData currentClass) {
        if ((string1.equals("get") || string1.equals("set")) & string2.equals(field.getName().toLowerCase())) {
            handlePublicFields(publicFieldsToClass, currentClass, field);
        }
    }

    private void handlePublicFields(Map<String, ArrayList<String>> publicFieldsToClass, ClassData currentClass, FieldData field) {
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
