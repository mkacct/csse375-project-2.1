package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.javadata.ClassData;
import domain.javadata.ClassDataCollection;

public class ClassDataCollectionTest {
	private static final String[] USER_CLASS_NAMES = {"Foo", "Bar", "Baz"};
	private static final String[] COMPILER_CLASS_NAMES = {"$CompilerGenerated"};

	private Set<ClassData> allClasses;
	private Set<ClassData> userClasses;

	@BeforeEach
	public void setup() {
		this.allClasses = new HashSet<>();
		this.userClasses = new HashSet<>();
		for (String name : USER_CLASS_NAMES) {
			ClassData classData = new StubClassData(name);
			this.allClasses.add(classData);
			this.userClasses.add(classData);
		}
		for (String name : COMPILER_CLASS_NAMES) {
			ClassData classData = new StubClassData(name);
			this.allClasses.add(classData);
		}
	}

	@Test
	public void testConstructorWithNoClasses() {
		ClassDataCollection classes = new ClassDataCollection();
		assertTrue(classes.isEmpty());
	}

	@Test
	public void testConstructorWithClasses() {
		ClassData[] classArr = this.allClasses.toArray(new ClassData[0]);
		ClassDataCollection classes = new ClassDataCollection(classArr);
		assertEquals(this.userClasses, classes.getClasses());
	}

