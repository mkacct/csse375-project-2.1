package domain;

import domain.javadata.ClassData;
import domain.javadata.ClassReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalesnmChecksTest {

    private static final String STRING_RESOURCE_PATH = "java/lang/String.class";

    private byte[] javaBytecode;

    @BeforeEach
    public void setup() throws IOException {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(STRING_RESOURCE_PATH)) {
            this.javaBytecode = in.readAllBytes();
        }
    }

    @Test
    public void testUnusedAbstractionsCheck() {
        ClassData classData = ClassReaderUtil.read(this.javaBytecode);
        assertEquals(false,classData.isAbstract());
    }



}
