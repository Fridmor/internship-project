package com.github.fridmor.algorithm;

import com.github.fridmor.model.Rate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActualAlgorithm implements Algorithm {

    @Override
    public Rate calculateRateForDate(List<Rate> rateList, LocalDate date) {
        Rate lastRate = getLastRate(rateList);
        if (date.minusYears(2).isAfter(lastRate.getDate())) {
            throw new IllegalArgumentException("the last rate entry is more than two years before the requested date");
        }

        Map<Integer, Optional<Rate>> rateMap = rateList.stream()
                .collect(Collectors.groupingBy(r -> r.getDate().getYear(), Collectors.filtering(
                        r -> r.getDate().getMonthValue() <= date.getMonthValue() &&
                                r.getDate().getDayOfMonth() <= date.getDayOfMonth(),
                        Collectors.maxBy(Comparator.comparing(Rate::getDate)))));

        Rate rateTwoYearsBefore = rateMap.get(date.minusYears(2).getYear()).orElseThrow();
        Rate rateThreeYearsBefore = rateMap.get(date.minusYears(3).getYear()).orElseThrow();
        BigDecimal newCurs = rateTwoYearsBefore.getCurs().add(rateThreeYearsBefore.getCurs());
        return new Rate(lastRate.getNominal(), date, newCurs, lastRate.getCdx());
    }
}
