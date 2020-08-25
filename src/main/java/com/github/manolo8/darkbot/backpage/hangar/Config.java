package com.github.manolo8.darkbot.backpage.hangar;

import java.util.Map;

public class Config {
    private Equipment ship;
    private Map<String, Equipment> drones;
    private Equipment pet;

    public Equipment getShip() {
        return ship;
    }

    public Map<String, Equipment> getDrones() {
        return drones;
    }

    public Equipment getPet() {
        return pet;
    }

    @Override
    public String toString() {
        return "Config{" +
                "ship=" + ship +
                ", drones=" + drones +
                ", pet=" + pet +
                '}';
    }
}
