package com.github.manolo8.darkbot.core.objects.swf;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.utils.ByteUtils;

import static com.github.manolo8.darkbot.Main.API;

public class VectorPtr extends Updatable {
    private final int sizeOffset, tableOffset, bytesOffset;

    public int size;
    public long[] elements = new long[0];

    public VectorPtr() {
        this(56, 48, 16);
    }

    protected VectorPtr(int sizeOffset, int tableOffset, int bytesOffset) {
        this.sizeOffset  = sizeOffset;
        this.tableOffset = tableOffset;
        this.bytesOffset = bytesOffset;
    }

    public static VectorPtr ofArray() {
        return new VectorPtr(56, 32, 16);
    }

    public long getLast() {
        return get(size - 1);
    }

    public long get(int idx) {
        return idx >= 0 && idx < size && idx < elements.length ? elements[idx] : 0;
    }

    @Override
    public void update() {
        size = API.readMemoryInt(address + sizeOffset);

        if (size < 0 || size > 8192 || address == 0) return;
        if (elements.length < size) elements = new long[Math.min((int) (size * 1.25), 8192)];

        long table = API.readMemoryLong(address + tableOffset) + bytesOffset;
        int length = size * 8;
        byte[] bytes = API.readMemory(table, length);

        for (int current = 0, i = 0; current < size && i < length; i += 8) {
            long value = ByteUtils.getLong(bytes, i);
            if (value != 0) elements[current++] = value & EntryArray.FIX;
        }
    }
}
