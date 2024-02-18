package datasource;

import java.io.FileNotFoundException;

public interface DataPrinter {
    void print(String s) throws FileNotFoundException;
}
