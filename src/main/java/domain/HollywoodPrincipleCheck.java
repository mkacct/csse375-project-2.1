package domain;

import datasource.Configuration;
import domain.javadata.ClassData;

import java.util.Map;
import java.util.Set;

public class HollywoodPrincipleCheck extends Check {
  private static final String NAME = "hollywoodPrinciple";

  public HollywoodPrincipleCheck() { super(NAME); }

  @Override
  public Set<Message> run(Map<String, ClassData> classes, Configuration config) {
    return null;
  }
}
