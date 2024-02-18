package domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
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

    public static Set<String> getEntireFile(String file) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(file));
        Set<String> ret = new HashSet<String>();
        while (scan.hasNextLine()) {
            ret.add(scan.nextLine());
        }

        scan.close();
        return ret;
    }
}
