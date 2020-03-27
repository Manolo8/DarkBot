package com.github.manolo8.darkbot.core.objects.swf;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.utils.ByteUtils;

import static com.github.manolo8.darkbot.Main.API;

public class VectorPtr extends Updatable {
    private final int sizeOffset, tableOffset, bytesOffset;

    public int size;
    public long[] elements = new long[0];

    public VectorPtr() {
        this(0x38, 0x30, 0x10);
    }

    protected VectorPtr(int sizeOffset, int tableOffset, int bytesOffset) {
        this.sizeOffset  = sizeOffset;
        this.tableOffset = tableOffset;
        this.bytesOffset = bytesOffset;
    }

    public long getLast() {
        return get(size - 1);
    }

    public long get(int idx) {
        return idx >= 0 && idx < size && idx < elements.length ? elements[idx] : 0;
    }

    public int indexOf(long value) {
        for (int i = size - 1; i >= 0; i--) if (value == elements[i]) return i;
        return -1;
    }

    @Override
    public void update() {
        size = API.readMemoryInt(address + sizeOffset);

        if (size < 0 || size > 8192 || address == 0) return;
        if (elements.length < size) elements = new long[Math.min((int) (size * 1.25), 8192)];

        long table = API.readMemoryLong(address + tableOffset) + bytesOffset;
        byte[] bytes = API.readMemory(table, size * 8);

        for (int i = 0, offset = 0; offset < bytes.length && i < size; offset += 8) {
            long value = ByteUtils.getLong(bytes, offset);
            if (value != 0) elements[i++] = value & ByteUtils.FIX;
        }
    }
}
