package com.github.manolo8.darkbot.core.objects.swf;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.utils.ByteUtils;

import static com.github.manolo8.darkbot.Main.API;

public class Array2 extends Updatable {//for pet modules also
    public int size;
    public long[] elements;

    public Array2() {
        this(0);
    }

    public Array2(long address) {
        this.address = address;
        this.elements = new long[0];
    }

    public long get(int idx) {
        return idx >= 0 && idx < size ? elements[idx] : 0;
    }

    @Override
    public void update(long address) {
        super.update(API.readMemoryLong(API.readMemoryLong(address + 72) + 64));
    }

    @Override
    public void update() {
        size = API.readMemoryInt(address + 24);

        if (size < 0 || 2048 < size || address == 0) return;
        if (elements.length < size) elements = new long[Math.min((int) (size * 1.25), 2048)];

        long table = API.readMemoryLong(address + 8) + 8;
        int length = size * 8;
        byte[] bytes = API.readMemory(table, length);

        for (int current = 0, i = 0; current < size && i < length; i += 8) {
            long address = ByteUtils.getLong(bytes, i);
            if (address != 0) elements[current++] = API.readMemoryLong(address + 216);
        }
    }
}
