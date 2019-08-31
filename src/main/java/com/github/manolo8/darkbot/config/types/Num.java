package com.github.manolo8.darkbot.config.types;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Num {
    int min() default 0;
    int max() default 100;
    int step() default 5;
}
