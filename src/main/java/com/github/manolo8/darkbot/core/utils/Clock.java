package com.github.manolo8.darkbot.core.utils;

public class Clock {

    public long value;
    public long last;

    public boolean isBiggerThenReset(long time) {

        last = System.currentTimeMillis() - this.value;

        if (last > time) {
            this.value = System.currentTimeMillis();
            return true;
        }

        return false;
    }

    public void reset() {
        this.value = System.currentTimeMillis();
    }

    public boolean isBigger(long time) {
        return System.currentTimeMillis() - this.value > time;
    }

    public long elapsed() {
        return System.currentTimeMillis() - value;
    }

    public void back(int value) {
        this.value = System.currentTimeMillis() - value;
    }
}
