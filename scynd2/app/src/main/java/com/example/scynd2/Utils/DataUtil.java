package com.example.scynd2.Utils;


public class DataUtil {
    /**
     * Convert duration to hours, minutes and seconds
     * @param duration total seconds
     * @return HH:mm:ss
     */
    public static String getFormattedTime(int duration){
        int hours = 0, minutes = 0, seconds = 0;
        StringBuilder sb = new StringBuilder();

        //h = duration/3600s
        hours = duration / 3600;
        duration = duration % 3600;
        if (hours < 10){
            sb.append("0");
        }
        sb.append(hours + ":");

        //min = duration/60s
        minutes = duration / 60;
        duration = duration % 60;
        if (minutes < 10) {
            sb.append("0");
        }
        sb.append(minutes + ":");

        //min = duration
        seconds = duration;
        if (seconds < 10) {
            sb.append("0");
        }
        sb.append(seconds + "");

        return sb.toString();
    }

    /**
     * Determine if it is a pure number
     * @param str string to be judged
     * @return output
     */
    public static boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }
}
