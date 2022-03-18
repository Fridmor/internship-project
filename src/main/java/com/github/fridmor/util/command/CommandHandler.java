package com.github.fridmor.util.command;

import com.github.fridmor.enumeration.AlgorithmEnum;
import com.github.fridmor.enumeration.CdxEnum;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CommandHandler {
    private static final String PATTERN_WITH_DATE = "rate <cdx_arg> -date <date_arg> -alg <alg_arg>";
    private static final String PATTERN_WITH_PERIOD = "rate <cdx_arg> -period <period_arg> -alg <alg_arg> -output <output_arg>";
    private static final String EXAMPLE_WITH_DATE_TOMORROW = "rate USD -date tomorrow -alg actual";
    private static final String EXAMPLE_WITH_DATE_SPECIFIC_DATE = "rate TRY -date 22.02.2030 -alg moon";
    private static final String EXAMPLE_WITH_PERIOD_WEEK_LIST = "rate EUR -period week -alg linear -output list";
    private static final String EXAMPLE_WITH_PERIOD_MONTH_GRAPH = "rate BGN,AMD -period month -alg moon -output graph";

    private final String[] cmdArgs;
    private String coreCmd;
    @Getter
    private String[] cdxArgs;
    @Getter
    private String periodCmd;
    @Getter
    private String periodArg;
    private String algCmd;
    @Getter
    private String algArg;
    private String outputCmd;
    @Getter
    private String outputArg;

    public CommandHandler(String command) {
        cmdArgs = command.trim().split("\\s+");
        commandValidation();
    }

    private void commandValidation() {
        String errorDefault = "the command must match the pattern:";
        String errorDate = "" +
                "\tpattern using -date command: " + PATTERN_WITH_DATE + "\n" +
                "\t\texamples:\n" +
                "\t\t\t" + EXAMPLE_WITH_DATE_TOMORROW + "\n" +
                "\t\t\t" + EXAMPLE_WITH_DATE_SPECIFIC_DATE;
        String errorPeriod = "" +
                "\tpattern using -period command: " + PATTERN_WITH_PERIOD + "\n" +
                "\t\texamples:\n" +
                "\t\t\t" + EXAMPLE_WITH_PERIOD_WEEK_LIST + "\n" +
                "\t\t\t" + EXAMPLE_WITH_PERIOD_MONTH_GRAPH;

        if (cmdArgs.length != 6 && cmdArgs.length != 8) {
            throw new IllegalArgumentException(errorDefault + "\n" + errorDate + "\n" + errorPeriod);
        }

        coreCmd = cmdArgs[0];
        cdxArgs = cmdArgs[1].split(",");
        periodCmd = cmdArgs[2];
        periodArg = cmdArgs[3];
        algCmd = cmdArgs[4];
        algArg = cmdArgs[5];
        outputCmd = cmdArgs.length == 8 ? cmdArgs[6] : "";
        outputArg = cmdArgs.length == 8 ? cmdArgs[7] : "";


        StringBuilder sb = new StringBuilder();
        sb.append(coreCmdValidation());
        sb.append(cdxArgsValidation());
        sb.append(periodCmdValidation());
        sb.append(periodArgValidation());
        sb.append(algCmdValidation());
        sb.append(algArgValidation());
        sb.append(outputCmdValidation());
        sb.append(outputArgValidation());

        if (sb.toString().length() > 0) {
            throw new IllegalArgumentException(sb.toString());
        }
    }

    private String coreCmdValidation() {
        String error = "rate command error: must be rate\n";
        if (!coreCmd.equals("rate")) {
            return error;
        }
        return "";
    }

    private String cdxArgsValidation() {
        try {
            if (cmdArgs.length == 6 && !(cdxArgs.length == 1)) {
                return "cdx_arg error: for pattern using -date you can use only one currency\n";
            }
            Set<String> cdxSet = new HashSet<>();
            for (String cdx : cdxArgs) {
                CdxEnum.valueOf(cdx.toUpperCase(Locale.ROOT));
                if (!cdxSet.add(cdx)) {
                    return "cdx_arg error: currencies must be unique\n";
                }
            }
        } catch (IllegalArgumentException e) {
            return "cdx_arg error: wrong currency name\n" +
                    "\tavailable currencies: AMD,BGN,EUR,TRY,USD\n";
        }
        return "";
    }

    private String periodCmdValidation() {
        if (cmdArgs.length == 6 && !periodCmd.equals("-date")) {
            return "-date command error: wrong period command for pattern using -date\n";
        }
        if (cmdArgs.length == 8 && !periodCmd.equals("-period")) {
            return "-period command error: wrong period command for pattern using -period\n";
        }
        return "";
    }

    private String periodArgValidation() {
        if (cmdArgs.length == 6 && !periodArg.equals("tomorrow")) {
            if (periodArg.equals("week") || periodArg.equals("month")) {
                return "date_arg error: wrong date argument for pattern using -date\n" +
                        "\tavailable date argument: tomorrow, date in format dd.MM.yyyy\n";
            }
            try {
                LocalDate.parse(periodArg, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            } catch (DateTimeParseException e) {
                return "date_arg error: wrong date argument for pattern using -date\n" +
                        "\tavailable date argument: tomorrow, date in format dd.MM.yyyy\n";
            }
        }
        if (cmdArgs.length == 8 && !periodArg.equals("week") && !periodArg.equals("month")) {
            return "period_arg error: wrong period argument for pattern using -period\n" +
                    "\tavailable period argument: week, month\n";
        }
        return "";
    }

    private String algCmdValidation() {
        if (!algCmd.equals("-alg")) {
            return "-alg command error: must be -alg\n";
        }
        return "";
    }

    private String algArgValidation() {
        try {
            AlgorithmEnum.valueOf(algArg.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return "alg_arg error: wrong algorithm name\n" +
                    "\tavailable algorithms: actual, moon, linear\n";
        }
        return "";
    }

    private String outputCmdValidation() {
        if (cmdArgs.length == 8 && !outputCmd.equals("-output")) {
            return "-output command error: must be -output\n";
        }
        return "";
    }

    private String outputArgValidation() {
        if (cmdArgs.length == 8) {
            if (!outputArg.equals("list") && !outputArg.equals("graph")) {
                return "output_arg error: wrong output argument\n" +
                        "\tavailable output argument: list, graph\n";
            }
            if (outputArg.equals("list") && !(cdxArgs.length == 1)) {
                return "output_arg error: for output argument 'list' you can use only one currency\n";
            }
        }
        return "";
    }
}
