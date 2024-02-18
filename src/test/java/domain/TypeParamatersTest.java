package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import datasource.DirLoader;
import datasource.FilesLoader;
import domain.javadata.ClassData;
import domain.javadata.FieldData;
import domain.javadata.MethodData;
import domain.javadata.TypeStructure;
import domain.javadata.VariableData;

public class TypeParamatersTest {
    private static final String STRING_RESOURCE_PATH = "src/test/resources/TypeParamaterTest";
    private static final String CLASS_NAME = "App";
    Map<String, ClassData> map;

	@BeforeEach
	public void setup() throws IOException {
		FilesLoader fl = new DirLoader(STRING_RESOURCE_PATH);

        map = TestUtility.getMap(fl.loadFiles("class"));
	}

    @Test
    public void testFields() {
		ClassData classData = map.get(CLASS_NAME);
        for (FieldData f : classData.getFields()) {
            if (f.getName().equals("a")) {
                assertEquals(Set.of("package1.A"), f.getAllTypeFullName());
                assertEquals(0, f.typeParam().getNumArrays());
                assertEquals(List.of(), f.typeParam().getSubTypes());
            } else if (f.getName().equals("b")) {
                assertEquals(Set.of("package1.package2.B"), f.getAllTypeFullName());
                assertEquals(0, f.typeParam().getNumArrays());
                assertEquals(List.of(), f.typeParam().getSubTypes());
            } else if (f.getName().equals("c")) {
                assertEquals(Set.of("java.lang.Integer"), f.getAllTypeFullName());
                assertEquals(0, f.typeParam().getNumArrays());
                assertEquals(List.of(), f.typeParam().getSubTypes());
            } else if (f.getName().equals("d")) {
                assertEquals(Set.of("package1.A"), f.getAllTypeFullName());
                assertEquals(1, f.typeParam().getNumArrays());
                assertEquals(List.of(), f.typeParam().getSubTypes());
            } else if (f.getName().equals("e")) {
                assertEquals(Set.of("package1.A", "java.util.Set"), f.getAllTypeFullName());
                assertEquals("java.util.Set", f.typeParam().getFullTypeName());
                assertEquals(0, f.typeParam().getNumArrays());
                assertEquals(1, f.typeParam().getSubTypes().size());
                for (TypeStructure t1 : f.typeParam().getSubTypes()) {
                    assertEquals("package1.A", t1.getFullTypeName());
                    assertEquals(0, t1.getNumArrays());
                    assertEquals(0, t1.getSubTypes().size());
                }
            } else if (f.getName().equals("f")) {
                assertEquals(Set.of("java.util.Set", "java.lang.Integer"), f.getAllTypeFullName());
                assertEquals("java.util.Set", f.typeParam().getFullTypeName());
                assertEquals(0, f.typeParam().getNumArrays());
                assertEquals(1, f.typeParam().getSubTypes().size());
                for (TypeStructure t1 : f.typeParam().getSubTypes()) {
                    assertEquals("java.lang.Integer", t1.getFullTypeName());
                    assertEquals(0, t1.getNumArrays());
                    assertEquals(0, t1.getSubTypes().size());
                }
            } else if (f.getName().equals("g")) {
                assertEquals(Set.of("java.util.Set",  "package1.A"), f.getAllTypeFullName());
                assertEquals("java.util.Set", f.typeParam().getFullTypeName());
                assertEquals(0, f.typeParam().getNumArrays());
                assertEquals(1, f.typeParam().getSubTypes().size());
                for (TypeStructure t1 : f.typeParam().getSubTypes()) {
                    assertEquals("package1.A", t1.getFullTypeName());
                    assertEquals(1, t1.getNumArrays());
                    assertEquals(0, t1.getSubTypes().size());
                }
            } else if (f.getName().equals("h")) {
                assertEquals(Set.of("java.util.Map", "package1.A", "package1.package2.B"), f.getAllTypeFullName());
                assertEquals("java.util.Map", f.typeParam().getFullTypeName());
                assertEquals(0, f.typeParam().getNumArrays());
                assertEquals(2, f.typeParam().getSubTypes().size());
                int i = 0;
                for (TypeStructure t1 : f.typeParam().getSubTypes()) {
                    if (i == 0) {
                        assertEquals("package1.A", t1.getFullTypeName());
                        i++;
                    } else {
                        assertEquals("package1.package2.B", t1.getFullTypeName());
                    }
                    assertEquals(0, t1.getNumArrays());
                    assertEquals(0, t1.getSubTypes().size());
                }
            } else if (f.getName().equals("i")) {
                assertEquals(Set.of("java.util.Set", "package1.A"), f.getAllTypeFullName());
                assertEquals("java.util.Set", f.typeParam().getFullTypeName());
                assertEquals(1, f.typeParam().getNumArrays());
                assertEquals(1, f.typeParam().getSubTypes().size());
                for (TypeStructure t1 : f.typeParam().getSubTypes()) {
                    assertEquals("package1.A", t1.getFullTypeName());
                    assertEquals(0, t1.getNumArrays());
                    assertEquals(0, t1.getSubTypes().size());
                }
            } else if (f.getName().equals("j")) {
                assertEquals(Set.of("java.util.Set", "package1.A"), f.getAllTypeFullName());
                assertEquals("java.util.Set", f.typeParam().getFullTypeName());
                assertEquals(1, f.typeParam().getNumArrays());
                assertEquals(1, f.typeParam().getSubTypes().size());
                for (TypeStructure t1 : f.typeParam().getSubTypes()) {
                    assertEquals("package1.A", t1.getFullTypeName());
                    assertEquals(1, t1.getNumArrays());
                    assertEquals(0, t1.getSubTypes().size());
                }
            } else if (f.getName().equals("k")) {
                assertEquals(Set.of("java.util.Map", "java.util.Set", "package1.A", "package1.package2.B"), f.getAllTypeFullName());
                assertEquals("java.util.Map", f.typeParam().getFullTypeName());
                assertEquals(0, f.typeParam().getNumArrays());
                assertEquals(2, f.typeParam().getSubTypes().size());
                int i = 0;
                for (TypeStructure t1 : f.typeParam().getSubTypes()) {
                    if (i == 0) {
                        assertEquals("package1.A", t1.getFullTypeName());
                        assertEquals(2, t1.getNumArrays());
                        assertEquals(0, t1.getSubTypes().size());
                        i++;
                    } else {
                        assertEquals(Set.of("java.util.Set", "package1.package2.B"), t1.getAllFullTypeNames());
                        assertEquals("java.util.Set", t1.getFullTypeName());
                        assertEquals(1, t1.getNumArrays());
                        assertEquals(1, t1.getSubTypes().size());
                        for (TypeStructure t2 : t1.getSubTypes()) {
                            assertEquals("package1.package2.B", t2.getFullTypeName());
                            assertEquals(1, t2.getNumArrays());
                            assertEquals(0, t2.getSubTypes().size());
                        }
                    }
                }
            } else { // weird
                assertEquals(Set.of("java.util.Map", "java.util.Set", "package1.A", "package1.package2.B", "package1.package2.C", "java.lang.Integer"), f.getAllTypeFullName());
                assertEquals("java.util.Map", f.typeParam().getFullTypeName());
                assertEquals(0, f.typeParam().getNumArrays());
                assertEquals(2, f.typeParam().getSubTypes().size());
                int i = 0;
                for (TypeStructure t1 : f.typeParam().getSubTypes()) {
                    if (i == 0) {
                        assertEquals(Set.of("java.util.Map", "package1.A", "java.lang.Integer"), t1.getAllFullTypeNames());
                        assertEquals("java.util.Map", t1.getFullTypeName());
                        assertEquals(1, t1.getNumArrays());
                        assertEquals(2, t1.getSubTypes().size());
                        int j = 0;
                        for (TypeStructure t2 : t1.getSubTypes()) {
                            if (j == 0) {
                                assertEquals("java.lang.Integer", t2.getFullTypeName());
                                assertEquals(1, t2.getNumArrays());
                                assertEquals(0, t2.getSubTypes().size());
                                j++;
                            } else {
                                assertEquals("package1.A", t2.getFullTypeName());
                                assertEquals(2, t2.getNumArrays());
                                assertEquals(0, t2.getSubTypes().size());
                            }
                        }
                        i++;
                    } else {
                        assertEquals(Set.of("java.util.Set", "package1.package2.B", "java.util.Map", "package1.package2.C"), t1.getAllFullTypeNames());
                        assertEquals("java.util.Set", t1.getFullTypeName());
                        assertEquals(1, t1.getNumArrays());
                        assertEquals(1, t1.getSubTypes().size());
                        for (TypeStructure t2 : t1.getSubTypes()) {
                            assertEquals(Set.of("java.util.Map", "package1.package2.B", "package1.package2.C"), t2.getAllFullTypeNames());
                            assertEquals("java.util.Map", t2.getFullTypeName());
                            assertEquals(1, t2.getNumArrays());
                            assertEquals(2, t2.getSubTypes().size());
                            int j = 0;
                            for (TypeStructure t3 : t2.getSubTypes()) {
                                if (j == 0) {
                                    assertEquals("package1.package2.B", t3.getFullTypeName());
                                    assertEquals(1, t3.getNumArrays());
                                    assertEquals(0, t3.getSubTypes().size());
                                    j++;
                                } else {
                                    assertEquals("package1.package2.C", t3.getFullTypeName());
                                    assertEquals(0, t3.getNumArrays());
                                    assertEquals(0, t3.getSubTypes().size());
                                }
                            }
                        }
                    }
                }
            }
        }
	}

