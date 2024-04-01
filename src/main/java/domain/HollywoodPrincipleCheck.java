package domain;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.ClassType;
import domain.javadata.MethodData;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HollywoodPrincipleCheck extends Check {
  private static final String NAME = "hollywoodPrinciple";

  public HollywoodPrincipleCheck() { super(NAME); }

  @Override
  public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
    boolean checkHollywood = config.getBoolean("hollywood", true);
    if (!config.getBoolean("hollywood", true)) {
      return Set.of();
    }
    return checkAbstractClasses(classes);
  }

  private Set<Message> checkAbstractClasses(Map<String, ClassData> classes) {
    Set<Message> messages = new HashSet<>();
    for (Map.Entry<String,ClassData> entry : classes.entrySet()) {
      if (isNotAbstractClass(entry)) continue;
      messages.add(checkForAbstractMethods(entry.getValue().getMethods()));
    }
    return messages;
  }

  private static boolean isNotAbstractClass(Map.Entry<String, ClassData> entry) {
    return !(entry.getValue().isAbstract() && entry.getValue().getClassType() == ClassType.CLASS);
  }

  private Message checkForAbstractMethods(Set<MethodData> methods) {
    for (MethodData m : methods) {
      if (!m.isAbstract()) continue;
    }
  }

}
