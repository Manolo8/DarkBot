package com.github.manolo8.darkbot.core.utils.module;

public interface Module {

    void resume();

    boolean canRefresh();

    void tick();
}
