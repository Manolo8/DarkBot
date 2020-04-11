package com.github.manolo8.darkbot.core.exception;

public class BasicException extends Throwable {

    public BasicException(String message, boolean writableStackTrace) {
        super(message, null, false, writableStackTrace);
    }
}
