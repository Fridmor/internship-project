package com.github.fridmor.enumeration;

import lombok.Getter;

public enum PeriodEnum {
    WEEK(7), MONTH(30);

    @Getter
    private final int days;

    private PeriodEnum(int days) {
        this.days = days;
    }
}
