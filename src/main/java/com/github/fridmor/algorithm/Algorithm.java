package com.github.fridmor.algorithm;

import com.github.fridmor.enumeration.PeriodEnum;
import com.github.fridmor.model.Rate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public interface Algorithm {
    Rate calculateRateForDate(List<Rate> rateList, LocalDate date);

    default List<Rate> calculateRateListForPeriod(List<Rate> rateList, PeriodEnum period) {
        List<Rate> rateListForPeriod = new ArrayList<>();
        for (int i = 0; i < period.getDays(); i++) {
            rateListForPeriod.add(calculateRateForDate(rateList, LocalDate.now().plusDays(i + 1)));
        }
        return rateListForPeriod;
    }

    default Rate getLastRate(List<Rate> rateList) {
        return rateList.stream()
                .max(Comparator.comparing(Rate::getDate))
                .orElseThrow();
    }
}
