package com.github.fridmor;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileHandler {

    public static List<List<String>> readFile(File file, int dataRequired) {
        List<List<String>> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null && (dataRequired <= 0 || data.size() < dataRequired)) {
                if (DataValidator.inputDataIsValid(line)) {
                    String[] values = line.split(";");
                    data.add(Arrays.asList(values));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void writeFile(File file, List<List<String>> oldData, List<String> newData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("data;curs;cdx\n");
            if (newData != null) {
                writer.write(lineFormatter(newData) + "\n");
            }
            for (List<String> oldLine : oldData) {
                writer.write(lineFormatter(oldLine) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String lineFormatter(List<String> line) {
        return line.get(0) + ";" + line.get(1).replace('.', ',') + ";" + line.get(2);
    }
}
