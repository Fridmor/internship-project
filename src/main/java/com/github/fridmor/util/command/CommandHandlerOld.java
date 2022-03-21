package com.github.fridmor.util.command;

import com.github.fridmor.enumeration.AlgorithmEnum;
import com.github.fridmor.enumeration.CdxEnum;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Deprecated
public class CommandHandlerOld {
    private static final String PATTERN_WITH_DATE = "rate <cdx_arg> -date <date_arg> -alg <alg_arg>";
    private static final String PATTERN_WITH_PERIOD = "rate <cdx_arg> -period <period_arg> -alg <alg_arg> -output <output_arg>";
    private static final String EXAMPLE_WITH_DATE_TOMORROW = "rate USD -date tomorrow -alg actual";
    private static final String EXAMPLE_WITH_DATE_SPECIFIC_DATE = "rate TRY -date 22.02.2030 -alg moon";
    private static final String EXAMPLE_WITH_PERIOD_WEEK_LIST = "rate EUR -period week -alg linear -output list";
    private static final String EXAMPLE_WITH_PERIOD_MONTH_GRAPH = "rate BGN,AMD -period month -alg moon -output graph";

    private static final int DEFAULT_CMD_SIZE = 6;
    private static final int EXTENDED_CMD_SIZE = 8;

    private static final int MAIN_CMD_IDX = 0;
    private static final int CDX_VALUES_IDX = 1;
    private static final int PERIOD_ARG_IDX = 2;
    private static final int PERIOD_VALUE_IDX = 3;
    private static final int ALGORITHM_ARG_IDX = 4;
    private static final int ALGORITHM_VALUE_IDX = 5;
    private static final int OUTPUT_ARG_IDX = 6;
    private static final int OUTPUT_VALUE_IDX = 7;

    private final String[] cmdArgs;
    private String mainCmd;
    @Getter private String[] cdxValues;
    @Getter private String periodArg;
    @Getter private String periodValue;
    private String algArg;
    @Getter private String algValue;
    private String outputArg;
    @Getter private String outputValue;

    public CommandHandlerOld(String command) {
        cmdArgs = command.trim().split("\\s+");
        getErrorIfCommandInvalid();
    }

    private void getErrorIfCommandInvalid() {
        String errorDefault = "the command must match the pattern:";
        String errorDate = "" +
                " pattern using -date command:\n" +
                "  " + PATTERN_WITH_DATE + "\n" +
                "   examples:\n" +
                "    " + EXAMPLE_WITH_DATE_TOMORROW + "\n" +
                "    " + EXAMPLE_WITH_DATE_SPECIFIC_DATE;
        String errorPeriod = "" +
                " pattern using -period command:\n" +
                "  " + PATTERN_WITH_PERIOD + "\n" +
                "   examples:\n" +
                "    " + EXAMPLE_WITH_PERIOD_WEEK_LIST + "\n" +
                "    " + EXAMPLE_WITH_PERIOD_MONTH_GRAPH;

        if (cmdArgs.length != DEFAULT_CMD_SIZE && cmdArgs.length != EXTENDED_CMD_SIZE) {
            throw new IllegalArgumentException(errorDefault + "\n" + errorDate + "\n" + errorPeriod);
        }

        mainCmd = cmdArgs[MAIN_CMD_IDX];
        cdxValues = cmdArgs[CDX_VALUES_IDX].split(",");
        periodArg = cmdArgs[PERIOD_ARG_IDX];
        periodValue = cmdArgs[PERIOD_VALUE_IDX];
        algArg = cmdArgs[ALGORITHM_ARG_IDX];
        algValue = cmdArgs[ALGORITHM_VALUE_IDX];
        outputArg = cmdArgs.length == EXTENDED_CMD_SIZE ? cmdArgs[OUTPUT_ARG_IDX] : "";
        outputValue = cmdArgs.length == EXTENDED_CMD_SIZE ? cmdArgs[OUTPUT_VALUE_IDX] : "";

        StringBuilder sb = new StringBuilder();
        sb.append(getErrorIfMainCmdInvalid());
        sb.append(getErrorIfCdxArgsInvalid());
        sb.append(getErrorIfPeriodCmdInvalid());
        sb.append(getErrorIfPeriodArgInvalid());
        sb.append(getErrorIfAlgCmdInvalid());
        sb.append(getErrorIfAlgArgInvalid());
        sb.append(getErrorIfOutputCmdInvalid());
        sb.append(getErrorIfOutputArgInvalid());

        if (!sb.toString().isEmpty()) {
            throw new IllegalArgumentException(sb.toString());
        }
    }

