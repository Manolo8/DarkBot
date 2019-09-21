package com.github.manolo8.darkbot.backpage.entities.galaxy;

public class Multiplier {
    private String mode;
    private int state;
    private int value;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Multiplier{" +
                "mode='" + mode + '\'' +
                ", state=" + state +
                ", value=" + value +
                '}';
    }
}
