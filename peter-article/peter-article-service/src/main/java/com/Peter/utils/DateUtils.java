package com.Peter.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String date2Str(Date date, String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }
    public static String getCurrentTimeStr(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

        String format = simpleDateFormat.format(new Date());
        return format;
    }
}
