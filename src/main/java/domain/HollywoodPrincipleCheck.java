package domain;

import datasource.Configuration;
import domain.javadata.ClassData;
import domain.javadata.MethodData;

import java.lang.reflect.Method;
import java.util.*;

public class HollywoodPrincipleCheck extends Check {
  private static final String NAME = "hollywoodPrinciple";

  public HollywoodPrincipleCheck() {
    super(NAME);
  }

  /**
   * General process:
   * 1. Iterate through all classes that implement or extend to another class
   * 2. Flag all calls to super() that IS NOT a constructor
   * 3. Create a warning message for each class that does this
   */
  @Override
  public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
    Set<Message> messages = new HashSet<>();
    List<String> violatingClassNames = new ArrayList<>();
    getViolatingSubclassNames(classes, violatingClassNames);
    generateWarningMessages(violatingClassNames, messages);
    return messages;
  }

  private static void generateWarningMessages(List<String> violatingClassNames, Set<Message> messages) {
    for (String badClass : violatingClassNames) {
      addWarningMessage(badClass, messages);
    }
  }

  private void getViolatingSubclassNames(Map<String, ClassData> classes, List<String> violatingClassNames) {
    for (ClassData c : getImplementers(classes)) {
      getViolatingClassNames(c, violatingClassNames);
    }
  }

  private static void addWarningMessage(String badClass, Set<Message> messages) {
    Message result = new Message(MessageLevel.WARNING, "The Following class is not used, please" +
            " consider using them or deleting them: ", badClass);
    messages.add(result);
  }

  private void getViolatingClassNames(ClassData c, List<String> violatingClassNames) {
    if (containsNonConstructorCallToSuper(c)) {
      violatingClassNames.add(c.getSimpleName());
    }
  }

  private boolean containsNonConstructorCallToSuper(ClassData c) {
    for (MethodData m : c.getMethods()) {
      if (m.getName().equals("super")) {
        return true;
      }
    }
    return false;
  }


  private List<ClassData> getImplementers(Map<String, ClassData> classes) {
    List<ClassData> subclasses = new ArrayList<>();
    for (Map.Entry<String, ClassData> entry : classes.entrySet()) {
      addSubclass(entry, subclasses);
    }
    return subclasses;
  }

  private static void addSubclass(Map.Entry<String, ClassData> entry, List<ClassData> subclasses) {
    if (!entry.getValue().getInterfaceFullNames().isEmpty()) {
      subclasses.add(entry.getValue());
    }
  }
}
