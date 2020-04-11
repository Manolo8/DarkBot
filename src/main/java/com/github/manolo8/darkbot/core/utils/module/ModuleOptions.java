package com.github.manolo8.darkbot.core.utils.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleOptions {

    String value();

    boolean showInModules() default true;

    boolean alwaysNewInstance() default false;
}
