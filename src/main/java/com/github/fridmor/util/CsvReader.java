package com.github.fridmor.util;

import com.github.fridmor.model.Rate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CsvReader {
    private static final String NOMINAL_HEADLINE = "nominal";
    private static final String DATA_HEADLINE = "data";
    private static final String CURS_HEADLINE = "curs";
    private static final String CDX_HEADLINE = "cdx";

    private static final String SEMICOLON_DELIMITER = ";";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static List<Rate> readFile(String fileName) throws FileNotFoundException {
        List<Rate> rateList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader((Objects.requireNonNull(
                        CsvReader.class.getClassLoader().getResourceAsStream(fileName)))))) {
            String line;
            String[] headlines = br.readLine().split(SEMICOLON_DELIMITER);
            while ((line = br.readLine()) != null) {
                String[] values = line.split(SEMICOLON_DELIMITER);
                rateList.add(parseValues(headlines, values));
            }
        } catch (IOException e) {
            throw new FileNotFoundException(String.format("file named %s in resources folder not found", fileName));
        }
        return rateList;
    }

    private static Rate parseValues(String[] headlines, String[] values) {
        if (!csvFileDataIsValid(headlines)) {
            throw new IllegalArgumentException("csv file data is not enough");
        }
        int nominalHeadlineIdx = 0;
        int dataHeadlineIdx = 0;
        int cursHeadlineIdx = 0;
        int cdxHeadlineIdx = 0;
        for (int i = 0; i < headlines.length; i++) {
            if (headlines[i].equals(NOMINAL_HEADLINE)) {
                nominalHeadlineIdx = i;
            }if (headlines[i].equals(DATA_HEADLINE)) {
                dataHeadlineIdx = i;
            }if (headlines[i].equals(CURS_HEADLINE)) {
                cursHeadlineIdx = i;
            }if (headlines[i].equals(CDX_HEADLINE)) {
                cdxHeadlineIdx = i;
            }
        }
        int nominal = Integer.parseInt(values[nominalHeadlineIdx]
                .replace(".", ""));
        LocalDate date = LocalDate.parse(values[dataHeadlineIdx], DATE_FORMAT);
        BigDecimal curs = new BigDecimal(values[cursHeadlineIdx]
                .replace(",", ".")
                .replace("\"", ""));
        String cdx = values[cdxHeadlineIdx];
        return new Rate(nominal, date, curs, cdx);
    }

    private static boolean csvFileDataIsValid(String[] headlines) {
        boolean nominalHeadlineExist = false;
        boolean dataHeadlineExist = false;
        boolean cursHeadlineExist = false;
        boolean cdxHeadlineExist = false;
        for (String headline : headlines) {
            if (headline.equals(NOMINAL_HEADLINE)) {
                nominalHeadlineExist = true;
            }
            if (headline.equals(DATA_HEADLINE)) {
                dataHeadlineExist = true;
            }
            if (headline.equals(CURS_HEADLINE)) {
                cursHeadlineExist = true;
            }
            if (headline.equals(CDX_HEADLINE)) {
                cdxHeadlineExist = true;
            }
        }
        return nominalHeadlineExist && dataHeadlineExist && cursHeadlineExist && cdxHeadlineExist;
    }
}
