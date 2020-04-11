package com.github.manolo8.darkbot.backpage.utils;

public abstract class AbstractWalker {

    protected final String data;
    protected final int    end;
    protected       int    index;

    public AbstractWalker(String data, int start, int end) {
        this.data = data;
        this.index = start;
        this.end = end;
    }

    protected boolean nextIsEquals(String value) {
        return data.regionMatches(index + 1, value, 0, value.length());
    }

    protected boolean nextIsEquals(char value) {
        return data.charAt(index + 1) == value;
    }
}
