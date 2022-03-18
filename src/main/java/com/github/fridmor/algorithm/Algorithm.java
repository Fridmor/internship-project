package com.github.fridmor.algorithm;

import com.github.fridmor.enumeration.PeriodEnum;
import com.github.fridmor.model.Rate;

import java.time.LocalDate;
import java.util.List;

public abstract class Algorithm {
    protected final int FIRST_ELEMENT = 0;

    public abstract Rate calculateRateForDate(List<Rate> rateList, LocalDate date);

    public abstract List<Rate> calculateRateListForPeriod(List<Rate> rateList, PeriodEnum period);
}
