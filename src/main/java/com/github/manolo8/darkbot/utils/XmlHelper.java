package com.github.manolo8.darkbot.utils;

import org.dom4j.Element;

public class XmlHelper {

    public static Integer getAttrInt(Element e, String attr) {
        String value = e.attributeValue(attr);
        if (value == null || value.isEmpty()) return null;
        return Integer.parseInt(value);
    }

    public static Integer getValueInt(Element e, String attr) {
        String value = e.elementText(attr);
        if (value == null || value.isEmpty()) return null;
        else return Integer.parseInt(value);
    }
}