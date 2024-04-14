package domain.javadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Collection of ClassData indexed by full name, for passing into checks.
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
		return this.classesByFullName.size();
	}

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		return this.classesByFullName.containsValue(o);
	}

	@Override
	public Iterator<ClassData> iterator() {
		return this.classesByFullName.values().iterator();
	}

	@Override
	public Object[] toArray() {
		return this.classesByFullName.values().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
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

	public boolean containsFullName(String fullName) {
		return this.classesByFullName.containsKey(fullName);
	}

	public Set<String> getFullNames() {
		return this.classesByFullName.keySet();
	}
}
