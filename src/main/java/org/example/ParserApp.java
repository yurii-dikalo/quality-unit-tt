package org.example;

import java.io.IOException;
import java.util.List;
import org.example.service.Parser;
import org.example.service.Reader;
import org.example.service.Writer;

public class ParserApp {
    public static void main(String[] args) throws IOException {
        List<String> lines = new Reader().readData("input.txt");
        List<String> report = new Parser().getReport(lines);
        new Writer().writeReport("output.txt", report);
    }
}
