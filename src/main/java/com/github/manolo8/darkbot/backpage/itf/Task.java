package com.github.manolo8.darkbot.backpage.itf;

import com.github.manolo8.darkbot.core.utils.Clock;

import java.util.Random;

public abstract class Task {

    private final        Clock  clock;
    private final        long   wait;

    public Task(long wait) {
        this.clock = new Clock();
        this.wait = wait;
    }

    public boolean shouldExecute() {
        return clock.isBiggerThenReset(wait);
    }

    public long lastExecuted() {
        return clock.last;
    }

    public abstract void execute();

}
