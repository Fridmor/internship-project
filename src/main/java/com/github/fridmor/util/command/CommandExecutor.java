package com.github.fridmor.util.command;

import com.github.fridmor.algorithm.Algorithm;
import com.github.fridmor.enumeration.AlgorithmEnum;
import com.github.fridmor.enumeration.CdxEnum;
import com.github.fridmor.enumeration.PeriodEnum;
import com.github.fridmor.model.Rate;
import com.github.fridmor.util.CsvReader;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandExecutor {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final String DATE_CMD = "-date";
    private static final String PERIOD_CMD = "-period";
    private static final String DATE_ARG = "tomorrow";
    private static final String OUTPUT_LIST = "list";
    private static final String OUTPUT_GRAPH = "graph";
    private static final String FILE_NAME = "graph.png";

    private final String[] cdxArg;
    private final String periodCmd;
    private final String periodArg;
    private final String algArg;
    private final String outputArg;

    public CommandExecutor(CommandHandler commandHandler) {
        cdxArg = commandHandler.getCdxValues();
        periodCmd = commandHandler.getPeriodArg();
        periodArg = commandHandler.getPeriodValue();
        algArg = commandHandler.getAlgValue();
        outputArg = commandHandler.getOutputValue();
    }

    public String commandExecuteWithTextReturn() throws FileNotFoundException {
        List<List<Rate>> rateListList = getRateList(cdxArg);
        Algorithm algorithm = getAlgorithm(algArg);

        if (periodCmd.equals(DATE_CMD)) {
            LocalDate date = getDate(periodArg);
            return outputList(rateListList, algorithm, date);
        } else if (periodCmd.equals(PERIOD_CMD) && outputArg.equals(OUTPUT_LIST)) {
            PeriodEnum period = getPeriod(periodArg);
            return outputList(rateListList, algorithm, period);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public File commandExecuteWithGraphReturn() throws PythonExecutionException, IOException {
        List<List<Rate>> rateListList = getRateList(cdxArg);
        Algorithm algorithm = getAlgorithm(algArg);

        if (periodCmd.equals(PERIOD_CMD) && outputArg.equals(OUTPUT_GRAPH)) {
            PeriodEnum period = getPeriod(periodArg);
            return outputGraph(rateListList, algorithm, period);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private List<List<Rate>> getRateList(String[] cdxArg) throws FileNotFoundException {
        List<List<Rate>> rateList = new ArrayList<>();
        for (String cdx : cdxArg) {
            String fileName = CdxEnum.valueOf(cdx.toUpperCase()).getFileName();
            rateList.add(CsvReader.readFile(fileName));
        }
        return rateList;
    }

    private Algorithm getAlgorithm(String algArg) {
        return AlgorithmEnum.valueOf(algArg.toUpperCase()).getAlgorithm();
    }

    private LocalDate getDate(String periodArg) {
        return periodArg.equals(DATE_ARG) ? LocalDate.now().plusDays(1) : LocalDate.parse(periodArg, DATE_FORMAT);
    }

    private PeriodEnum getPeriod(String periodArg) {
        return PeriodEnum.valueOf(periodArg.toUpperCase());
    }

    private String outputList(List<List<Rate>> rateListList, Algorithm algorithm, LocalDate date) {
        StringBuilder sb = new StringBuilder();
        for (List<Rate> rateList : rateListList) {
            Rate rate = algorithm.calculateRateForDate(rateList, date);
            sb.append(rate).append("\n");
        }
        return sb.toString();
    }

    private String outputList(List<List<Rate>> rateListList, Algorithm algorithm, PeriodEnum period) {
        StringBuilder sb = new StringBuilder();
        for (List<Rate> rateList : rateListList) {
            List<Rate> outputRateList = algorithm.calculateRateListForPeriod(rateList, period);
            for (Rate rate : outputRateList) {
                sb.append(rate).append("\n");
            }
        }
        return sb.toString();
    }

    private File outputGraph(List<List<Rate>> rateListList, Algorithm algorithm, PeriodEnum period) throws PythonExecutionException, IOException {
        Plot plt = Plot.create();
        List<Number> days = new ArrayList<>();
        for (int i = 0; i < period.getDays(); i++) {
            days.add(i + 1);
        }
        for (List<Rate> rateList : rateListList) {
            List<Rate> outputRateList = algorithm.calculateRateListForPeriod(rateList, period);
            List<Double> rates = outputRateList.stream()
                    .map(Rate::getCurs)
                    .map(BigDecimal::doubleValue)
                    .collect(Collectors.toList());
            plt.plot().add(days, rates);
        }
        plt.xlabel("Days");
        plt.ylabel("Rate");
        plt.title("Graph");
        plt.savefig(FILE_NAME).dpi(200);
        plt.executeSilently();
        return new File(FILE_NAME);
    }
}
