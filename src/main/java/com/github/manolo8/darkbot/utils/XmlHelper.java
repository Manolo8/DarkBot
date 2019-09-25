package com.github.manolo8.darkbot.utils;

import org.dom4j.Element;

import java.util.stream.Stream;

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

    public static boolean hasNext(Element e, String attr) {
        return e.elementIterator(attr).hasNext();
    }

    public static Stream<Element> elements(Element e, String attr) {
        return e.elementIterator(attr).next().elements().stream();
    }
}