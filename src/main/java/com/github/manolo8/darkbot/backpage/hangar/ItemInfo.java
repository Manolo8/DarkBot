package com.github.manolo8.darkbot.backpage.hangar;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ItemInfo extends Item {
    private String name;
    @SerializedName("C") private String category;
    private List<Map<String, Object>> levels;

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public List<Map<String, Object>> getLevels() {
        return levels;
    }

    @Override
    public String toString() {
        return "ItemInfo{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", levels=" + levels +
                "} " + super.toString();
    }
}
