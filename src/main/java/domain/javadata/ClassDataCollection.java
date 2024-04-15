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

	public ClassData get(String fullName) {
		return this.classesByFullName.get(fullName);
	}

	public ClassDataCollection() {
		this.classesByFullName = new HashMap<String, ClassData>();
	}

	public ClassDataCollection(ClassDataCollection toCopy) {
		this.classesByFullName = new HashMap<String, ClassData>(toCopy.classesByFullName);
	}

	public ClassDataCollection(ClassData... classes) {
		this.classesByFullName = new HashMap<String, ClassData>();
		for (ClassData classData : classes) {
			this.add(classData);
		}
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

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
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

	@Override
	public Object[] toArray() {
		return this.getNonCompilerGeneratedClasses().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.getNonCompilerGeneratedClasses().toArray(a);
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

	public boolean containsFullName(String fullName) {
		return this.classesByFullName.containsKey(fullName);
	}

	public Set<String> getFullNames() {
		Set<String> fullNames = new HashSet<String>();
		for (ClassData classData : this.getNonCompilerGeneratedClasses()) {
			fullNames.add(classData.getFullName());
		}
		return fullNames;
	}
}
