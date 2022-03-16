package com.github.fridmor.Selector;

public class FileSelector {

    public static String select(String cdx) {
        switch (cdx) {
            case "AMD":
                return "AMD_F01_02_2005_T05_03_2022.csv";
            case "BGN":
                return "BGN_F01_02_2005_T05_03_2022.csv";
            case "EUR":
                return "EUR_F01_02_2005_T05_03_2022.csv";
            case "TRY":
                return "TRY_F01_02_2005_T05_03_2022.csv";
            case "USD":
                return "USD_F01_02_2005_T05_03_2022.csv";
            default:
                return null;
        }
    }
}
