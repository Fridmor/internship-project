package com.github.fridmor.algorithm;

import com.github.fridmor.enumeration.PeriodEnum;
import com.github.fridmor.model.Rate;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public abstract class Algorithm {
    public abstract Rate calculateRateForDate(List<Rate> rateList, LocalDate date);

    public abstract List<Rate> calculateRateListForPeriod(List<Rate> rateList, PeriodEnum period);

    Rate getLastRate(List<Rate> rateList) {
        return rateList.stream().max(Comparator.comparing(Rate::getDate)).orElseThrow();
    }
}
