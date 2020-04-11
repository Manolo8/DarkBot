package com.github.manolo8.darkbot.core.objects.swf;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.utils.ByteUtils;
import com.github.manolo8.darkbot.core.utils.Observable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class SwfDictionary
        extends Updatable {

    private HashMap<String, Observable<Long>> lazy;

    private Entry[]  elements;
    private String[] checks;

    public int size;

    public SwfDictionary(long address) {
        this.address = address;
        this.elements = new Entry[0];
    }

    public void addLazy(String key, Consumer<Long> consumer) {

        if (lazy == null)
            lazy = new HashMap<>();

        Observable<Long> observable = this.lazy.get(key);

        if (observable == null) {
            observable = new Observable<>(0L);
            this.lazy.put(key, observable);

            Entry entry = entry(key);

            if (entry != null)
                observable.next(entry.value);
        }

        observable.subscribe(consumer);
    }

    public void addUniqueLazy(String key, Consumer<Long> consumer) {

        if (lazy == null) {
            addLazy(key, consumer);
        } else {
            Observable<Long> observable = this.lazy.get(key);

            if (observable != null)
                observable.clear();

            addLazy(key, consumer);
        }
    }

    public boolean hasLazy(String key) {
        return lazy != null && lazy.containsKey(key);
    }

    public Entry element(int index) {
        return elements[index];
    }

    @Override
    public void update() {

        if (address == 0)
            return;

        long tableInfo = API.readMemoryLong(address + 32);

        if (tableInfo == 0)
            return;

        int  size   = API.readMemoryInt(tableInfo + 16);
        long table  = fixToEight(API.readMemoryLong(tableInfo + 8));
        int  exp    = API.readMemoryInt(tableInfo + 20);
        int  length = (int) (Math.pow(2, exp) * 4);

        if (length > 4096 || length < 0 || size < 0 || size > 1024)
            return;

        if (elements.length < size) {
            checks = new String[size];
            elements = Arrays.copyOf(elements, size);
        }

        byte[] bytes = API.readMemory(table, length);

        int current = 0;
        int check   = 0;

        for (int i = 0; i < length; i += 16) {

            long keyAddress   = ByteUtils.getLong(bytes, i) - 2;
            long valueAddress = ByteUtils.getLong(bytes, i + 8) - 1;

            if (valueAddress >= -2 && valueAddress <= 9 || keyAddress == -2)
                continue;

            Entry entry;

            if (current >= size) {
                break;
            } else if (elements[current] == null) {
                entry = elements[current] = new Entry(API.readMemoryString(keyAddress), valueAddress);
                send(entry);
            } else if ((entry = elements[current]).value != valueAddress) {
                checks[check++] = entry.key;

                entry.key = API.readMemoryString(keyAddress);
                entry.value = valueAddress;

                //nextRow update
                send(entry);
            }

            current++;
        }

        if (this.size > current)
            while (this.size != current) {
                checks[check++] = elements[--this.size].key;
                elements[this.size] = null;
            }

        this.size = current;

        if (check > 0)
            for (check--; check >= 0; check--) {
                String str;

                if (entry(str = checks[check]) == null)
                    send(str, 0);

                checks[check] = null;
            }
    }

    private long fixToEight(long value) {

        long fix = value + 8 - (value & 0b1111);

        if (fix - value <= 0)
            fix += 8;

        return fix;
    }

    private void send(Entry entry) {
        send(entry.key, entry.value);
    }

    private void send(String key, long value) {
        if (lazy != null) {
            Observable<Long> observable = this.lazy.get(key);
            if (observable != null) {
                observable.next(value);
            }
        }
    }

    private Entry entry(String str) {
        for (int i = 0; i < size; i++) {
            Entry entry = elements[i];
            if (entry.key.equals(str))
                return entry;
        }

        return null;
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
