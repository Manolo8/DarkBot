package com.github.manolo8.darkbot.backpage.types;

public class Gate {

    public String  name;
    public int     group;
    public int     id;
    public int     parts;
    public int     totalParts;
    public int     currentWave;
    public int     totalWave;
    public int     lifePrice;
    public int     lifeLeft;
    public boolean prepared;
    public int     multiplier;

    public Gate(String name, int group) {
        this.name = name;
        this.group = group;
    }
}
