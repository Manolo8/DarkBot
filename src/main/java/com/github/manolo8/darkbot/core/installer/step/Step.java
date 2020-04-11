package com.github.manolo8.darkbot.core.installer.step;

import com.github.manolo8.darkbot.core.utils.Observable;

public abstract class Step {

    public Observable<Long> addressObservable;

    public Step() {
        this.addressObservable = new Observable<>(0L);
    }

    public abstract boolean isValid();

    public boolean requireRunning() {
        return false;
    }

    public boolean blockUpdate() {
        return false;
    }

    public boolean blockTick() {
        return false;
    }
}
