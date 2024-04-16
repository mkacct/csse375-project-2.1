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
    String path = "src/test/resources/DirLoaderNonDirectoryFile.txt";
    DirLoader dirLoader = new DirLoader(path);
    Exception exception = assertThrows(IllegalStateException.class, () -> {
      dirLoader.loadFiles(CLASS_FILE_EXT);
    });
    assertEquals("Provided path is not a directory: " + path, exception.getMessage());
  }

  @Test
  public void loadFiles_NonExistentDirectory_ExpectedException() {
    String path = "src/test/resources/not_existent_directory";
    DirLoader dirLoader = new DirLoader(path);
    Exception exception = assertThrows(IllegalStateException.class, () -> {
      dirLoader.loadFiles(CLASS_FILE_EXT);
    });
    assertEquals("Provided path is not a directory: " + path, exception.getMessage());
  }

  @Test
  public void loadFiles_NonClassFileDirectory_ExpectedException() {
    String path = "src/test/resources/DirLoaderNonClassFileDirectory";
    DirLoader dirLoader = new DirLoader(path);
    Exception exception = assertThrows(IllegalStateException.class, () -> {
      dirLoader.loadFiles(CLASS_FILE_EXT);
    });
    assertEquals("src/test/resources/DirLoaderNonClassFileDirectory/hello.txt is not a class file", exception.getMessage());
  }
}
