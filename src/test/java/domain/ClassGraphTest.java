package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import datasource.DirLoader;
import datasource.FilesLoader;
import domain.javadata.ClassData;

/**
 * Test Class Graph on 
 */
public class ClassGraphTest {
	private static final String STRING_RESOURCE_PATH = "src/test/resources/graphtest";
    private static final String STRING_RESOURCE_PATH2 = "src/test/resources/graphtest2";

	ClassGraph graph;
    Map<String,ClassData> stringToClass;

    ClassGraph graph2;
    Map<String,ClassData> stringToClass2;

	@BeforeEach
	public void setup() throws IOException {
		FilesLoader fl = new DirLoader(STRING_RESOURCE_PATH);
        graph = new ClassGraph(TestUtility.getMap(fl.loadFiles("class")));
        stringToClass = graph.getClasses();

        fl = new DirLoader(STRING_RESOURCE_PATH2);
        graph2 = new ClassGraph(TestUtility.getMap(fl.loadFiles("class")));
        stringToClass2 = graph2.getClasses();
	}

	@Test
	public void testHasClasses() {
		Set<String> expectedClasses = new HashSet<String>();
        expectedClasses.add("engine.AbsIntegration");
        expectedClasses.add("engine.BooleIntegration");
        expectedClasses.add("engine.IntegrationMethod");
        expectedClasses.add("engine.Integrator");
        expectedClasses.add("engine.Simpson38Integration");
        expectedClasses.add("engine.SimpsonIntegration");
        expectedClasses.add("engine.TrapezoidIntegration");
        assertEquals(expectedClasses.size(), stringToClass.keySet().size());
        Iterator<String> it = stringToClass.keySet().iterator();
        while (it.hasNext()) {
            expectedClasses.remove(it.next());
        }
        assertEquals(0, expectedClasses.size());
	}

	@Test
	public void testEdges() {
        Iterator<String> it1 = graph.getClasses().keySet().iterator();
        String temp1;
        Iterator<String> it2;
        String temp2;
        while (it1.hasNext()) {
            it2 = graph.getClasses().keySet().iterator();
            temp1 = it1.next();
            while (it2.hasNext()) {
                temp2 = it2.next();
                if (temp2.equals("engine.IntegrationMethod")) {
                    if (temp1.equals("engine.Simpson38Integration")) {
                        assertEquals(4, graph.getWeight(graph.getIndex(temp1),graph.getIndex(temp2)));
                    } else if (temp1.equals("engine.AbsIntegration")) {
                        assertEquals(6, graph.getWeight(graph.getIndex(temp1),graph.getIndex(temp2))); 
                    } else if (temp1.equals("engine.BooleIntegration")) {
                        assertEquals(4, graph.getWeight(graph.getIndex(temp1),graph.getIndex(temp2))); 
                    } else if (temp1.equals("engine.Integrator")) {
                        assertEquals(1, graph.getWeight(graph.getIndex(temp1),graph.getIndex(temp2))); 
                    } else if (temp1.equals("engine.SimpsonIntegration")) {
                        assertEquals(4, graph.getWeight(graph.getIndex(temp1),graph.getIndex(temp2))); 
                    } else if (temp1.equals("engine.TrapezoidIntegration")) {
                        assertEquals(4, graph.getWeight(graph.getIndex(temp1),graph.getIndex(temp2))); 
                    } else if (temp1.equals("engine.IntegrationMethod")) {
                        assertEquals(0, graph.getWeight(graph.getIndex(temp1),graph.getIndex(temp2))); 
                    }
                } else {
                    assertEquals(0, graph.getWeight(graph.getIndex(temp1),graph.getIndex(temp2)));                    
                }
            }
            if (temp1.equals("engine.Simpson38Integration")) {
                assertEquals(0, graph.inDegree(graph.getIndex(temp1))); 
                assertEquals(1, graph.outDegree(graph.getIndex(temp1)));
            } else if (temp1.equals("engine.AbsIntegration")) {
                assertEquals(0, graph.inDegree(graph.getIndex(temp1)));
                assertEquals(1, graph.outDegree(graph.getIndex(temp1))); 
            } else if (temp1.equals("engine.BooleIntegration")) {
                assertEquals(0, graph.inDegree(graph.getIndex(temp1)));
                assertEquals(1, graph.outDegree(graph.getIndex(temp1)));
            } else if (temp1.equals("engine.Integrator")) {
                assertEquals(0, graph.inDegree(graph.getIndex(temp1)));
                assertEquals(1, graph.outDegree(graph.getIndex(temp1)));
            } else if (temp1.equals("engine.SimpsonIntegration")) {
                assertEquals(0, graph.inDegree(graph.getIndex(temp1)));
                assertEquals(1, graph.outDegree(graph.getIndex(temp1)));
            } else if (temp1.equals("engine.TrapezoidIntegration")) {
                assertEquals(0, graph.inDegree(graph.getIndex(temp1)));
                assertEquals(1, graph.outDegree(graph.getIndex(temp1)));
            } else if (temp1.equals("engine.IntegrationMethod")) {
                assertEquals(6, graph.inDegree(graph.getIndex(temp1)));
                assertEquals(0, graph.outDegree(graph.getIndex(temp1)));
            }
        }   
	}

