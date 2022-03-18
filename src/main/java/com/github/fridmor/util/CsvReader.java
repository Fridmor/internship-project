package com.github.fridmor.util;

import com.github.fridmor.model.Rate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    private static final String DELIMITER_REGEX = ";\"|;|\";";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static List<Rate> readFile(String fileName) {
        List<Rate> rateList = new ArrayList<>();
        try (InputStream is = getFileAsStream(fileName);
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(DELIMITER_REGEX);
                rateList.add(parseValues(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rateList;
    }

    private static InputStream getFileAsStream(String fileName) {
        return CsvReader.class.getClassLoader().getResourceAsStream(fileName);
    }

    private static Rate parseValues(String[] values) {
        int nominal = Integer.parseInt(values[0].replaceAll("[.]", ""));
        LocalDate date = LocalDate.parse(values[1], DATE_FORMAT);
        BigDecimal curs = new BigDecimal(values[2].replace(',', '.'));
        String cdx = values[3];
        return new Rate(nominal, date, curs, cdx);
    }
}
