package com.github.manolo8.darkbot.core.objects.swf;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.utils.ByteUtils;

import static com.github.manolo8.darkbot.Main.API;

public class Array extends Updatable {
    public long[] elements;
    public int size;
    private int tableOffset;

    public Array() {
        this(0);
    }

    public Array(long address) {
        this(address, 48);
    }

    public Array(long address, int tableOffset) {//32 for pet modules
        this.address = address;
        this.elements = new long[0];
        this.tableOffset = tableOffset;
    }

    public long get(int idx) {
        return idx >= 0 && idx < size ? elements[idx] : 0;
    }

    @Override
    public void update() {
        size = API.readMemoryInt(address + 56);

        if (size < 0 || size > 8192 || address == 0) return;
        if (elements.length < size) elements = new long[Math.min((int) (size * 1.25), 8192)];

        long table = API.readMemoryLong(address + tableOffset) + 16;
        int length = size * 8;
        byte[] bytes = API.readMemory(table, length);

        for (int current = 0, i = 0; current < size && i < length; i += 8) {
            long value = ByteUtils.getLong(bytes, i);
            if (value != 0) elements[current++] = value - 1;
        }
    }
}