    @Test
    public void testMisc() {
        assertFalse(ClassGraph.checkExtend(0));
        assertFalse(ClassGraph.checkImplement(0));
        assertFalse(ClassGraph.checkHasA(0));
        assertFalse(ClassGraph.checkDepends(0));

        assertTrue(ClassGraph.checkExtend(15));
        assertTrue(ClassGraph.checkImplement(15));
        assertTrue(ClassGraph.checkHasA(15));
        assertTrue(ClassGraph.checkDepends(15));

        assertFalse(ClassGraph.checkExtend(1));
        assertFalse(ClassGraph.checkImplement(1));
        assertFalse(ClassGraph.checkHasA(1));
        assertTrue(ClassGraph.checkDepends(1));

        assertTrue(ClassGraph.checkExtend(11));
        assertFalse(ClassGraph.checkImplement(11));
        assertTrue(ClassGraph.checkHasA(11));
        assertTrue(ClassGraph.checkDepends(11));

        
        int j = graph.getIndex("engine.IntegrationMethod");
        int[] col = graph.column(j);
        for (int i : col) {
            if (graph.indexToClass(i).equals("engine.Simpson38Integration")) {
                assertEquals(4, col[i]);
            } else if (graph.indexToClass(i).equals("engine.AbsIntegration")) {
                assertEquals(6, col[i]); 
            } else if (graph.indexToClass(i).equals("engine.BooleIntegration")) {
                assertEquals(4, col[i]); 
            } else if (graph.indexToClass(i).equals("engine.Integrator")) {
                assertEquals(1, col[i]); 
            } else if (graph.indexToClass(i).equals("engine.SimpsonIntegration")) {
                assertEquals(4, col[i]); 
            } else if (graph.indexToClass(i).equals("engine.TrapezoidIntegration")) {
                assertEquals(4, col[i]); 
            } else if (graph.indexToClass(i).equals("engine.IntegrationMethod")) {
                assertEquals(0, col[i]); 
            }
        }

    }

    @Test
    public void testABC() {
        assertEquals(2, graph2.getWeight(graph2.getIndex("A"), graph2.getIndex("B")));
        assertEquals(8, graph2.getWeight(graph2.getIndex("B"), graph2.getIndex("C")));
        assertEquals(1, graph2.getWeight(graph2.getIndex("C"), graph2.getIndex("A")));
        ClassGraphIterator it = graph2.graphIterator(graph2.getIndex("A"));
        assertEquals(Set.of(), it.followEdge(2, 2, 0, 2));
        assertEquals(Set.of(), it.followEdge(1, 2, 1, 2));
        for (ClassGraphIterator i : it.followEdge(2, 0, 1, 0)) {
            assertEquals(graph2.getIndex("B"), i.getCurrent());
        }
        for (ClassGraphIterator i : it.followEdge(2, 2, 1, 2)) {
            assertEquals(graph2.getIndex("B"), i.getCurrent());
        }
        for (ClassGraphIterator i : it.followEdge()) {
            assertEquals(graph2.getIndex("B"), i.getCurrent());
        }
        for (ClassGraphIterator i : it.followEdge()) {
            assertEquals(List.of(graph2.getIndex("A"), graph2.getIndex("B")), i.getPath());
        }
        for (ClassGraphIterator i : it.followEdge()) {
            for (ClassGraphIterator j : i.followEdge()) {
                assertEquals(List.of(graph2.getIndex("A"), graph2.getIndex("B"), graph2.getIndex("C")), j.getPath());
                for (ClassGraphIterator k : j.followEdge()) {
                    assertEquals(List.of(graph2.getIndex("A"), graph2.getIndex("B"), graph2.getIndex("C"), graph2.getIndex("A")), k.getPath());
                    assertTrue(k.hasCycle());
                }
            }
        }
    }


}
