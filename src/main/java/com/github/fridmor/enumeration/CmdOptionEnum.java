package com.github.fridmor.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CmdOptionEnum {
    MAIN("rate", null),
    DATE("-date", new String[]{"tomorrow"}),
    PERIOD("-period", new String[]{"week", "month"}),
    ALGORITHM("-alg", new String[]{"actual", "moon", "linear"}),
    OUTPUT("-output", new String[]{"list", "graph"});

    private final String arg;
    private final String[] values;
}
