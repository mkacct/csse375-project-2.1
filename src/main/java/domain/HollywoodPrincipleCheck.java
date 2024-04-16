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

  public HollywoodPrincipleCheck() {
    super(NAME);
  }

  /**
   * General process:
   * 1. Iterate through all classes
   * 2. If a class is abstract, then get all of its implementers
   * 3. Iterate through the implemeneters
   * 4. If the implemeneters implements the abstract class, then send a warning message
   */
  @Override
  public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
    return Set.of();
  }
}
