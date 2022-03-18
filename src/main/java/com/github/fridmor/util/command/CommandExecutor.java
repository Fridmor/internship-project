package com.github.fridmor.util.command;

import com.github.fridmor.algorithm.Algorithm;
import com.github.fridmor.enumeration.AlgorithmEnum;
import com.github.fridmor.enumeration.CdxEnum;
import com.github.fridmor.enumeration.PeriodEnum;
import com.github.fridmor.model.Rate;
import com.github.fridmor.util.CsvReader;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CommandExecutor {

    private final CommandHandler commandHandler;
    @Getter
    private String output;
    @Getter
    private File graph;

    public CommandExecutor(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    public void execute() throws PythonExecutionException, IOException {
        String[] cdxArg = commandHandler.getCdxArgs();
        String periodCmd = commandHandler.getPeriodCmd();
        String periodArg = commandHandler.getPeriodArg();
        String algArg = commandHandler.getAlgArg();
        String outputArg = commandHandler.getOutputArg();

        List<List<Rate>> rateListList = getRateList(cdxArg);
        Algorithm algorithm = getAlgorithm(algArg);

        if (periodCmd.equals("-date")) {
            LocalDate date = getDate(periodArg);
            outputList(rateListList, algorithm, date);
        }
        if (periodCmd.equals("-period")) {
            PeriodEnum period = getPeriod(periodArg);
            if (outputArg.equals("list")) {
                outputList(rateListList, algorithm, period);
            }
            if (outputArg.equals("graph")) {
                outputGraph(rateListList, algorithm, period);
            }
        }
    }

    private List<List<Rate>> getRateList(String[] cdxArg) {
        List<List<Rate>> rateList = new ArrayList<>();
        for (String cdx : cdxArg) {
            String fileName = CdxEnum.valueOf(cdx.toUpperCase(Locale.ROOT)).getFileName();
            rateList.add(CsvReader.readFile(fileName));
        }
        return rateList;
    }

    private LocalDate getDate(String periodArg) {
        return periodArg.equals("tomorrow") ?
                LocalDate.now().plusDays(1) : LocalDate.parse(periodArg, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private PeriodEnum getPeriod(String periodArg) {
        return PeriodEnum.valueOf(periodArg.toUpperCase(Locale.ROOT));
    }

    private Algorithm getAlgorithm(String algArg) {
        return AlgorithmEnum.valueOf(algArg.toUpperCase(Locale.ROOT)).getAlgorithm();
    }

    private void outputList(List<List<Rate>> rateListList, Algorithm algorithm, LocalDate date) {
        StringBuilder sb = new StringBuilder();
        for (List<Rate> rateList : rateListList) {
            Rate rate = algorithm.calculateRateForDate(rateList, date);
            sb.append(rate).append("\n");
        }
        output = sb.toString();
    }

    private void outputList(List<List<Rate>> rateListList, Algorithm algorithm, PeriodEnum period) {
        StringBuilder sb = new StringBuilder();
        for (List<Rate> rateList : rateListList) {
            List<Rate> outputRateList = algorithm.calculateRateListForPeriod(rateList, period);
            for (Rate rate : outputRateList) {
                sb.append(rate).append("\n");
            }
        }
        output = sb.toString();
    }

    private void outputGraph(List<List<Rate>> rateListList, Algorithm algorithm, PeriodEnum period) throws PythonExecutionException, IOException {
        Plot plt = Plot.create();
        for (List<Rate> rateList : rateListList) {
            List<Rate> outputRateList = algorithm.calculateRateListForPeriod(rateList, period);
            List<Double> rates = outputRateList.stream()
                    .map(Rate::getCurs)
                    .map(BigDecimal::doubleValue)
                    .collect(Collectors.toList());
            plt.plot().add(rates);
        }
        plt.savefig("graph.png");
        plt.executeSilently();
        graph = new File("graph.png");
    }
}
