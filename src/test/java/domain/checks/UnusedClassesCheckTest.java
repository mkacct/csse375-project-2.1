package domain.checks;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import datasource.Configuration;
import datasource.DirLoader;
import datasource.FilesLoader;
import domain.Check;
import domain.Message;
import domain.javadata.ClassData;
import domain.javadata.ClassDataCollection;
import domain.javadata.ClassReaderUtil;

public class UnusedClassesCheckTest {
    private static final String[] CLASS_DIR_PATHS = new String[] {"src/test/resources/UnusedClassesTest1",
            "src/test/resources/UnusedClassesTest2",
            "src/test/resources/UnusedClassesTest3",
            "src/test/resources/UnusedClassesTest4"};

    private static final Configuration CONFIG_EMPTY = new Configuration(Map.of());

    private List<ClassDataCollection> classes = new ArrayList<ClassDataCollection>();

    @BeforeEach
    public void setup() throws IOException {
        for(String CLASS_DIR_PATH : CLASS_DIR_PATHS) {



        FilesLoader loader = new DirLoader(CLASS_DIR_PATH);
        Set<byte[]> bytecodes = loader.loadFiles("class");
        ClassDataCollection item = new ClassDataCollection();
        for (byte[] bytecode : bytecodes) {
            ClassData classData = ClassReaderUtil.read(bytecode);
            item.add(classData);
        }
        this.classes.add(item);

        }
    }


    @Test
    public void UnusedAbstractionsCheckTest1 () {
        Check check = new UnusedAbstractionsCheck();
        Set<Message> result = check.run(this.classes.get(0),CONFIG_EMPTY);
        assertEquals("[[WARN] The Following class is not used, please consider" +
                " using them or deleting them:  (BadClass)]",result.toString());

    }

    @Test
    public void UnusedAbstractionsCheckTest2 () {
        Check check = new UnusedAbstractionsCheck();
        Set<Message> result = check.run(this.classes.get(1),CONFIG_EMPTY);
        assertEquals("[]",result.toString());
    }

    @Test
    public void UnusedAbstractionsCheckTest3 () {
        Check check = new UnusedAbstractionsCheck();
        Set<Message> result = check.run(this.classes.get(2),CONFIG_EMPTY);
        assertEquals("[[WARN] The Following class is not used, please consider" +
                " using them or deleting them:  (BadInterface)]",result.toString());
    }

    @Test
    public void UnusedAbstractionsCheckTest4 () {
        Check check = new UnusedAbstractionsCheck();
        Set<Message> result = check.run(this.classes.get(3),CONFIG_EMPTY);
        assertEquals("[]",result.toString());
    }





}
