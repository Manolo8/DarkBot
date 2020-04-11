package com.github.manolo8.darkbot.core.objects.swf;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.utils.ByteUtils;

import java.util.Arrays;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class SwfObject
        extends Updatable {

    public int     size;
    public Entry[] elements;

    public SwfObject(long address) {
        update(address);
        elements = new Entry[0];
    }

    @Override
    public void update() {

        int  size   = API.readMemoryInt(address + 40);
        long table  = fixToEight(API.readMemoryLong(address + 32));
        int  exp    = API.readMemoryInt(address + 44);
        int  length = (int) (Math.pow(2, exp) * 4);

        if (length > 4096 || length < 0 || size < 0 || size > 1024)
            return;

        if (elements.length < size)
            elements = Arrays.copyOf(elements, size);

        byte[] bytes = API.readMemory(table, length);

        int current = 0;

        for (int i = 0; i < length; i += 16) {

            long keyAddress   = ByteUtils.getLong(bytes, i) - 2;
            long valueAddress = ByteUtils.getLong(bytes, i + 8) - 1;

            if (valueAddress >= -1 && valueAddress <= 5)
                continue;

            Entry entry;

            if (current >= size) {
                break;
            } else if (elements[current] == null) {
                elements[current] = new Entry(stringTo(keyAddress), valueAddress);
            } else if ((entry = elements[current]).value != valueAddress) {
                entry.key = stringTo(keyAddress);
                entry.value = valueAddress;
            }

            current++;
        }

        this.size = current;
    }

    private long fixToEight(long value) {
        return value + 8 - (value & 0b1111);
    }

    private String stringTo(long address) {
        if (address < 1000)
            return String.valueOf(address);
        return
                API.readMemoryString(address);
    }

    public static class Entry {

        public String key;
        public long   value;

        public Entry(String key, long value) {
            this.key = key;
            this.value = value;
        }
    }
}