	@Test
	public void testConstructorWithClassSet() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		assertEquals(this.userClasses, classes.getClasses());
	}

	@Test
	public void testCopyConstructor() {
		ClassDataCollection original = new ClassDataCollection(this.allClasses);
		ClassDataCollection copy = new ClassDataCollection(original);
		original.clear();
		assertEquals(Set.of(), original.getClasses());
		assertEquals(this.userClasses, copy.getClasses());
	}

	@Test
	public void testGetExpectUserClass() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		ClassData classData = classes.get("Foo");
		assertEquals("Foo", classData.getFullName());
	}

	@Test
	public void testGetExpectCompilerClass() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		ClassData classData = classes.get("$CompilerGenerated");
		assertEquals("$CompilerGenerated", classData.getFullName());
	}

	@Test
	public void testGetExpectNull() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		ClassData classData = classes.get("Pluh");
		assertNull(classData);
	}

	@Test
	public void testSizeWithEmpty() {
		ClassDataCollection classes = new ClassDataCollection();
		assertEquals(0, classes.size());
	}

	@Test
	public void testSizeWithClasses() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		assertEquals(this.userClasses.size(), classes.size());
	}

	@Test
	public void testSizeICGWithEmpty() {
		ClassDataCollection classes = new ClassDataCollection();
		assertEquals(0, classes.sizeIncludingCompilerGenerated());
	}

	@Test
	public void testSizeICGWithClasses() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		assertEquals(this.allClasses.size(), classes.sizeIncludingCompilerGenerated());
	}

	@Test
	public void testIsEmptyWithEmpty() {
		ClassDataCollection classes = new ClassDataCollection();
		assertTrue(classes.isEmpty());
	}

	@Test
	public void testIsEmptyWithClasses() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		assertFalse(classes.isEmpty());
	}

	@Test
	public void testContainsExpectTrue() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		ClassData example = this.userClasses.iterator().next();
		assertTrue(classes.contains(example));
	}

	@Test
	public void testContainsExpectFalse() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		assertFalse(classes.contains(new StubClassData("Nope")));
	}

	@Test
	public void testIterator() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		Set<ClassData> actual = new HashSet<>();
		Iterator<ClassData> iter = classes.iterator();
		while (iter.hasNext()) {
			actual.add(iter.next());
		}
		assertEquals(this.userClasses, actual);
	}

	@Test
	public void testIteratorICG() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		Set<ClassData> actual = new HashSet<>();
		Iterator<ClassData> iter = classes.iteratorIncludingCompilerGenerated();
		while (iter.hasNext()) {
			actual.add(iter.next());
		}
		assertEquals(this.allClasses, actual);
	}

	@Test
	public void testToArray() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		Object[] actual = classes.toArray();
		assertEquals(this.userClasses, Set.of(actual));
	}

	@Test
	public void testToArrayWithArray() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		ClassData[] actual = classes.toArray(new ClassData[0]);
		assertEquals(this.userClasses, Set.of(actual));
	}

	@Test
	public void testToArrayICG() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		Object[] actual = classes.toArrayIncludingCompilerGenerated();
		assertEquals(this.allClasses, Set.of(actual));
	}

	@Test
	public void testToArrayWithArrayICG() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		ClassData[] actual = classes.toArrayIncludingCompilerGenerated(new ClassData[0]);
		assertEquals(this.allClasses, Set.of(actual));
	}

	@Test
	public void testAdd() {
		ClassDataCollection classes = new ClassDataCollection();
		ClassData classData = new StubClassData("Ex");
		classes.add(classData);
		assertEquals(Set.of(classData), classes.getClasses());
	}

	@Test
	public void testRemoveExpectTrue() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		ClassData example = this.userClasses.iterator().next();
		Set<ClassData> expected = new HashSet<>(this.userClasses);
		expected.remove(example);
		boolean didRemove = classes.remove(example);
		assertTrue(didRemove);
		assertEquals(expected, classes.getClasses());
	}

	@Test
	public void testRemoveExpectFalse() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		boolean didRemove = classes.remove(new StubClassData("Nah"));
		assertFalse(didRemove);
		assertEquals(this.userClasses, classes.getClasses());
	}

	@Test
	public void testContainsAllExpectTrue() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		assertTrue(classes.containsAll(this.userClasses));
		assertTrue(classes.containsAll(this.allClasses));
	}

	@Test
	public void testContainsAllExpectFalse() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		ClassData example = this.userClasses.iterator().next();
		assertFalse(classes.containsAll(Set.of(example, new StubClassData("Nope"))));
	}

	@Test
	public void testAddAll() {
		Set<ClassData> init = Set.of(new StubClassData("Init1"), new StubClassData("Init2"));
		ClassDataCollection classes = new ClassDataCollection(init);
		Set<ClassData> addl = Set.of(new StubClassData("Addl1"), new StubClassData("Addl2"));
		classes.addAll(addl);
		Set<ClassData> expected = new HashSet<>(init);
		expected.addAll(addl);
		assertEquals(expected, classes.getClasses());
	}

	@Test
	public void testRemoveAllExpectTrue() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		Set<ClassData> toRemove = Set.of(this.userClasses.iterator().next(), new StubClassData("IgnoreMe"));
		Set<ClassData> expected = new HashSet<>(this.userClasses);
		expected.removeAll(toRemove);
		boolean didRemove = classes.removeAll(toRemove);
		assertTrue(didRemove);
		assertEquals(expected, classes.getClasses());
	}

	@Test
	public void testRemoveAllExpectFalse() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		Set<ClassData> toRemove = Set.of(new StubClassData("IgnoreMe"), new StubClassData("Ditto"));
		boolean didRemove = classes.removeAll(toRemove);
		assertFalse(didRemove);
		assertEquals(this.userClasses, classes.getClasses());
	}

	@Test
	public void testRetainAllExpectTrue() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		Set<ClassData> toRetain = Set.of(this.userClasses.iterator().next(), new StubClassData("IgnoreMe"));
		Set<ClassData> expected = new HashSet<>(this.userClasses);
		expected.retainAll(toRetain);
		boolean didRetain = classes.retainAll(toRetain);
		assertTrue(didRetain);
		assertEquals(expected, classes.getClasses());
	}

	@Test
	public void testRetainAllExpectFalse() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		boolean didRetain = classes.retainAll(this.allClasses);
		assertFalse(didRetain);
		assertEquals(this.userClasses, classes.getClasses());
	}

	@Test
	public void testClear() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		classes.clear();
		assertTrue(classes.isEmpty());
	}

	@Test
	public void testContainsFullNameExpectTrue() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		assertTrue(classes.containsFullName("Foo"));
		assertTrue(classes.containsFullName("$CompilerGenerated"));
	}

	@Test
	public void testContainsFullNameExpectFalse() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		assertFalse(classes.containsFullName("Nah"));
	}

	@Test
	public void testGetFullNames() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		assertEquals(getFullNames(this.userClasses), classes.getFullNames());
	}

	@Test
	public void testGetFullNamesICG() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		assertEquals(getFullNames(this.allClasses), classes.getFullNamesIncludingCompilerGenerated());
	}

	@Test
	public void testGetClasses() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		assertEquals(this.userClasses, classes.getClasses());
	}

	@Test
	public void testGetClassesICG() {
		ClassDataCollection classes = new ClassDataCollection(this.allClasses);
		assertEquals(this.allClasses, classes.getClassesIncludingCompilerGenerated());
	}

	private static Set<String> getFullNames(Set<ClassData> classes) {
		Set<String> fullNames = new HashSet<>();
		for (ClassData classData : classes) {
			fullNames.add(classData.getFullName());
		}
		return fullNames;
	}
}