    @Test
    public void testMethods() {
        ClassData classData = map.get(CLASS_NAME);
        for (MethodData m: classData.getMethods()) {
            if (m.getName().equals("z")) {
                assertEquals(Set.of("java.lang.Integer", "java.util.Set"),m.getAllReturnTypeFullName());
                assertEquals("java.util.Set", m.getReturnTypeStructure().getFullTypeName());
                assertEquals(0, m.getReturnTypeStructure().getNumArrays());
                assertEquals(1, m.getReturnTypeStructure().getSubTypes().size());
                for (TypeStructure t1 : m.getReturnTypeStructure().getSubTypes()) {
                    assertEquals("java.lang.Integer", t1.getFullTypeName());
                    assertEquals(0, t1.getNumArrays());
                    assertEquals(0, t1.getSubTypes().size());
                }
                int vi = 0;
                for (VariableData v : m.getParams()) {
                    if (vi == 0) {
                        vi++;
                        assertEquals(Set.of("int"), v.getAllTypeFullName());
                        assertEquals("int", v.typeParam().getFullTypeName());
                        assertEquals(0, v.typeParam().getNumArrays());
                        assertEquals(0, v.typeParam().getSubTypes().size());
                    } else {
                        assertEquals(Set.of("java.lang.String", "java.util.Set"), v.getAllTypeFullName());
                        assertEquals("java.util.Set", v.typeParam().getFullTypeName());
                        assertEquals(0, v.typeParam().getNumArrays());
                        assertEquals(1, v.typeParam().getSubTypes().size());
                        for (TypeStructure t1 : v.typeParam().getSubTypes()) {
                            assertEquals("java.lang.String", t1.getFullTypeName());
                            assertEquals(0, t1.getNumArrays());
                            assertEquals(0, t1.getSubTypes().size());
                        }
                    }
                }

                for (VariableData v : m.getLocalVariables()) {
                    if (v.name.equals("a")) {
                        assertEquals(Set.of("int"), v.getAllTypeFullName());
                        assertEquals("int", v.typeParam().getFullTypeName());
                        assertEquals(0, v.typeParam().getNumArrays());
                        assertEquals(0, v.typeParam().getSubTypes().size());
                    } else if (v.name.equals("strs")) {
                        assertEquals(Set.of("java.lang.String", "java.util.Set"), v.getAllTypeFullName());
                        assertEquals("java.util.Set", v.typeParam().getFullTypeName());
                        assertEquals(0, v.typeParam().getNumArrays());
                        assertEquals(1, v.typeParam().getSubTypes().size());
                        for (TypeStructure t1 : v.typeParam().getSubTypes()) {
                            assertEquals("java.lang.String", t1.getFullTypeName());
                            assertEquals(0, t1.getNumArrays());
                            assertEquals(0, t1.getSubTypes().size());
                        }
                    } else {
                        assertEquals(Set.of("java.lang.Boolean", "java.util.Set"), v.getAllTypeFullName());
                        assertEquals("java.util.Set", v.typeParam().getFullTypeName());
                        assertEquals(0, v.typeParam().getNumArrays());
                        assertEquals(1, v.typeParam().getSubTypes().size());
                        for (TypeStructure t1 : v.typeParam().getSubTypes()) {
                            assertEquals("java.lang.Boolean", t1.getFullTypeName());
                            assertEquals(0, t1.getNumArrays());
                            assertEquals(0, t1.getSubTypes().size());
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testGraph() {
        ClassGraph graph = new ClassGraph(map);
        assertTrue(ClassGraph.checkHasA(graph.getWeight(graph.getIndex("App"), graph.getIndex("package1.package2.C"))));
    }
}
