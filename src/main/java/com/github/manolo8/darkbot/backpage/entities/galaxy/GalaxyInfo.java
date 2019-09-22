package com.github.manolo8.darkbot.backpage.entities.galaxy;

import com.github.manolo8.darkbot.utils.XmlHelper;
import org.dom4j.Element;

import java.util.List;

public class GalaxyInfo {
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

    public GalaxyInfo() {

    }

    public void updateGalaxyInfo(Element e) {
        this.money = XmlHelper.getValueInt(e, "money");
        this.samples = XmlHelper.getValueInt(e, "samples");
        this.spinOnSale = XmlHelper.getValueInt(e, "spinOnSale");
        this.spinSalePercentage = XmlHelper.getValueInt(e, "spinSalePrecentage");
        this.galaxyGateDay = XmlHelper.getValueInt(e, "galaxyGateDay");
        this.bonusRewardsDay = XmlHelper.getValueInt(e, "bonusRewardsDay");
    }

    public Integer getMoney() {
        return money;
    }

    public Integer getSamples() {
        return samples;
    }

    public Integer getSpinOnSale() {
        return spinOnSale;
    }

    public Integer getSpinSalePercentage() {
        return spinSalePercentage;
    }

    public Integer getGalaxyGateDay() {
        return galaxyGateDay;
    }

    public Integer getBonusRewardsDay() {
        return bonusRewardsDay;
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
        return "GalaxyInfo{" +
                "money=" + money +
                ", samples=" + samples +
                ", spinOnSale=" + spinOnSale +
                ", spinSalePercentage=" + spinSalePercentage +
                ", galaxyGateDay=" + galaxyGateDay +
                ", bonusRewardsDay=" + bonusRewardsDay +
                '}';
    }
}
