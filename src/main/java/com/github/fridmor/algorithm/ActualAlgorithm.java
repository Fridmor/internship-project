package com.github.fridmor.algorithm;

import com.github.fridmor.model.Rate;
import com.github.fridmor.enumeration.PeriodEnum;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@NoArgsConstructor
public class ActualAlgorithm extends Algorithm {

    private List<Rate> Data;

    @Override
    public Rate calculateRateForDate(List<Rate> rateList, LocalDate date) {
        Rate lastRate = rateList.get(FIRST_ELEMENT);

        if (date.minusYears(2).isAfter(lastRate.getDate())) {
            throw new IllegalArgumentException("not enough data");
        }

        Rate rateTwoYearsBefore = rateList.stream()
                .filter(r -> !r.getDate().isAfter(date.minusYears(2)))
                .findFirst()
                .orElseThrow();

        Rate rateThreeYearsBefore = rateList.stream()
                .filter(r -> !r.getDate().isAfter(date.minusYears(3)))
                .findFirst()
                .orElseThrow();

        BigDecimal newCurs = rateTwoYearsBefore.getCurs().add(rateThreeYearsBefore.getCurs());

        return new Rate(lastRate.getNominal(), date,  newCurs, lastRate.getCdx());
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
