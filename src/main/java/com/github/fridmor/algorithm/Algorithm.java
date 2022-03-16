package com.github.fridmor.algorithm;

import com.github.fridmor.model.Rate;
import com.github.fridmor.enumeration.PeriodEnum;

import java.time.LocalDate;
import java.util.List;

public interface Algorithm {

    Rate calculateRateForDate(List<Rate> rateList, LocalDate date);

    List<Rate> calculateRateListForPeriod(List<Rate> rateList, PeriodEnum period);
}
