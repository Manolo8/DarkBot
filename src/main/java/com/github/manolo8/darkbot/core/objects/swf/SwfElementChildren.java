package com.github.manolo8.darkbot.core.objects.swf;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.utils.ByteUtils;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class SwfElementChildren
        extends Updatable {

    public int    size;
    public long[] elements;

    public SwfElementChildren(long address) {
        this.update(address);
        this.elements = new long[0];
    }

    @Override
    public void update(long address) {

        if (address != 0)
            this.address = API.readMemoryLong(API.readMemoryLong(address + 72) + 64);
    }

    @Override
    public void update() {

        if (address == 0)
            return;

        checkSize();

        if (size != 0)
            updateData();
    }

    private void checkSize() {
        int size = API.readMemoryInt(address + 24);

        if (size < 0 || size > 2048) {
            this.size = 0;
        } else {
            this.size = size;

            if (elements.length < size)
                elements = new long[size];
        }
    }

    private void updateData() {
        long table  = API.readMemoryLong(address + 8) + 8;
        int  length = size * 8;

        byte[] bytes = API.readMemory(table, length);

        int current = 0;

        for (int i = 0; i < length; i += 8) {
            long value = ByteUtils.getLong(bytes, i) - 1;

            if (value != -1 && current < elements.length)
                elements[current++] = readElement(value);
        }
    }

    private long readElement(long value) {
        return API.readMemoryLong(value + 217);
    }
}
