package datasource;

import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DirLoaderTest {
  private static final String CLASS_FILE_EXT = "class";

  @Test
  public void loadFiles_EmptyPath_ExpectedException() {
    DirLoader dirLoader = new DirLoader("");
    Exception exception = assertThrows(InvalidParameterException.class, () -> {
      dirLoader.loadFiles(CLASS_FILE_EXT);
    });
    assertEquals("Empty path is not allowed", exception.getMessage());
  }

  @Test
  public void loadFiles_NonDirectoryPath_ExpectedException() {
    DirLoader dirLoader = new DirLoader("test.txt");
    Exception exception = assertThrows(IllegalStateException.class, () -> {
      dirLoader.loadFiles(CLASS_FILE_EXT);
    });
    assertEquals("No such directory: test.txt", exception.getMessage());
  }
}
