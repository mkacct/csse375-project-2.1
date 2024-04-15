package domain.javadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Collection of ClassData indexed by full name, for passing into checks.
 * Iterating over the collection skips compiler-generated classes, though you can still get them by name.
 */
public class ClassDataCollection implements Collection<ClassData> {
	private final Map<String, ClassData> classesByFullName;

	/**
	 * @param classes classes to add to the collection
	 */
	public ClassDataCollection(ClassData... classes) {
		this.classesByFullName = new HashMap<String, ClassData>();
		for (ClassData classData : classes) {
			this.add(classData);
		}
	}

	/**
	 * @param classes set of classes to add to the collection
	 */
	public ClassDataCollection(Set<ClassData> classes) {
		this.classesByFullName = new HashMap<String, ClassData>();
		this.addAll(classes);
	}

	/**
	 * @param toCopy ClassDataCollection to copy
	 */
	public ClassDataCollection(ClassDataCollection toCopy) {
		this.classesByFullName = new HashMap<String, ClassData>(toCopy.classesByFullName);
	}

	/**
	 * @param fullName a fully qualified class name
	 * @return the ClassData with the given full name, or null if not found
	 */
	public ClassData get(String fullName) {
		return this.classesByFullName.get(fullName);
	}

	@Override
	public int size() {
		int count = 0;
		for (ClassData classData : this.classesByFullName.values()) {
			if (!NameUtil.isCompilerGenerated(classData.getFullName())) {
				count++;
			}
		}
		return count;
	}

	/**
	 * @return the number of classes in the collection, including compiler-generated classes
	 */
	public int sizeIncludingCompilerGenerated() {
		return this.classesByFullName.size();
	}

	@Override
	public boolean isEmpty() {
		return this.classesByFullName.size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		return this.classesByFullName.containsValue(o);
	}

	private Set<ClassData> getNonCompilerGeneratedClasses() {
		Set<ClassData> nonCompilerGeneratedClasses = new HashSet<ClassData>();
		for (ClassData classData : this.classesByFullName.values()) {
			if (!NameUtil.isCompilerGenerated(classData.getFullName())) {
				nonCompilerGeneratedClasses.add(classData);
			}
		}
		return nonCompilerGeneratedClasses;
	}

	@Override
	public Iterator<ClassData> iterator() {
		return this.getNonCompilerGeneratedClasses().iterator();
	}

	/**
	 * @return an iterator over all classes in the collection, including compiler-generated classes
	 */
	public Iterator<ClassData> iteratorIncludingCompilerGenerated() {
		Set<ClassData> allClasses = new HashSet<ClassData>(this.classesByFullName.values());
		return allClasses.iterator();
	}

	@Override
	public Object[] toArray() {
		return this.getNonCompilerGeneratedClasses().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.getNonCompilerGeneratedClasses().toArray(a);
	}

	/**
	 * @return an array of all classes in the collection, including compiler-generated classes
	 */
	public ClassData[] toArrayIncludingCompilerGenerated() {
		return this.classesByFullName.values().toArray(new ClassData[0]);
	}

	/**
	 * @param a array to fill with classes
	 * @return an array of all classes in the collection, including compiler-generated classes
	 */
	public ClassData[] toArrayIncludingCompilerGenerated(ClassData[] a) {
		return this.classesByFullName.values().toArray(a);
	}

	@Override
	public boolean add(ClassData e) {
		this.classesByFullName.put(e.getFullName(), e);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		ClassData removed = this.classesByFullName.remove(((ClassData)o).getFullName());
		return removed != null;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.classesByFullName.values().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends ClassData> c) {
		boolean didAdd = false;
		for (ClassData classData : c) {
			boolean didJustAdd = this.add(classData);
			if (didJustAdd) {didAdd = true;}
		}
		return didAdd;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.classesByFullName.values().removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.classesByFullName.values().retainAll(c);
	}

	@Override
	public void clear() {
		this.classesByFullName.clear();
	}

	/**
	 * @param fullName a fully qualified class name
	 * @return whether the collection contains a class with the given full name
	 */
	public boolean containsFullName(String fullName) {
		return this.classesByFullName.containsKey(fullName);
	}

	/**
	 * @return a set of all full names of classes in the collection (excluding compiler-generated classes)
	 */
	public Set<String> getFullNames() {
		Set<String> fullNames = new HashSet<String>();
		for (ClassData classData : this.getNonCompilerGeneratedClasses()) {
			fullNames.add(classData.getFullName());
		}
		return fullNames;
	}

	/**
	 * @return a set of all full names of classes in the collection (including compiler-generated classes)
	 */
	public Set<String> getFullNamesIncludingCompilerGenerated() {
		return new HashSet<String>(this.classesByFullName.keySet());
	}

	/**
	 * @return a set of all classes in the collection (excluding compiler-generated classes)
	 */
	public Set<ClassData> getClasses() {
		return this.getNonCompilerGeneratedClasses();
	}

	/**
	 * @return a set of all classes in the collection (including compiler-generated classes)
	 */
	public Set<ClassData> getClassesIncludingCompilerGenerated() {
		return new HashSet<ClassData>(this.classesByFullName.values());
	}
}
