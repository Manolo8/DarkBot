package com.github.manolo8.darkbot.backpage.entities.galaxy;

import org.dom4j.Element;

import static com.github.manolo8.darkbot.utils.XmlHelper.getAttrInt;

public class Gate {
    private String state;

    private Integer total;
    private Integer current;
    private Integer id;
    private Integer prepared;
    private Integer totalWave;
    private Integer currentWave;
    private Integer livesLeft;
    private Integer lifePrice;

    private Gate(String state, Integer total, Integer current, Integer id, Integer prepared, Integer totalWave, Integer currentWave, Integer livesLeft, Integer lifePrice) {
        this.state = state;
        this.total = total;
        this.current = current;
        this.id = id;
        this.prepared = prepared;
        this.totalWave = totalWave;
        this.currentWave = currentWave;
        this.livesLeft = livesLeft;
        this.lifePrice = lifePrice;
    }

    Gate(Element e) {
        this(e.attributeValue("state"), getAttrInt(e, "total"), getAttrInt(e, "current"),
                getAttrInt(e, "id"), getAttrInt(e, "prepared"), getAttrInt(e, "totalWave"),
                getAttrInt(e, "currentWave"), getAttrInt(e, "livesLeft"), getAttrInt(e, "lifePrice"));
    }

    public String getState() {
        return state;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getCurrent() {
        return current;
    }

    public Integer getId() {
        return id;
    }

    public Integer getPrepared() {
        return prepared;
    }

    public Integer getTotalWave() {
        return totalWave;
    }

    public Integer getCurrentWave() {
        return currentWave;
    }

    public Integer getLivesLeft() {
        return livesLeft;
    }

    public Integer getLifePrice() {
        return lifePrice;
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
