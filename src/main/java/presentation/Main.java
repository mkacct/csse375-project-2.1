package presentation;

import java.io.IOException;
import java.io.PrintStream;

import domain.PlantUMLGenerator;

public class Main {
    public static void main(String[] args) throws IOException {
        PlantUMLGenerator a = new PlantUMLGenerator();
        PrintStream b = new PrintStream("test.svg");
        b.print(a.test());
    }
}
