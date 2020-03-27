package com.github.manolo8.darkbot.core.objects.swf;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.utils.ByteUtils;

import static com.github.manolo8.darkbot.Main.API;

/**
 * Reads Vector of ints. {@code Vector.<int>}
 */
public class VectorInt extends Updatable {
    public int[] elements = new int[0];
    public int size;

    public VectorInt() {
        this(0);
    }

    public VectorInt(long address) {
        this.address = address;
    }

    public int get(int idx) {
        return idx >= 0 && idx < size && idx < elements.length ? elements[idx] : 0;
    }

    @Override
    public void update() {
        size = API.readMemoryInt(address + 0x40);

        if (size < 0 || size > 1024) return;
        if (elements.length < size) elements = new int[(int) Math.max(size * 1.25, 1024)];

        long table  = API.readMemoryLong(address + 0x30) + 4;
        byte[] data = API.readMemory(table, size * 4);

        for (int i = 0, offset = 0; i < size; i++, offset += 4) {
            elements[i] = ByteUtils.getInt(data, offset);
        }
    }
}
