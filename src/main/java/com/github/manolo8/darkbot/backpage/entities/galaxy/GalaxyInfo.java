package com.github.manolo8.darkbot.backpage.entities.galaxy;

import org.dom4j.Element;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.manolo8.darkbot.utils.XmlHelper.*;

public class GalaxyInfo {
    private Integer money;
    private Integer samples;
    private Integer spinOnSale;
    private Integer spinSalePercentage;
    private Integer galaxyGateDay;
    private Integer bonusRewardsDay;
    private EnergyCost energyCosts;

    private List<Multiplier> multipliers;
    private List<Gate> gates;
    private List<Item> items;

    public GalaxyInfo() {
    }

    public void updateGalaxyInfo(Element e) {
        this.money = getValueInt(e, "money");
        this.samples = getValueInt(e, "samples");
        this.spinOnSale = getValueInt(e, "spinOnSale");
        this.spinSalePercentage = getValueInt(e, "spinSalePercentage");
        this.galaxyGateDay = getValueInt(e, "galaxyGateDay");
        this.bonusRewardsDay = getValueInt(e, "bonusRewardsDay");
        this.energyCosts = new EnergyCost(e.element("energy_cost"));

        if (hasChild(e, "multipliers")) this.multipliers = childrenOf(e, "multipliers").map(Multiplier::new).collect(Collectors.toList());
        if (hasChild(e, "gates")) this.gates = childrenOf(e, "gates").map(Gate::new).collect(Collectors.toList());
        if (hasChild(e, "items")) this.items = childrenOf(e, "items").map(Item::new).collect(Collectors.toList());
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

    public EnergyCost getEnergyCosts() {
        return energyCosts;
    }

    public List<Multiplier> getMultipliers() {
        return multipliers;
    }

    public List<Gate> getGates() {
        return gates;
    }

    public List<Item> getItems() {
        return items;
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
                ", energyCosts=" + energyCosts +
                ", multipliers=" + multipliers +
                ", gates=" + gates +
                ", items=" + items +
                '}';
    }
}
