package com.github.fridmor.enumeration;

import com.github.fridmor.algorithm.ActualAlgorithm;
import com.github.fridmor.algorithm.Algorithm;
import com.github.fridmor.algorithm.LinearAlgorithm;
import com.github.fridmor.algorithm.MoonAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AlgorithmEnum {
    ACTUAL(new ActualAlgorithm()),
    MOON(new MoonAlgorithm()),
    LINEAR(new LinearAlgorithm());

    private final Algorithm algorithm;
}
