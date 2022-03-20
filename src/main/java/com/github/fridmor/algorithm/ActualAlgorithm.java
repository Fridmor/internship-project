package com.github.fridmor.algorithm;

import com.github.fridmor.enumeration.PeriodEnum;
import com.github.fridmor.model.Rate;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@NoArgsConstructor
public class ActualAlgorithm extends Algorithm {

    @Override
    public Rate calculateRateForDate(List<Rate> rateList, LocalDate date) {
        Rate lastRate = getLastRate(rateList);
        if (date.minusYears(2).isAfter(lastRate.getDate())) {
            throw new IllegalArgumentException("the last rate entry is more than two years before the requested date");
        }
        Rate rateTwoYearsBefore = rateList.stream()
                .filter(r -> !r.getDate().isAfter(date.minusYears(2)))
                .max(Comparator.comparing(Rate::getDate))
                .orElseThrow();
        Rate rateThreeYearsBefore = rateList.stream()
                .filter(r -> !r.getDate().isAfter(date.minusYears(3)))
                .max(Comparator.comparing(Rate::getDate))
                .orElseThrow();
        BigDecimal newCurs = rateTwoYearsBefore.getCurs().add(rateThreeYearsBefore.getCurs());
        return new Rate(lastRate.getNominal(), date, newCurs, lastRate.getCdx());
    }

    @Override
    public List<Rate> calculateRateListForPeriod(List<Rate> rateList, PeriodEnum period) {
        List<Rate> rateListForPeriod = new ArrayList<>();
        for (int i = 0; i < period.getDays(); i++) {
            rateListForPeriod.add(calculateRateForDate(rateList, LocalDate.now().plusDays(i + 1)));
        }
        return rateListForPeriod;
    }
}
