package com.github.fridmor.algorithm;

import com.github.fridmor.enumeration.PeriodEnum;
import com.github.fridmor.model.Rate;
import com.github.fridmor.util.FullMoonCalendar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MoonAlgorithm implements Algorithm {

    @Override
    public Rate calculateRateForDate(List<Rate> rateList, LocalDate date) {
        List<LocalDate> fullMoonDateList = FullMoonCalendar.getFullMoonDates(date.getYear());
        LocalDate ClosestFullMoonDate = fullMoonDateList.stream()
                .max(Comparator.comparing(LocalDate::toEpochDay))
                .orElseThrow();
        Rate lastRate = getLastRate(rateList);
        while (lastRate.getDate().isBefore(ClosestFullMoonDate)) {
            LocalDate lastRateDate = lastRate.getDate();
            LocalDate nextFullMoonDate = fullMoonDateList.stream()
                    .filter(d -> d.isAfter(lastRateDate))
                    .min(Comparator.comparing(LocalDate::toEpochDay))
                    .orElseThrow();
            BigDecimal nextFullMoonCurs = getAvgCurs(rateList, fullMoonDateList, nextFullMoonDate);
            lastRate = new Rate(lastRate.getNominal(), nextFullMoonDate, nextFullMoonCurs, lastRate.getCdx());
            rateList.add(lastRate);
        }
        BigDecimal curs = getAvgCurs(rateList, fullMoonDateList, date);
        return new Rate(lastRate.getNominal(), date, curs, lastRate.getCdx());
    }

    @Override
    public List<Rate> calculateRateListForPeriod(List<Rate> rateList, PeriodEnum period) {
        List<Rate> rateListForPeriod = new ArrayList<>();
        LocalDate nextDate = LocalDate.now().plusDays(1);
        rateListForPeriod.add(calculateRateForDate(rateList, nextDate));
        for (int i = 1; i < period.getDays(); i++) {
            nextDate = LocalDate.now().plusDays(i + 1);
            Rate lastRate = rateListForPeriod.get(rateListForPeriod.size() - 1);
            BigDecimal curs = getNextCurs(lastRate.getCurs());
            rateListForPeriod.add(new Rate(lastRate.getNominal(), nextDate, curs, lastRate.getCdx()));
        }
        return rateListForPeriod;
    }

    private BigDecimal getAvgCurs(List<Rate> rateList, List<LocalDate> fullMoonDateList, LocalDate date) {
        List<BigDecimal> cursList = fullMoonDateList.stream()
                .filter(d -> d.isBefore(date))
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .map(d -> rateList.stream()
                        .filter(r -> !r.getDate().isAfter(d))
                        .max(Comparator.comparing(Rate::getDate))
                        .orElseThrow()
                        .getCurs())
                .collect(Collectors.toList());

        BigDecimal divisor = new BigDecimal(cursList.size());
        return cursList.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(divisor, RoundingMode.HALF_UP);
    }


    private BigDecimal getNextCurs(BigDecimal curs) {
        Random random = new Random();
        int min = -10;
        int max = 10;
        int diff = max - min;
        int randValue = min + random.nextInt(diff + 1);

        double percentage = randValue / 100d;
        return BigDecimal.valueOf((1 + percentage) * curs.doubleValue());
    }
}
