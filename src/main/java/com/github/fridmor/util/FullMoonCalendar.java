package com.github.fridmor.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FullMoonCalendar {

    private static final LocalDateTime START_POINT = LocalDateTime.parse("2005-01-25T13:32");
    private static final int DAYS_UNTIL_NEXT_MOON = 29;
    private static final int HOURS_UNTIL_NEXT_MOON = 12;
    private static final int MINUTES_UNTIL_NEXT_MOON = 44;

    public static List<LocalDate> getFullMoonDates(int year) {
        List<LocalDateTime> fullMoonDates = new ArrayList<>();
        fullMoonDates.add(START_POINT);
        while (true) {
            LocalDateTime lastDate = fullMoonDates.get(fullMoonDates.size() - 1);
            LocalDateTime nextDate = lastDate
                    .plusDays(DAYS_UNTIL_NEXT_MOON)
                    .plusHours(HOURS_UNTIL_NEXT_MOON)
                    .plusMinutes(MINUTES_UNTIL_NEXT_MOON);
            if (nextDate.getYear() > year) {
                return fullMoonDates.stream()
                        .map(LocalDateTime::toLocalDate)
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList());
            }
            fullMoonDates.add(nextDate);
        }
    }
}
