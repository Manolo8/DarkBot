package com.github.manolo8.darkbot.backpage.entities.galaxy;

public class Gate {
    private String state;

    private int total;
    private int current;
    private int id;
    private int prepared;
    private int totalWave;
    private int currentWave;
    private int livesLeft;
    private int lifePrice;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrepared() {
        return prepared;
    }

    public void setPrepared(int prepared) {
        this.prepared = prepared;
    }

    public int getTotalWave() {
        return totalWave;
    }

    public void setTotalWave(int totalWave) {
        this.totalWave = totalWave;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public void setCurrentWave(int currentWave) {
        this.currentWave = currentWave;
    }

    public int getLivesLeft() {
        return livesLeft;
    }

    public void setLivesLeft(int livesLeft) {
        this.livesLeft = livesLeft;
    }

    public int getLifePrice() {
        return lifePrice;
    }

    public void setLifePrice(int lifePrice) {
        this.lifePrice = lifePrice;
    }

    @Override
    public String toString() {
        return "Gate{" +
                "state='" + state + '\'' +
                ", total=" + total +
                ", current=" + current +
                ", id=" + id +
                ", prepared=" + prepared +
                ", totalWave=" + totalWave +
                ", currentWave=" + currentWave +
                ", livesLeft=" + livesLeft +
                ", lifePrice=" + lifePrice +
                '}';
    }
}
