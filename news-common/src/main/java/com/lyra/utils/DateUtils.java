package com.lyra.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static Date stringOfDate(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
             date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
}