    private String getErrorIfMainCmdInvalid() {
        if (!mainCmd.equals("rate")) {
            return "rate command error: must be rate\n";
        }
        return "";
    }

    private String getErrorIfCdxArgsInvalid() {
        if (cmdArgs.length == DEFAULT_CMD_SIZE && !(cdxValues.length == 1)) {
            return "cdx_arg error: for pattern using -date you can use only one currency\n";
        }
        Set<String> cdxSet = new HashSet<>();
        for (String cdx : cdxValues) {
            if (Arrays.stream(CdxEnum.values()).noneMatch(e -> e.name().equals(cdx))) {
                return "cdx_arg error: wrong currency name\n" +
                        "\tavailable currencies: AMD,BGN,EUR,TRY,USD\n";
            } else if (!cdxSet.add(cdx)) {
                return "cdx_arg error: currencies must be unique\n";
            }
        }
        return "";
    }

    private String getErrorIfPeriodCmdInvalid() {
        if (cmdArgs.length == DEFAULT_CMD_SIZE && !periodArg.equals("-date")) {
            return "-date command error: wrong period command for pattern using -date\n";
        }
        if (cmdArgs.length == EXTENDED_CMD_SIZE && !periodArg.equals("-period")) {
            return "-period command error: wrong period command for pattern using -period\n";
        }
        return "";
    }

    private String getErrorIfPeriodArgInvalid() {
        if (cmdArgs.length == DEFAULT_CMD_SIZE && !periodValue.equals("tomorrow")) {
            try {
                LocalDate.parse(periodValue, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            } catch (DateTimeParseException e) {
                return "date_arg error: wrong date argument for pattern using -date\n" +
                        "\tavailable date argument: tomorrow, date in format dd.MM.yyyy\n";
            }
        }
        if (cmdArgs.length == EXTENDED_CMD_SIZE && !periodValue.matches("week|month")) {
            return "period_arg error: wrong period argument for pattern using -period\n" +
                    "\tavailable period argument: week, month\n";
        }
        return "";
    }

    private String getErrorIfAlgCmdInvalid() {
        if (!algArg.equals("-alg")) {
            return "-alg command error: must be -alg\n";
        }
        return "";
    }

    private String getErrorIfAlgArgInvalid() {
        if (Arrays.stream(AlgorithmEnum.values()).noneMatch(e -> e.name().equals(algValue.toUpperCase()))) {
            return "alg_arg error: wrong algorithm name\n" +
                    "\tavailable algorithms: actual, moon, linear\n";
        }
        return "";
    }

    private String getErrorIfOutputCmdInvalid() {
        if (cmdArgs.length == EXTENDED_CMD_SIZE && !outputArg.equals("-output")) {
            return "-output command error: must be -output\n";
        }
        return "";
    }

    private String getErrorIfOutputArgInvalid() {
        if (cmdArgs.length == EXTENDED_CMD_SIZE) {
            if (!outputValue.matches("list|graph")) {
                return "output_arg error: wrong output argument\n" +
                        "\tavailable output argument: list, graph\n";
            }
            if (outputValue.equals("list") && cdxValues.length > 1) {
                return "output_arg error: for output argument 'list' you can use only one currency\n";
            }
        }
        return "";
    }
}
