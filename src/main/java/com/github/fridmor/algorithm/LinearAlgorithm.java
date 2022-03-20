package com.github.fridmor.algorithm;

import com.github.fridmor.enumeration.PeriodEnum;
import com.github.fridmor.model.Rate;
import com.github.fridmor.util.LinearRegression;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@NoArgsConstructor
public class LinearAlgorithm extends Algorithm {
    private static final int DAYS_AMOUNT_TO_ITERATE = 30;

    @Override
    public Rate calculateRateForDate(List<Rate> rateList, LocalDate date) {
        List<Rate> rateListLastMonth = getRateListForLastMonth(rateList);
        rateListLastMonth.sort(Comparator.comparing(Rate::getDate));
        double[] dateArray = new double[rateListLastMonth.size()];
        double[] cursArray = new double[rateListLastMonth.size()];
        for (int i = 0; i < rateListLastMonth.size(); i++) {
            dateArray[i] = i + 1;
            cursArray[i] = rateListLastMonth.get(i).getCurs().doubleValue();
        }
        Rate lastRate = getLastRate(rateList);
        int i = 1;
        while (lastRate.getDate().isBefore(date)) {
            LinearRegression lr = new LinearRegression(dateArray, cursArray);
            double lrPrediction = lr.predict(dateArray[rateListLastMonth.size() - 1] + i);
            BigDecimal curs = BigDecimal.valueOf(lrPrediction);
            lastRate = new Rate(lastRate.getNominal(), lastRate.getDate().plusDays(i), curs, lastRate.getCdx());
            rateList.add(lastRate);
            i++;
        }
        return lastRate;
    }

    @Override
    public List<Rate> calculateRateListForPeriod(List<Rate> rateList, PeriodEnum period) {
        List<Rate> rateListForPeriod = new ArrayList<>();
        for (int i = 0; i < period.getDays(); i++) {
            rateListForPeriod.add(calculateRateForDate(rateList, LocalDate.now().plusDays(i + 1)));
        }
        return rateListForPeriod;
    }

    private List<Rate> getRateListForLastMonth(List<Rate> rateList) {
        List<Rate> rateListForLastMonth = new ArrayList<>();
        Rate lastRate = getLastRate(rateList);
        LocalDate lastDate = lastRate.getDate();
        for (int i = 0; i < DAYS_AMOUNT_TO_ITERATE; i++) {
            LocalDate previousDate = lastDate.minusDays(i);
            if (rateList.stream()
                    .anyMatch(r -> r.getDate().isEqual(previousDate))) {
                rateListForLastMonth.add(rateList.stream()
                        .filter(r -> r.getDate().isEqual(previousDate))
                        .max(Comparator.comparing(Rate::getDate))
                        .orElseThrow());

            } else {
                BigDecimal curs = (rateList.stream()
                        .filter(r -> r.getDate().isBefore(previousDate))
                        .max(Comparator.comparing(Rate::getDate))
                        .orElseThrow()
                        .getCurs());
                rateListForLastMonth.add(new Rate(lastRate.getNominal(), previousDate, curs, lastRate.getCdx()));
            }
        }
        return rateListForLastMonth;
    }
}
