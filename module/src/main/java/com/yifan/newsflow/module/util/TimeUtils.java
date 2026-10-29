package com.yifan.newsflow.module.util;

public class TimeUtils {

    private TimeUtils() {
    }

    public static int currentSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }

}
