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
        Rate lastRate = rateList.get(FIRST_ELEMENT);
        int i = 1;
        while (rateList.stream()
                .allMatch(r -> r.getDate().isBefore(date))) {
            LinearRegression lr = new LinearRegression(dateArray, cursArray);
            double lrPrediction = lr.predict(dateArray[rateListLastMonth.size() - 1] + i);
            BigDecimal curs = BigDecimal.valueOf(lrPrediction);
            rateList.add(new Rate(lastRate.getNominal(), lastRate.getDate().plusDays(i), curs, lastRate.getCdx()));
            i++;
        }
        return rateList.stream()
                .filter(r -> r.getDate().isEqual(date))
                .findFirst()
                .orElseThrow();
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
        Rate lastRate = rateList.get(FIRST_ELEMENT);
        LocalDate lastDate = lastRate.getDate();
        int i = 0;
        while (i < DAYS_AMOUNT_TO_ITERATE) {
            LocalDate nextDate = lastDate.minusDays(i);
            if (rateList.stream()
                    .anyMatch(r -> r.getDate().isEqual(nextDate))) {
                rateListForLastMonth.add(rateList.stream()
                        .filter(r -> r.getDate().isEqual(nextDate))
                        .findFirst()
                        .orElseThrow());

            } else {
                BigDecimal curs = (rateList.stream()
                        .filter(r -> r.getDate().isBefore(nextDate))
                        .findFirst()
                        .orElseThrow()
                        .getCurs());
                rateListForLastMonth.add(new Rate(lastRate.getNominal(), nextDate, curs, lastRate.getCdx()));
            }
            i++;
        }
        return rateListForLastMonth;
    }
}
