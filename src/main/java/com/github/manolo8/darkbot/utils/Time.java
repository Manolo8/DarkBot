package com.github.manolo8.darkbot.utils;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Time {
    public static final int SECOND = 1000, MINUTE = SECOND * 60, HOUR = MINUTE * 60, DAY = HOUR * 24, WEEK = DAY * 7;

    private static final DateTimeFormatter LOG_DATE      = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss.SSS");
    private static final DateTimeFormatter FILENAME_DATE = DateTimeFormatter.ofPattern("uuuu-MM-dd_HH-mm-ss_SSS");

    public static String toString(Integer time) {
        if (time == null) return "-";
        return toString(time.intValue());
    }

    public static String toString(long time) {
        StringBuilder builder = new StringBuilder();
        int seconds = (int) (time / 1000L);
        if (seconds >= 3600) {
            int hours = seconds / 3600;
            if (hours < 10) {
                builder.append('0');
            }
            builder.append(hours).append(':');
        }
        if (seconds >= 60) {
            int minutes = seconds % 3600 / 60;
            if (minutes < 10) {
                builder.append('0');
            }
            builder.append(minutes).append(':');
        }
        if ((seconds %= 60) < 10) {
            builder.append('0');
        }
        builder.append(seconds);
        return builder.toString();
    }

    public static void sleepMax(long time, int total) {
        time = System.currentTimeMillis() - time;
        sleep(total - time);
    }

    public static void sleep(long millis) {
        if (millis <= 0) return;
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {}
    }

    public static PrintStream getLogger() throws FileNotFoundException {
        return new PrintStreamWithDate("logs/" + LocalDateTime.now().format(FILENAME_DATE) + ".log");
    }

    public static class PrintStreamWithDate extends PrintStream {
        public PrintStreamWithDate(String logfileName) throws FileNotFoundException {
            super(logfileName);
        }

        @Override
        public void println(String string) {
            super.println("[" + LocalDateTime.now().format(LOG_DATE) + "] " + string);
        }
    }
}
