package datasource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class FullFilePrinter implements DataPrinter{

    private final String path;
    public FullFilePrinter(String path) {
        this.path = path;
    }
    @Override
    public void print(String s) throws FileNotFoundException {
        File f = new File(path);
        PrintStream printer = new PrintStream(f);
        printer.print(s);
        printer.close();
    }
    
}
