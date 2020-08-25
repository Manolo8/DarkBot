package com.github.manolo8.darkbot.backpage.hangar;

import java.util.List;

public class Map {
    private List<String> types;
    private List<String> lootIds;

    public List<String> getTypes() {
        return types;
    }

    public List<String> getLootIds() {
        return lootIds;
    }

    @Override
    public String toString() {
        return "Map{" +
                "types=" + types +
                ", lootIds=" + lootIds +
                '}';
    }
}
