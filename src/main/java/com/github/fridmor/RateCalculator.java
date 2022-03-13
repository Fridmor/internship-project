package com.github.fridmor;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RateCalculator {
    private File dataFile;
    private File tempFile;

    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public RateCalculator(File dataFile, File tempFile) {
        this.dataFile = dataFile;
        this.tempFile = tempFile;
    }

    public static List<String> rateCalc(List<List<String>> data) {
        String lastDate = data.get(0).get(0);
        String nextDate = LocalDate.parse(lastDate, dateFormatter).plusDays(1).format(dateFormatter);

        double rate = 0;
        for (List<String> line : data) {
            String getRate = line.get(1).replace(',', '.');
            rate += Double.parseDouble(getRate);
        }
        rate /= 7;

        String cdx = data.get(0).get(2);

        List<String> output = new ArrayList<>();
        output.add(nextDate);
        output.add(String.format("%.4f", rate));
        output.add(cdx);

        return output;
    }

    public void updateTempFile() {
        List<List<String>> data = FileHandler.readFile(dataFile, 7);
        FileHandler.writeFile(tempFile, data, null);
        while (!dateWeekAfterExist(tempFile)) {
            List<String> newData = rateCalc(FileHandler.readFile(tempFile, 7));
            List<List<String>> oldData = FileHandler.readFile(tempFile, -1);
            FileHandler.writeFile(tempFile, oldData, newData);
        }
    }

    public static boolean dateWeekAfterExist(File file) {
        List<List<String>> data = FileHandler.readFile(file, 1);
        String dateWeekAfter = LocalDate.now().plusDays(7).format(dateFormatter);
        String lastDate = data.get(0).get(0);
        return lastDate.equals(dateWeekAfter);
    }
}
