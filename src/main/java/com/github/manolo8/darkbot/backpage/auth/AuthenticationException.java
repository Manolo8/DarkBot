package com.github.manolo8.darkbot.backpage.auth;

import com.github.manolo8.darkbot.core.exception.BasicException;

public class AuthenticationException extends BasicException {

    public AuthenticationException(String message) {
        super(message, false);
    }
}
