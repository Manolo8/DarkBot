package com.github.manolo8.darkbot.core;

import com.github.manolo8.darkbot.core.objects.swf.SwfArray;
import com.github.manolo8.darkbot.core.objects.swf.SwfElementChildren;
import com.github.manolo8.darkbot.core.utils.Matcher;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class DarkFlashUtils {

    private SwfElementChildren children;
    private SwfArray           array;

    public DarkFlashUtils() {
        this.children = new SwfElementChildren(0);
        this.array = new SwfArray(0);
    }

    public long inArrayGetAddressIfMatch(long arrayAddress, int tableOffset, Matcher matcher) {

        array.tableOffset = tableOffset;
        array.update(arrayAddress);
        array.update();

        for (int i = 0; i < array.size; i++) {
            long value = array.elements[i];

            if (matcher.match(value))
                return value;
        }

        return 0;
    }

    public long inElementGetChildren(long elementAddress, int index) {
        children.update(elementAddress);
        children.update();

        if (children.size >= index)
            return children.elements[index];

        return 0;
    }

    public long inElementGetLastChildren(long elementAddress) {
        children.update(elementAddress);
        children.update();

        if (children.size > 0)
            return children.elements[children.size - 1];

        return 0;
    }

    public long inElementGetFirstChildren(long elementAddress) {
        children.update(elementAddress);
        children.update();

        if (children.size > 0)
            return children.elements[0];

        return 0;
    }

    public int readIntFromIntHolder(long address, int holderOffset) {
        return API.readMemoryInt(API.readMemoryLong(address + holderOffset) + 40);
    }

    public String readStringFromStringHolder(long address, int holderOffset) {
        return API.readMemoryString(API.readMemoryLong(API.readMemoryLong(address + holderOffset) + 40));
    }
}