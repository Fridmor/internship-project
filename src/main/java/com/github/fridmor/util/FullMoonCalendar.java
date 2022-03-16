package com.github.fridmor.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FullMoonCalendar {

    private static final LocalDateTime START_POINT = LocalDateTime.of(2005, 1, 25, 13, 32);

    public static List<LocalDate> getFullMoonDates(int year) {
        List<LocalDateTime> fullMoonDates = new ArrayList<>();
        fullMoonDates.add(START_POINT);
        while (true) {
            LocalDateTime lastDate = fullMoonDates.get(fullMoonDates.size() - 1);
            LocalDateTime nextDate = lastDate.plusDays(29).plusHours(12).plusMinutes(44);
            if (nextDate.getYear() > year) {
                return fullMoonDates.stream().
                        map(date -> LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth()))
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList());
            }
            fullMoonDates.add(lastDate.plusDays(29).plusHours(12).plusMinutes(44));
        }
    }
}
