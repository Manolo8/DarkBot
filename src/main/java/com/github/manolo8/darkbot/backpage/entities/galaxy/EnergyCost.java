package com.github.manolo8.darkbot.backpage.entities.galaxy;

public class EnergyCost {
    private String mode;
    private int value;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "EnergyCost{" +
                "mode='" + mode + '\'' +
                ", value=" + value +
                '}';
    }
}
