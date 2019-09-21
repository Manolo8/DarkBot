package com.github.manolo8.darkbot.backpage.entities.galaxy;

import java.util.List;

public class Jumpgate {
    private int money;
    private int samples;
    private int spinOnSale;
    private int spinSalePercentage;
    private int galaxyGateDay;
    private int bonusRewardsDay;

    private List<EnergyCost> energyCosts;
    private List<Multiplier> multipliers;
    private List<Gate> gates;
    private List<Item> items;

    public Jumpgate() {

    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        this.samples = samples;
    }

    public List<EnergyCost> getEnergyCosts() {
        return energyCosts;
    }

    public void setEnergyCosts(List<EnergyCost> energyCosts) {
        this.energyCosts = energyCosts;
    }

    public List<Multiplier> getMultipliers() {
        return multipliers;
    }

    public void setMultipliers(List<Multiplier> multipliers) {
        this.multipliers = multipliers;
    }

    public List<Gate> getGates() {
        return gates;
    }

    public void setGates(List<Gate> gates) {
        this.gates = gates;
    }

    public int getSpinOnSale() {
        return spinOnSale;
    }

    public void setSpinOnSale(int spinOnSale) {
        this.spinOnSale = spinOnSale;
    }

    public int getSpinSalePercentage() {
        return spinSalePercentage;
    }

    public void setSpinSalePercentage(int spinSalePercentage) {
        this.spinSalePercentage = spinSalePercentage;
    }

    public int getGalaxyGateDay() {
        return galaxyGateDay;
    }

    public void setGalaxyGateDay(int galaxyGateDay) {
        this.galaxyGateDay = galaxyGateDay;
    }

    public int getBonusRewardsDay() {
        return bonusRewardsDay;
    }

    public void setBonusRewardsDay(int bonusRewardsDay) {
        this.bonusRewardsDay = bonusRewardsDay;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Jumpgate{" +
                "money=" + money +
                ", samples=" + samples +
                ", spinOnSale=" + spinOnSale +
                ", spinSalePercentage=" + spinSalePercentage +
                ", galaxyGateDay=" + galaxyGateDay +
                ", bonusRewardsDay=" + bonusRewardsDay +
                '}';
    }
}
