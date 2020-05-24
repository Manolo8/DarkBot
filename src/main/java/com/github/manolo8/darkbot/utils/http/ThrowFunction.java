package com.github.manolo8.darkbot.utils.http;

@FunctionalInterface
public interface ThrowFunction<T, R> {
    R apply(T t) throws Exception;
}