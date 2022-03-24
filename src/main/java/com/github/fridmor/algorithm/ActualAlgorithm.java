package com.github.fridmor.algorithm;

import com.github.fridmor.model.Rate;
import com.github.fridmor.util.CsvReader;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActualAlgorithm implements Algorithm {

    @Override
    public Rate calculateRateForDate(List<Rate> rateList, LocalDate date) {
        Rate lastRate = getLastRate(rateList);
        LocalDate dateTwoYearsBefore = date.minusYears(2);
        LocalDate dateThreeYearsBefore = date.minusYears(3);
        if (dateTwoYearsBefore.isAfter(lastRate.getDate())) {
            throw new IllegalArgumentException("the last rate entry is more than two years before the requested date");
        }

        Map<Year, Optional<Rate>> rateMap= rateList.stream()
                .collect(Collectors.groupingBy(r -> Year.from(r.getDate()),
                        Collectors.filtering(r -> r.getDate().getDayOfYear() <= date.getDayOfYear(),
                        Collectors.maxBy(Comparator.comparing(Rate::getDate)))));

        Rate rateTwoYearsBefore = rateMap.get(Year.from(dateTwoYearsBefore)).orElseThrow();
        Rate rateThreeYearsBefore = rateMap.get(Year.from(dateThreeYearsBefore)).orElseThrow();
        BigDecimal newCurs = rateTwoYearsBefore.getCurs().add(rateThreeYearsBefore.getCurs());
        return new Rate(lastRate.getNominal(), date, newCurs, lastRate.getCdx());
    }
}
