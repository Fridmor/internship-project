package com.github.fridmor.algorithm;

import com.github.fridmor.model.Rate;
import com.github.fridmor.enumeration.PeriodEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class ActualAlgorithm implements Algorithm {

    private List<Rate> Data;

    public ActualAlgorithm() {
    }

    @Override
    public Rate calculateRateForDate(List<Rate> rateList, LocalDate date) {
        if (!dataIsEnough(rateList, date)) {
            throw new IllegalArgumentException("not enough data");
        }
        Rate lastRate = rateList.get(0);

        Rate rateTwoYearsBefore = rateList.stream()
                .filter(r -> r.getDate().isEqual(date.minusYears(2)) ||
                        r.getDate().isBefore(date.minusYears(2)))
                .findFirst().orElseThrow();

        Rate rateThreeYearsBefore = rateList.stream()
                .filter(r -> r.getDate().isEqual(date.minusYears(3)) ||
                        r.getDate().isBefore(date.minusYears(3)))
                .findFirst().orElseThrow();

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

    private boolean dataIsEnough(List<Rate> listRate, LocalDate date) {
        return !date.minusYears(2).isAfter(listRate.get(0).getDate());
    }
}
