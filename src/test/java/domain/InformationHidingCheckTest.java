package domain;

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
import domain.javadata.ClassData;
import domain.javadata.ClassDataCollection;
import domain.javadata.ClassReaderUtil;


public class InformationHidingCheckTest {
    private static final String[] CLASS_DIR_PATHS = new String[]{"src/test/resources/InformationHidingTest1",
            "src/test/resources/InformationHidingTest2",
            "src/test/resources/InformationHidingTest3"
    };

    private static final Configuration CONFIG_EMPTY = new Configuration(Map.of());

    private List<ClassDataCollection> classes = new ArrayList<ClassDataCollection>();

    @BeforeEach
    public void setup() throws IOException {
        for (String CLASS_DIR_PATH : CLASS_DIR_PATHS) {
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
    public void InformationHidingCheckTest1() {
        Check check = new InformationHidingCheck();
        Set<Message> result = check.run(this.classes.get(0), CONFIG_EMPTY);
        //System.out.println(result.toString());
        Message message1 = new Message(MessageLevel.WARNING, "The class Class1 contains" +
                " the following fields that violate information hiding: [bad_field]");
        Message message2 = new Message(MessageLevel.WARNING,"The class Class2 contains" +
                " the following fields that violate information hiding: [num2]");
        Set<Message> actual = Set.of(message1,message2);
        assertEquals(result,actual);
//        assertEquals("[[WARN] The class Class1 contains the following fields" +
//                " that violate information hiding: [bad_field], [WARN] The class Class2 contains" +
//                " the following fields that violate information hiding: [num2]]", result.toString());


    }

    @Test
    public void InformationHidingCheckTest2() {
        Check check = new InformationHidingCheck();
        Set<Message> result = check.run(this.classes.get(1), CONFIG_EMPTY);
        //System.out.println(result.toString());
        Message message1 = new Message(MessageLevel.WARNING, "The class Class1 contains" +
                " the following fields that violate information hiding: [bad_field2, bad_field1]");
        Message message2 = new Message(MessageLevel.WARNING,"The class Class2 contains" +
                " the following fields that violate information hiding: [num5, num4, num2]");
        Set<Message> actual = Set.of(message1,message2);
        assertEquals(result,actual);


//        assertEquals("[[WARN] The class Class2 contains the following fields that violate" +
//                " information hiding: [num5, num4, num2], [WARN] The class Class1 contains the following" +
//                " fields that violate information hiding: [bad_field2, bad_field1]]", result.toString());


    }

    @Test
    public void InformationHidingCheckTest3() {
        Check check = new InformationHidingCheck();
        Set<Message> result = check.run(this.classes.get(2), CONFIG_EMPTY);
        //System.out.println(result.toString());
        Message message1 = new Message(MessageLevel.WARNING, "The class Class1 contains" +
                " the following fields that violate information hiding: [bad_field2, bad_field1, name, age]");
        Message message2 = new Message(MessageLevel.WARNING,"The class Class2 contains" +
                " the following fields that violate information hiding: [num1, num5, num4, num2]");
        Set<Message> actual = Set.of(message1,message2);
        assertEquals(result,actual);
//        assertEquals("[[WARN] The class Class1 contains the following fields that violate" +
//                " information hiding: [bad_field2, bad_field1, name, age], [WARN] The class" +
//                " Class2 contains the following fields that violate information hiding:" +
//                " [num1, num5, num4, num2]]", result.toString());

    }


}
