package com.github.manolo8.darkbot.core.entities;

public class Dron {
    private String lootId;
    private int repairPrice;
    private String itemId;
    private String repairCurrency;
    private int droneLevel;
    private int damage;

    public Dron(String lootId, int repairPrice, String itemId, String repairCurrency, int droneLevel, int damage) {
        this.lootId = lootId;
        this.repairPrice = repairPrice;
        this.itemId = itemId;
        this.repairCurrency = repairCurrency;
        this.droneLevel = droneLevel;
        this.damage = damage;
    }

    public String getLootId() {
        return lootId;
    }

    public int getRepairPrice() {
        return repairPrice;
    }

    public String getItemId() {
        return itemId;
    }

    public String getRepairCurrency() {
        return repairCurrency;
    }

    public int getDroneLevel() {
        return droneLevel;
    }

    public int getDamage() {
        return damage;
    }
}
