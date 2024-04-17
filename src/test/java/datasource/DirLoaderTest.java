package datasource;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DirLoaderTest {
  private static final String CLASS_FILE_EXT = "class";

  @Test
  public void loadFiles_EmptyPath_ExpectedException() throws IOException {
    String path = "";
    DirLoader dirLoader = new DirLoader(path);
    Exception exception = assertThrows(IllegalStateException.class, () -> {
      dirLoader.loadFiles(CLASS_FILE_EXT);
    });
    assertEquals("No such directory: " + path, exception.getMessage());
  }

  @Test
  public void loadFiles_NonDirectoryPath_ExpectedException() throws IOException {
    String path = "src/test/resources/DirLoaderNonDirectoryFile.txt";
    DirLoader dirLoader = new DirLoader(path);
    Exception exception = assertThrows(IllegalStateException.class, () -> {
      dirLoader.loadFiles(CLASS_FILE_EXT);
    });
    assertEquals("No such directory: " + path, exception.getMessage());
  }

  @Test
  public void loadFiles_NonExistentDirectory_ExpectedException() throws IOException {
    String path = "src/test/resources/not_existent_directory";
    DirLoader dirLoader = new DirLoader(path);
    Exception exception = assertThrows(IllegalStateException.class, () -> {
      dirLoader.loadFiles(CLASS_FILE_EXT);
    });
    assertEquals("No such directory: " + path, exception.getMessage());
  }

  @Test
  public void loadFiles_NonClassFileDirectory_ExpectedException() throws IOException {
    String path = "src/test/resources/DirLoaderNonClassFileDirectory";
    DirLoader dirLoader = new DirLoader(path);
    assertEquals(new HashSet<byte[]>(), dirLoader.loadFiles(CLASS_FILE_EXT));
  }

  @Test
  public void loadFiles_EmptyDirectory_ExpectedException() throws IOException {
    String path = "src/test/resources/DirLoaderEmptyDirectory";
    DirLoader dirLoader = new DirLoader(path);
    Exception exception = assertThrows(IllegalStateException.class, () -> {
      dirLoader.loadFiles(CLASS_FILE_EXT);
    });
    assertEquals("No such directory: " + path, exception.getMessage());
  }
}
