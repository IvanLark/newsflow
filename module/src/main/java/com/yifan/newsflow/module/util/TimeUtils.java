package com.yifan.newsflow.module.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    private TimeUtils() {
    }

    public static int currentSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static String format(long seconds, String pattern) {
        return new SimpleDateFormat(pattern).format(new Date(seconds * 1000L));
    }

}
