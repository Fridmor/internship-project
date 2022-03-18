package com.github.fridmor.algorithm;

import com.github.fridmor.model.Rate;
import com.github.fridmor.util.FullMoonCalendar;
import com.github.fridmor.enumeration.PeriodEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MoonAlgorithm extends Algorithm {

    public MoonAlgorithm() {
    }

    @Override
    public Rate calculateRateForDate(List<Rate> rateList, LocalDate date) {
        List<LocalDate> fullMoonDateList = FullMoonCalendar.getFullMoonDates(date.getYear());
        LocalDate ClosestFullMoonDate = fullMoonDateList.stream()
                .filter(d -> d.isBefore(date))
                .findFirst()
                .orElseThrow();
        while (rateList.stream()
                .allMatch(r -> r.getDate().isBefore(ClosestFullMoonDate))) {
            Rate lastRate = rateList.get(FIRST_ELEMENT);
            LocalDate nextFullMoonDate = fullMoonDateList.stream()
                    .sorted()
                    .filter(d -> d.isAfter(lastRate.getDate()))
                    .findFirst()
                    .orElseThrow();
            BigDecimal nextFullMoonCurs = getAvgCurs(rateList, fullMoonDateList, nextFullMoonDate);
            Rate nextRate = new Rate(lastRate.getNominal(), nextFullMoonDate, nextFullMoonCurs, lastRate.getCdx());
            rateList.add(nextRate);
            rateList.sort(Comparator.comparing(Rate::getDate).reversed());
        }
        Rate lastRate = rateList.get(FIRST_ELEMENT);
        BigDecimal curs = getAvgCurs(rateList, fullMoonDateList, date);
        return new Rate(lastRate.getNominal(), date, curs, lastRate.getCdx());
    }

    @Override
    public List<Rate> calculateRateListForPeriod(List<Rate> rateList, PeriodEnum period) {
        List<Rate> rateListForPeriod = new ArrayList<>();
        for (int i = 0; i < period.getDays(); i++) {
            LocalDate nextDate = LocalDate.now().plusDays(i + 1);
            if (i == 0) {
                rateListForPeriod.add(calculateRateForDate(rateList, nextDate));
                continue;
            }
            Rate lastRate = rateListForPeriod.get(rateListForPeriod.size() - 1);
            BigDecimal curs = getNextCurs(lastRate.getCurs());
            rateListForPeriod.add(new Rate(lastRate.getNominal(), nextDate, curs, lastRate.getCdx()));
        }
        return rateListForPeriod;
    }

    private BigDecimal getAvgCurs(List<Rate> rateList, List<LocalDate> fullMoonDateList, LocalDate date) {
        List<LocalDate> previousFullMoonDateList = fullMoonDateList.stream()
                .filter(d -> d.isBefore(date))
                .limit(3)
                .collect(Collectors.toList());
        List<BigDecimal> cursList = new ArrayList<>();
        for (LocalDate previousFullMoonDate : previousFullMoonDateList) {
            Rate rate = rateList.stream()
                    .filter(r -> !r.getDate().isAfter(previousFullMoonDate))
                    .findFirst()
                    .orElseThrow();
            cursList.add(rate.getCurs());
        }
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
