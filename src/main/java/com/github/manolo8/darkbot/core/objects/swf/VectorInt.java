package com.github.manolo8.darkbot.core.objects.swf;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.utils.ByteUtils;

import static com.github.manolo8.darkbot.Main.API;

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
        size = API.readMemoryInt(address + 64);

        if (size < 1 || size > 512) return;
        if (elements.length < size) elements = new int[size];

        byte[] data = API.readMemory(API.readMemoryLong(address + 48) + 4, size * 4);

        for (int i = 0, count = 0; i < data.length; i += 4, count++) {
            elements[count] = ByteUtils.getInt(data, i);
        }
    }
}
