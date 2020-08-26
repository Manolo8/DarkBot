package com.github.manolo8.darkbot.backpage.hangar;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ItemInfo extends Item {
    private String name;
    private String localizationId;
    @SerializedName("C") private String category;
    private List<Map<String, Object>> levels;

    public String getName() {
        return name;
    }

    public String getLocalizationId() {
        return localizationId;
    }

    public String getCategory() {
        return category;
    }

    public List<Map<String, Object>> getLevels() {
        return levels;
    }

    public void setLocalizationId(String localizationId) {
        this.localizationId = localizationId;
    }

    @Override
    public String toString() {
        return "ItemInfo{" +
                "name='" + name + '\'' +
                ", localizationId='" + localizationId + '\'' +
                ", category='" + category + '\'' +
                ", levels=" + levels +
                "} " + super.toString();
    }
}
