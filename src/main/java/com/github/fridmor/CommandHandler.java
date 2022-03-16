package com.github.fridmor;

import com.github.fridmor.Selector.AlgorithmSelector;
import com.github.fridmor.Selector.FileSelector;
import com.github.fridmor.algorithm.Algorithm;
import com.github.fridmor.enumeration.PeriodEnum;
import com.github.fridmor.model.Rate;
import com.github.fridmor.util.CsvReader;
import com.github.sh0nk.matplotlib4j.Plot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CommandHandler {
    String[] command = sc.nextLine().trim().split("\\s+");

    String[] cdxArg = command[1].split(",");
    String cmdArg = command[2];
    String periodArg = command[3];
    String algArg = command[5];
    String outputArg = command.length > 6 ? command[7] : null;

    // get input data
    List<List<Rate>> rateListList = new ArrayList<>();
        for(
    String cdx :cdxArg)

    {
        String fileName = FileSelector.select(cdx);
        rateListList.add(CsvReader.readFile(fileName));
    }

    //select algorithm
    Algorithm algorithm = AlgorithmSelector.select(algArg);

    //convert period
    LocalDate date = null;
    PeriodEnum period = null;
        switch(periodArg)

    {
        case "tomorrow":
            date = LocalDate.parse("05.03.2022", DateTimeFormatter.ofPattern("dd.MM.yyyy")).plusDays(1);
            break;
        case "week":
        case "month":
            period = PeriodEnum.valueOf(periodArg.toUpperCase(Locale.ROOT));
            break;
        default:
            date = LocalDate.parse(periodArg, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    //output
        switch(cmdArg)

    {
        case "-date":
            for (List<Rate> rateList : rateListList) {
                Rate outputRate = algorithm.calculateRateForDate(rateList, date);
                System.out.println(outputRate);
            }
            break;
        case "-period":
            switch (outputArg) {
                case "list":
                    for (List<Rate> rateList : rateListList) {
                        List<Rate> outputRateList = algorithm.calculateRateListForPeriod(rateList, period);
                        for (Rate rate : outputRateList) {
                            System.out.println(rate);
                        }
                    }
                    break;
                case "graph":
                    Plot plt = Plot.create();
                    for (List<Rate> rateList : rateListList) {
                        List<Rate> outputRateList = algorithm.calculateRateListForPeriod(rateList, period);
                        List<Double> rates = outputRateList.stream()
                                .map(Rate::getCurs)
                                .map(BigDecimal::doubleValue)
                                .collect(Collectors.toList());
                        plt.plot().add(rates);
                    }
                    plt.show();
                    break;
            }
            break;
    }
}
