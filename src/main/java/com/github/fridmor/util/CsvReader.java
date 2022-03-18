package com.github.fridmor.util;

import com.github.fridmor.model.Rate;
import com.github.fridmor.telegram.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvReader {
    private static final Logger LOG = LoggerFactory.getLogger(Bot.class);
    private static final String LOG_TAG = "CsvReader";

    private static final String NOMINAL_HEADLINE = "nominal";
    private static final String DATA_HEADLINE = "data";
    private static final String CURS_HEADLINE = "curs";
    private static final String CDX_HEADLINE = "cdx";

    private static final String DELIMITER_REGEX = ";\"|;|\";";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static List<Rate> readFile(String fileName) {
        List<Rate> rateList = new ArrayList<>();
        try (InputStream is = CsvReader.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            String[] headlines = br.readLine().split(DELIMITER_REGEX);
            while ((line = br.readLine()) != null) {
                String[] values = line.split(DELIMITER_REGEX);
                rateList.add(parseValues(headlines, values));
            }
        } catch (IOException | IllegalArgumentException e) {
            LOG.error(LOG_TAG, e);
        }
        return rateList;
    }

    private static Rate parseValues(String[] headlines, String[] values) {
        List<String> headlineList = Arrays.asList(headlines);
        if (!headlineList.contains(NOMINAL_HEADLINE) || !headlineList.contains(DATA_HEADLINE) ||
                !headlineList.contains(CURS_HEADLINE) || !headlineList.contains(CDX_HEADLINE)) {
            throw new IllegalArgumentException("csv file data is not enough");
        }
        int nominalHeadlineIdx = headlineList.indexOf(NOMINAL_HEADLINE);
        int dataHeadlineIdx = headlineList.indexOf(DATA_HEADLINE);
        int cursHeadlineIdx = headlineList.indexOf(CURS_HEADLINE);
        int cdxHeadlineIdx = headlineList.indexOf(CDX_HEADLINE);
        int nominal = Integer.parseInt(values[nominalHeadlineIdx].replaceAll("[.]", ""));
        LocalDate date = LocalDate.parse(values[dataHeadlineIdx], DATE_FORMAT);
        BigDecimal curs = new BigDecimal(values[cursHeadlineIdx].replace(',', '.'));
        String cdx = values[cdxHeadlineIdx];
        return new Rate(nominal, date, curs, cdx);
    }
}
