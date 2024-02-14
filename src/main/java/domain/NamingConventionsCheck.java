package domain;

import java.util.Map;
import java.util.Set;

import datasource.Configuration;
import domain.javadata.ClassData;

public class NamingConventionsCheck implements Check {

    @Override
    public String getName() {
        return "Naming Conventions Check";
    }

    @Override
    public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
        NamingConventions packageNames = NamingConventions.getConvention(config.getString("package", "lowercase"));
        NamingConventions classNames = NamingConventions.getConvention(config.getString("class", "PascalCase"));
        NamingConventions interfaceNames = NamingConventions.getConvention(config.getString("interface", "PascalCase"));
        NamingConventions abstractNames = NamingConventions.getConvention(config.getString("abstract", "PascalCase"));
        NamingConventions enumNames = NamingConventions.getConvention(config.getString("enum", "PascalCase"));
        NamingConventions fieldNames = NamingConventions.getConvention(config.getString("field", "camelCase"));
        NamingConventions methodNames = NamingConventions.getConvention(config.getString("method", "camelCase"));
        NamingConventions constantNames = NamingConventions.getConvention(config.getString("constant", "UPPER_CASE"));
        NamingConventions enumConstantNames = NamingConventions.getConvention(config.getString("enumConstant", "UPPER_CASE"));
        int maxLength = config.getInt("maxLength", -1);
        ClassData classInfo;
        for (String s : classes.keySet()) {
            classInfo = classes.get(s);
        }
        return null;
    }
    
}
