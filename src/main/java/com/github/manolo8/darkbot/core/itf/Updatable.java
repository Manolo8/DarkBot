package com.github.manolo8.darkbot.core.itf;

public abstract class Updatable {

    public long address;

    public void update(long address) {
        this.address = address;
    }

    public abstract void update();
}
