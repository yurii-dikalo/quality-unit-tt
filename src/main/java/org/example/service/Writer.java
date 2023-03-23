package org.example.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Writer {
    public void writeReport(String filepath, List<String> report) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            for (String line : report) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
