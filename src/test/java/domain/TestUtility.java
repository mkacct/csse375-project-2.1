package domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import domain.javadata.ClassData;
import domain.javadata.ClassDataCollection;
import domain.javadata.ClassReaderUtil;

public class TestUtility {
    public static ClassDataCollection toClassDataCollection(Set<byte[]> files) {
        ClassDataCollection classes = new ClassDataCollection();
        Iterator<byte[]> it = files.iterator();
        ClassData temp;
        while (it.hasNext()) {
            temp = ClassReaderUtil.read(it.next());
            classes.add(temp);
        }
        return classes;
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
