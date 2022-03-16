package com.github.fridmor.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class Rate {

    private final int nominal;
    private final LocalDate date;
    private final BigDecimal curs;
    private final String cdx;

    public Rate(int nominal, LocalDate date, BigDecimal curs, String cdx) {
        this.nominal = nominal;
        this.date = date;
        this.curs = curs;
        this.cdx = cdx;
    }

    @Override
    public String toString() {
        return date.format(DateTimeFormatter.ofPattern("E dd.MM.yyyy")) + " - " + String.format("%.2f", curs);
    }
}
