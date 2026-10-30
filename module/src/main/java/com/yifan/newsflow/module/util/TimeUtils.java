package com.yifan.newsflow.module.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    private TimeUtils() {
    }

    public static int currentSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static int parseDate(String value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        ParsePosition position = new ParsePosition(0);
        Date date = format.parse(value, position);
        if (date == null || position.getIndex() != value.length()) {
            throw new RuntimeException("publish time format must be yyyy-MM-dd");
        }
        return (int) (date.getTime() / 1000);
    }

    public static String format(long seconds, String pattern) {
        return new SimpleDateFormat(pattern).format(new Date(seconds * 1000L));
    }

}
