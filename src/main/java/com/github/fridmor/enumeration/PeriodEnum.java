package com.github.fridmor.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PeriodEnum {
    WEEK(7),
    MONTH(30);

    private final int days;
}
