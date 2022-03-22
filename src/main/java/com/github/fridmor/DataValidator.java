package com.github.fridmor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidator {

    public static boolean inputDataIsValid(String inputLine) {
        Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4});(\\d+,\\d{4});");
        Matcher matcher = pattern.matcher(inputLine);
        if (matcher.find()) {
            String date  = matcher.group(1);
            return dateIsValid(date);
        }
        return false;
    }

    public static boolean dateIsValid(String date) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
