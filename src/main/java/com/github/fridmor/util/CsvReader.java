package com.github.fridmor.util;

import com.github.fridmor.model.Rate;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
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
            List<String> headlinesList = List.of(br.readLine().split(SEMICOLON_DELIMITER));
            while ((line = br.readLine()) != null) {
                String[] values = line.split(SEMICOLON_DELIMITER);
                rateList.add(parseValues(headlinesList, values));
            }
        } catch (IOException e) {
            throw new FileNotFoundException(String.format("file named %s in resources folder not found", fileName));
        }
        return rateList;
    }

    private static Rate parseValues(List<String> headlinesList, String[] values) {
        if (!csvFileDataIsValid(headlinesList)) {
            throw new IllegalArgumentException("csv file data is not enough");
        }
        int nominal = Integer.parseInt(values[headlinesList.indexOf(NOMINAL_HEADLINE)]
                .replace(".", ""));
        LocalDate date = LocalDate.parse(values[headlinesList.indexOf(DATA_HEADLINE)], DATE_FORMAT);
        BigDecimal curs = new BigDecimal(values[headlinesList.indexOf(CURS_HEADLINE)]
                .replace(",", ".")
                .replace("\"", ""));
        String cdx = values[headlinesList.indexOf(CDX_HEADLINE)];
        return new Rate(nominal, date, curs, cdx);
    }

    private static boolean csvFileDataIsValid(List<String> headlinesList) {
        return headlinesList.contains(NOMINAL_HEADLINE) && headlinesList.contains(DATA_HEADLINE) &&
                headlinesList.contains(CURS_HEADLINE) && headlinesList.contains(CDX_HEADLINE);
    }
}
