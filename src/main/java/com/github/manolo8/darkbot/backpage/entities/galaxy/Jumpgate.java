package com.github.manolo8.darkbot.backpage.entities.galaxy;

import com.github.manolo8.darkbot.utils.XmlHelper;
import org.dom4j.Element;

import java.util.List;

public class Jumpgate {
    private Integer money;
    private Integer samples;
    private Integer spinOnSale;
    private Integer spinSalePercentage;
    private Integer galaxyGateDay;
    private Integer bonusRewardsDay;

    private List<EnergyCost> energyCosts;
    private List<Multiplier> multipliers;
    private List<Gate> gates;
    private List<Item> items;

    public Jumpgate() {

    }

    public Jumpgate(Integer money, Integer samples, Integer spinOnSale, Integer spinSalePercentage, Integer galaxyGateDay, Integer bonusRewardsDay) {
        this.money = money;
        this.samples = samples;
        this.spinOnSale = spinOnSale;
        this.spinSalePercentage = spinSalePercentage;
        this.galaxyGateDay = galaxyGateDay;
        this.bonusRewardsDay = bonusRewardsDay;
    }

    public Jumpgate(Element e) {
        this(XmlHelper.getValueInt(e, "money"), XmlHelper.getValueInt(e, "samples"),
                XmlHelper.getValueInt(e, "spinOnSale"), XmlHelper.getValueInt(e, "spinSalePrecentage"),
                XmlHelper.getValueInt(e, "galaxyGateDay"), XmlHelper.getValueInt(e, "bonusRewardsDay"));
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getSamples() {
        return samples;
    }

    public void setSamples(Integer samples) {
        this.samples = samples;
    }

    public Integer getSpinOnSale() {
        return spinOnSale;
    }

    public void setSpinOnSale(Integer spinOnSale) {
        this.spinOnSale = spinOnSale;
    }

    public Integer getSpinSalePercentage() {
        return spinSalePercentage;
    }

    public void setSpinSalePercentage(Integer spinSalePercentage) {
        this.spinSalePercentage = spinSalePercentage;
    }

    public Integer getGalaxyGateDay() {
        return galaxyGateDay;
    }

    public void setGalaxyGateDay(Integer galaxyGateDay) {
        this.galaxyGateDay = galaxyGateDay;
    }

    public Integer getBonusRewardsDay() {
        return bonusRewardsDay;
    }

    public void setBonusRewardsDay(Integer bonusRewardsDay) {
        this.bonusRewardsDay = bonusRewardsDay;
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
