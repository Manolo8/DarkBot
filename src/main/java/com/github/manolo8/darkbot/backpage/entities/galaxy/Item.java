package com.github.manolo8.darkbot.backpage.entities.galaxy;

public class Item {
    private String date;
    private String state;
    private String type;

    private int gateId;
    private int duplicate;
    private int partId;
    private int itemId;
    private int amount;
    private int current;
    private int total;
    private int multiplierUsed;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getGateId() {
        return gateId;
    }

    public void setGateId(int gateId) {
        this.gateId = gateId;
    }

    public int getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(int duplicate) {
        this.duplicate = duplicate;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getMultiplierUsed() {
        return multiplierUsed;
    }

    public void setMultiplierUsed(int multiplierUsed) {
        this.multiplierUsed = multiplierUsed;
    }

    @Override
    public String toString() {
        return "Item{" +
                "date='" + date + '\'' +
                ", state='" + state + '\'' +
                ", type='" + type + '\'' +
                ", gateId=" + gateId +
                ", duplicate=" + duplicate +
                ", partId=" + partId +
                ", itemId=" + itemId +
                ", amount=" + amount +
                ", current=" + current +
                ", total=" + total +
                ", multiplierUsed=" + multiplierUsed +
                '}';
    }
}
