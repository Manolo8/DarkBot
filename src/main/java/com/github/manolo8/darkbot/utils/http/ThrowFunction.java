package com.github.manolo8.darkbot.utils.http;

@FunctionalInterface
public interface ThrowFunction<T, R, X extends Throwable> {
    R apply(T t) throws X;
}