package org.example.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Reader {
    public List<String> readData(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int count = Integer.parseInt(reader.readLine());
            for (int i = 0; i < count; i++) {
                String line = reader.readLine();
                if (line != null) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }
}
