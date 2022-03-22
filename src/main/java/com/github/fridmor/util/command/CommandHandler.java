package com.github.fridmor.util.command;

import com.github.fridmor.enumeration.CdxEnum;
import com.github.fridmor.enumeration.CmdOptionEnum;
import lombok.AccessLevel;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class CommandHandler {
    @Getter(AccessLevel.NONE)
    private static final Pattern CDX_PATTERN = Pattern.compile("\\s+[A-Z]+(,[A-Z]+)*");
    @Getter(AccessLevel.NONE)
    private static final Pattern OPTION_PATTERN = Pattern.compile("\\s+(-[a-z]+)\\s+([a-z]+|\\d{2}\\.\\d{2}\\.\\d{4})");
    @Getter(AccessLevel.NONE)
    private static Matcher matcher;

    private String cmd;
    private final String mainCmd;
    private final String[] cdxValues;
    private final String[] dateOption;
    private final String[] periodOption;
    private final String[] algorithmOption;
    private final String[] outputOption;

    public CommandHandler(String inputCmd) {
        cmd = inputCmd;
        mainCmd = setMainCmd(inputCmd);
        cdxValues = setCdxValues(inputCmd);
        Map<String, String> cmdOptionMap = getCmdOptions(inputCmd);

        dateOption = setDateOption(cmdOptionMap);
        periodOption = setPeriodOption(cmdOptionMap);
        algorithmOption = setAlgOption(cmdOptionMap);
        outputOption = setOutputOption(cmdOptionMap);

        if (!this.cmd.isBlank()) {
            throw new IllegalArgumentException(String.format(
                    "%s arguments not used in the calculation, please correct the command or write a new one", cmd));
        }
    }

    private String setMainCmd(String cmd) {
        if (cmd.startsWith(CmdOptionEnum.MAIN.getArg())) {
            this.cmd = this.cmd.replaceFirst(CmdOptionEnum.MAIN.getArg(), "");
            return CmdOptionEnum.MAIN.getArg();
        } else {
            throw new IllegalArgumentException(String.format(
                    "command must start with %s", CmdOptionEnum.MAIN.getArg()));
        }
    }

    private String[] setCdxValues(String cmd) {
        matcher = CDX_PATTERN.matcher(cmd);
        if (matcher.find()) {
            String[] cdxValues = matcher.group().trim().split(",");
            this.cmd = this.cmd.replaceFirst(matcher.group().trim(), "");
            Set<String> cdxSet = new HashSet<>();
            for (String cdx : cdxValues) {
                if (Arrays.stream(CdxEnum.values()).noneMatch(e -> e.name().equals(cdx))) {
                    throw new IllegalArgumentException(String.format(
                            "invalid currency %s. available currencies: AMD,BGN,EUR,TRY,USD", cdx));
                } else if (!cdxSet.add(cdx)) {
                    throw new IllegalArgumentException(String.format("can't use same currency twice -> %s", cdx));
                }
            }
            return cdxValues;
        } else {
            throw new IllegalArgumentException(
                    "can't find cdx pattern. make sure currencies are listed separated by ',' without spaces");
        }
    }

    private Map<String, String> getCmdOptions(String cmd) {
        matcher = OPTION_PATTERN.matcher(cmd);
        Map<String, String> cmdOptionMap = new HashMap<>();
        while (matcher.find()) {
            if (Arrays.stream(CmdOptionEnum.values()).anyMatch(e -> e.getArg().equals(matcher.group(1)))) {
                if (!cmdOptionMap.containsKey(matcher.group(1))) {
                    cmdOptionMap.put(matcher.group(1), matcher.group(2));
                    this.cmd = this.cmd.replaceFirst(matcher.group().trim(), "");
                } else {
                    throw new IllegalArgumentException(String.format(
                            "can't use same argument twice -> %s", matcher.group(1)));
                }
            } else {
                throw new IllegalArgumentException(String.format(
                        "invalid %s argument. available arguments: -date, -period, -alg, -output", matcher.group(1)));
            }
        }
        if (cmdOptionMap.isEmpty()) {
            throw new IllegalArgumentException(
                    "can't find option pattern. " +
                            "make sure the option argument starts with '-' " +
                            "and the option value is specified");
        } else {
            return cmdOptionMap;
        }
    }

    private String[] setDateOption(Map<String, String> cmdOptionMap) {
        if (cmdOptionMap.containsKey(CmdOptionEnum.DATE.getArg())) {
            if (cdxValues.length > 1) {
                throw new IllegalArgumentException(String.format(
                        "can't use %s option with multiple currencies", CmdOptionEnum.DATE.name()));
            }
            if (cmdOptionMap.containsKey(CmdOptionEnum.PERIOD.getArg())) {
                throw new IllegalArgumentException(String.format(
                        "can't use %s option with %s option",
                        CmdOptionEnum.DATE.name(), CmdOptionEnum.PERIOD.name()));
            }
            if (cmdOptionMap.containsKey(CmdOptionEnum.OUTPUT.getArg())) {
                throw new IllegalArgumentException(String.format(
                        "can't use %s option with %s option",
                        CmdOptionEnum.DATE.name(), CmdOptionEnum.OUTPUT.name()));
            }
            String dateValue = cmdOptionMap.get(CmdOptionEnum.DATE.getArg());
            if (Arrays.asList(CmdOptionEnum.DATE.getValues()).contains(dateValue)) {
                return new String[]{CmdOptionEnum.DATE.getArg(), dateValue};
            } else {
                try {
                    LocalDate.parse(dateValue, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    return new String[]{CmdOptionEnum.DATE.getArg(), dateValue};
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException(String.format(
                            "invalid date option value -> %s", dateValue));
                }
            }
        } else {
            return null;
        }
    }

    private String[] setPeriodOption(Map<String, String> cmdOptionMap) {
        if (cmdOptionMap.containsKey(CmdOptionEnum.PERIOD.getArg())) {
            if (cmdOptionMap.containsKey(CmdOptionEnum.DATE.getArg())) {
                throw new IllegalArgumentException(String.format(
                        "can't use %s option with %s option",
                        CmdOptionEnum.PERIOD.name(), CmdOptionEnum.DATE.name()));
            }
            if (!cmdOptionMap.containsKey(CmdOptionEnum.OUTPUT.getArg())) {
                throw new IllegalArgumentException(String.format(
                        "can't use %s option without %s option",
                        CmdOptionEnum.PERIOD.name(), CmdOptionEnum.OUTPUT.name()));
            }
            String periodValue = cmdOptionMap.get(CmdOptionEnum.PERIOD.getArg());
            if (Arrays.asList(CmdOptionEnum.PERIOD.getValues()).contains(periodValue)) {
                return new String[]{CmdOptionEnum.PERIOD.getArg(), periodValue};
            } else {
                throw new IllegalArgumentException(String.format(
                        "invalid period option value -> %s", periodValue));
            }
        } else {
            return null;
        }
    }

    private String[] setAlgOption(Map<String, String> cmdOptionMap) {
        if (cmdOptionMap.containsKey(CmdOptionEnum.ALGORITHM.getArg())) {
            String algValue = cmdOptionMap.get(CmdOptionEnum.ALGORITHM.getArg());
            if (Arrays.asList(CmdOptionEnum.ALGORITHM.getValues()).contains(algValue)) {
                return new String[]{CmdOptionEnum.ALGORITHM.getArg(),
                        cmdOptionMap.get(CmdOptionEnum.ALGORITHM.getArg())};
            } else {
                throw new IllegalArgumentException(String.format(
                        "invalid algorithm option value -> %s", algValue));
            }
        } else {
            return null;
        }
    }

    private String[] setOutputOption(Map<String, String> cmdOptionMap) {
        if (cmdOptionMap.containsKey(CmdOptionEnum.OUTPUT.getArg())) {
            if (cmdOptionMap.containsKey(CmdOptionEnum.DATE.getArg())) {
                throw new IllegalArgumentException(String.format(
                        "can't use %s option with %s option",
                        CmdOptionEnum.OUTPUT.name(), CmdOptionEnum.DATE.name()));
            }
            if (!cmdOptionMap.containsKey(CmdOptionEnum.PERIOD.getArg())) {
                throw new IllegalArgumentException(String.format(
                        "can't use %s option without %s option",
                        CmdOptionEnum.OUTPUT.name(), CmdOptionEnum.PERIOD.name()));
            }
            String outputValue = cmdOptionMap.get(CmdOptionEnum.OUTPUT.getArg());
            if (Arrays.asList(CmdOptionEnum.OUTPUT.getValues()).contains(outputValue)) {
                if (outputValue.equals("list") && cdxValues.length > 1) {
                    throw new IllegalArgumentException(String.format(
                            "can't use %s option %s value with multiple currencies",
                            CmdOptionEnum.OUTPUT.name(), outputValue));
                } else {
                    return new String[]{CmdOptionEnum.OUTPUT.getArg(), outputValue};
                }
            } else {
                throw new IllegalArgumentException(String.format(
                        "invalid output option value -> %s", outputValue));
            }
        } else {
            return null;
        }
    }
}
