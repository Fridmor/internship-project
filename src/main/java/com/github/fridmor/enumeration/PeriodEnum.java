package com.github.fridmor.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PeriodEnum {
    WEEK(7),
    MONTH(30);

    private final int days;
}
