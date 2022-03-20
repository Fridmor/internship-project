package com.github.fridmor.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CdxEnum {
    AMD("AMD_F01_02_2005_T05_03_2022.csv"),
    BGN("BGN_F01_02_2005_T05_03_2022.csv"),
    EUR("EUR_F01_02_2005_T05_03_2022.csv"),
    TRY("TRY_F01_02_2005_T05_03_2022.csv"),
    USD("USD_F01_02_2005_T05_03_2022.csv");

    private final String fileName;
}
