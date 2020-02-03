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

    public long getElement(int element) {
        return (element > size) ? 0 : ((element >= elements.length) ? 0 : elements[element]);
    }

    @Override
    public void update(long address) {
        super.update(API.readMemoryLong(API.readMemoryLong(address + 72) + 64));
    }

    @Override
    public void update() {
        size = API.readMemoryInt(address + 24);

        if (size < 0 || 2048 < size || address == 0) return;
        if (elements.length - 1 != size) elements = new long[size];

        long table = API.readMemoryLong(address + 8) + 8;
        int length = size * 8;
        byte[] bytes = API.readMemory(table, length);

        for (int current = 0, i = 0; i < length; i += 8) {
            long address = ByteUtils.getLong(bytes, i) - 1;
            if (address != -1 && current < elements.length) elements[current++] = API.readMemoryLong(address + 217);
        }
    }
}
