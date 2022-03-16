package com.github.fridmor.Selector;

import com.github.fridmor.algorithm.ActualAlgorithm;
import com.github.fridmor.algorithm.Algorithm;
import com.github.fridmor.algorithm.LinearAlgorithm;
import com.github.fridmor.algorithm.MoonAlgorithm;

public class AlgorithmSelector {

    public static Algorithm select(String alg) {
        switch (alg) {
            case "actual":
                return new ActualAlgorithm();
            case "moon":
                return new MoonAlgorithm();
            case "linear":
                return new LinearAlgorithm();
            default:
                return null;
        }
    }
}
