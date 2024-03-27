package domain;

import datasource.Configuration;
import domain.javadata.AccessModifier;
import domain.javadata.ClassData;
import domain.javadata.FieldData;
import domain.javadata.MethodData;

import java.util.*;

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
    public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
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

    private void informationHidingClassCheck(Map<String, ClassData> classes, Map<String, ArrayList<String>> publicFieldsToClass) {
        for (Map.Entry<String, ClassData> entry : classes.entrySet()) {
            ClassData currentClass = entry.getValue();
            Set<FieldData> fields = entry.getValue().getFields();
            checkFieldModifiers(entry, fields, publicFieldsToClass, currentClass);
        }
    }

    private void checkFieldModifiers(Map.Entry<String, ClassData> entry, Set<FieldData> fields, Map<String, ArrayList<String>> publicFieldsToClass, ClassData currentClass) {
        for (FieldData field : fields) {
            boolean publicField = field.getAccessModifier() == AccessModifier.PUBLIC;
            if (publicField) {
                handlePublicFields(publicFieldsToClass, currentClass, field);
            } else {
                checkForGettersAndSetters(entry, field, publicFieldsToClass, currentClass);
            }
        }
    }

    private void checkForGettersAndSetters(Map.Entry<String, ClassData> entry, FieldData field, Map<String, ArrayList<String>> publicFieldsToClass, ClassData currentClass) {
        Set<MethodData> methods = entry.getValue().getMethods();
        for (MethodData method : methods) {
            String methodName = method.getName().toLowerCase();
            int GETTER_SETTER_LENGTH = 3;
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
