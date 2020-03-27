package com.github.manolo8.darkbot.core.objects.swf;

import com.github.manolo8.darkbot.core.utils.ByteUtils;

import static com.github.manolo8.darkbot.Main.API;

/**
 * Reads array of ints. {@code Array int}
 */
public class ArrayInt extends VectorInt {

    @Override
    public void update() {
        size = API.readMemoryInt(address + 0x28);

        if (size < 0 || size > 1024) return;
        if (elements.length < size) elements = new int[(int) Math.max(size * 1.25, 1024)];

        long table  = API.readMemoryLong(address + 0x20) + 0x10;
        byte[] data = API.readMemory(table, size * 8);

        for (int i = 0, offset = 0; i < size; i++, offset += 8) {
            elements[i] = ByteUtils.getInt(data, offset) >> 3;
        }
    }
}
