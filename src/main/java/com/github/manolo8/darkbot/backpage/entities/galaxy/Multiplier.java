package com.github.manolo8.darkbot.backpage.entities.galaxy;

import org.dom4j.Element;

import static com.github.manolo8.darkbot.utils.XmlHelper.getAttrInt;

public class Multiplier {
    private String mode;
    private Integer state;
    private Integer value;

    private Multiplier(String mode, Integer state, Integer value) {
        this.mode = mode;
        this.state = state;
        this.value = value;
    }

    Multiplier(Element e) {
        this(e.attributeValue("mode"), getAttrInt(e, "state"), getAttrInt(e, "value"));
    }

    public String getMode() {
        return mode;
    }

    public Integer getState() {
        return state;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Multiplier{" +
                "mode='" + mode + '\'' +
                ", state=" + state +
                ", value=" + value +
                '}';
    }
}
