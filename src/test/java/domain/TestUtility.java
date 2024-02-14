package domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import domain.javadata.ClassData;
import domain.javadata.ClassReaderUtil;

public class TestUtility {
    public static Map<String, ClassData> getMap(Set<byte[]> files) {
        Map<String, ClassData> map = new HashMap<String, ClassData>();
        Iterator<byte[]> it = files.iterator();
        ClassData temp;
        while (it.hasNext()) {
            temp = ClassReaderUtil.read(it.next());
            map.put(temp.getFullName(), temp);
        }
        return map;
    }
}
