package com.example.scynd2.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     * Get the date in the specified format
     *
     * @return get a formatted date yyyy-MM-dd
     */
    public static String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    /**
     * Convert a string to a Date
     * yyyy-MM-dd
     * @param date yyyy-MM-dd
     * @return get a Date
     */
    public static Date strToDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * Formatting a Date
     * @param date a Date
     * @return get a formatted String of date
     */
    public static String getFormattedTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * Convert String to Date
     * yyyy-MM-dd HH:mm:ss
     * @param time a String of time
     * @return get a Date of this String
     */
    public static Date strToTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
}
