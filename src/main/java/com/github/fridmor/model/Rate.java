package com.github.fridmor.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Getter
public class Rate {
    @Getter(AccessLevel.NONE)
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("E dd.MM.yyyy");

    private final int nominal;
    private final LocalDate date;
    private final BigDecimal curs;
    private final String cdx;

    @Override
    public String toString() {
        return date.format(DATE_FORMAT) + " - " + String.format("%.2f", curs);
    }
}
