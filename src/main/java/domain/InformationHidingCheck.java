package domain;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.FieldData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InformationHidingCheck implements Check {
    @Override
    public String getName() {
        return "informationHiding";
    }

    @Override
    public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
        Map<String,String> publicFieldsToClass = new HashMap<String,String>();

        for(Map.Entry<String,ClassData> entry : classes.entrySet()) {
            Set<FieldData> fields = entry.getValue().getFields();
        }

        return null;
    }
}
