package com.github.fridmor;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    private static final File EUR_FILE = new File("src/main/resources/EUR_F01_02_2002_T01_02_2022.csv");
    private static final File TRY_FILE = new File("src/main/resources/TRY_F01_02_2002_T01_02_2022.csv");
    private static final File USD_FILE = new File("src/main/resources/USD_F01_02_2002_T01_02_2022.csv");
    private static final File TEMP_FILE = new File("src/main/resources/temp.csv");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        printMenu();
        while (true) {
            String userInput = scanner.nextLine().trim().toLowerCase();
            if (!checkUserInput(userInput)) {
                System.out.println("\nUse command from the list above!\n");
                continue;
            }
            if (userInput.contains("exit")) {
                break;
            }
            File dataFile = selectFile(userInput);
            RateCalculator rateCalculator = new RateCalculator(dataFile, TEMP_FILE);
            rateCalculator.updateTempFile();
            applyCommand(userInput);
            System.out.println();
        }
        scanner.close();
    }

    public static void printMenu() {
        System.out.println();
        System.out.println("Available commands:");
        System.out.println("\trate EUR|TRY|USD week");
        System.out.println("\trate EUR|TRY|USD tomorrow");
        System.out.println("\texit");
        System.out.println();
    }

    private static boolean checkUserInput(String userInput) {
        Pattern pattern = Pattern.compile("(rate\\s+(eur|try|usd)\\s+tomorrow)|(rate\\s+(eur|try|usd)\\s+week)|exit");
        Matcher matcher = pattern.matcher(userInput);
        return matcher.find();
    }

    private static File selectFile(String userInput) {
        Pattern pattern = Pattern.compile("eur|try|usd");
        Matcher matcher = pattern.matcher(userInput);
        if (matcher.find()) {
            switch (matcher.group(0)) {
                case "eur":
                    return EUR_FILE;
                case "try":
                    return TRY_FILE;
                case "usd":
                    return USD_FILE;
            }
        }
        return null;
    }

    private static void applyCommand(String userInput) {
        Pattern pattern = Pattern.compile("week|tomorrow");
        Matcher matcher = pattern.matcher(userInput);
        matcher.find();

        List<List<String>> data = FileHandler.readFile(TEMP_FILE, 7);

        switch (matcher.group(0)) {
            case "tomorrow":
                System.out.println(lineFormatter(data.get(6)));
                break;
            case "week":
                for (int i = data.size() - 1; i >= 0; i--) {
                    System.out.println(lineFormatter(data.get(i)));
                }
                break;
        }
    }

    private static String lineFormatter(List<String> line) {
        DateTimeFormatter inputDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter outputDateFormat = DateTimeFormatter.ofPattern("E dd.MM.yyyy");

        LocalDate date = LocalDate.parse(line.get(0), inputDateFormat);
        double rate = Double.parseDouble(line.get(1).replace(',', '.'));
        return "\t" + outputDateFormat.format(date) + " - " + String.format("%.2f", rate);
    }
}
