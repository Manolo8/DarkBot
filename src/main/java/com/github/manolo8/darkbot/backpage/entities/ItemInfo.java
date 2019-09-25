package com.github.manolo8.darkbot.backpage.entities;

import com.google.gson.annotations.SerializedName;

public class ItemInfo {

    @SerializedName("L")
    private int lootId;

    @SerializedName("name")
    private String name;

    @SerializedName("T")
    private int typeId;

    @SerializedName("C")
    private String category;

    public ItemInfo(int lootId, String name, int typeId, String category) {
        this.lootId = lootId;
        this.name = name;
        this.typeId = typeId;
        this.category = category;
    }

    public int getLoot() {
        return this.lootId;
    }

    public String getName() {
        return this.name;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getCategory() {
        return category;
    }

    public String getMoveCategory() {
        if (typeId == 0) return "lasers";
        if (typeId == 1) return "heavy_guns";
        if (typeId == 3 || typeId == 4) return "generators";
        if (typeId == 7 || typeId == 9 || typeId == 10) return "extras";
        if (typeId == 12) return "ship_upgrades";

        return "";
    }

    public String getDroneMoveCategory() {
        if (typeId == 17) return "design";
        if (typeId == 18) return "visual";

        return "default";
    }

    public String getPetMoveCategory() {
        if (typeId == 0) return "lasers";
        if (typeId == 4) return "generators";
        if (typeId == 21) return "gears";
        if (typeId == 22) return "protocols";
        if (typeId == 24) return "visual";

        return "";
    }
}