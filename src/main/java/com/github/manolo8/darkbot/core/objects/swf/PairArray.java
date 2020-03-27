package com.github.manolo8.darkbot.core.objects.swf;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.utils.Lazy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.manolo8.darkbot.Main.API;

/**
 * Reads arrays with pair of values
 * Instead of EntryArray & Dictionary
 */
public class PairArray extends Updatable {
    public static final long FIX = 0xfffffffffff8L;

    private final int sizeOffset, tableOffset, bytesOffset;
    private final boolean isDictionary, autoUpdatable;

    public int size;
    public Pair[] pairs = new Pair[0];

    private Map<String, Lazy<Long>> lazy = new HashMap<>();

    protected PairArray(int sizeOffset, int tableOffset, int bytesOffset, boolean isDictionary, boolean autoUpdatable) {
        this.sizeOffset    = sizeOffset;
        this.tableOffset   = tableOffset;
        this.bytesOffset   = bytesOffset;
        this.isDictionary  = isDictionary;
        this.autoUpdatable = autoUpdatable;
    }

    /**
     * Reads pairs of {@code Array}
     */
    public static PairArray ofArray() {
        return ofArray(false);
    }

    public static PairArray ofArray(boolean autoUpdatable) {
        return new PairArray(0x50, 0x48, 0x8, false, autoUpdatable);
    }

    /**
     * Reads pairs of {@code Dictionary}
     */
    public static PairArray ofDictionary() {
        return ofDictionary(false);
    }

    public static PairArray ofDictionary(boolean autoUpdatable) {
        return new PairArray(0x10, 0x8, 0, true, autoUpdatable);
    }

    public void addLazy(String key, Consumer<Long> consumer) {
        this.lazy.computeIfAbsent(key, k -> new Lazy<>()).add(consumer);
    }

    public boolean hasKey(String key) {
        for (int i = 0; i < size && i < pairs.length; i++)
            if (pairs[i].key != null && pairs[i].key.equals(key)) return true;
        return false;
    }

    @Override
    public void update() {
        size = API.readMemoryInt(address + sizeOffset);

        if (size < 0 || size > 2048) return;
        if (pairs.length < size) pairs = new Pair[Math.min((int) (size * 1.25), 2048)];

        long index = 0;
        long table = (API.readMemoryLong(address + tableOffset) & FIX) + bytesOffset;

        for (int offset = 8, i = 0; offset < 8192 && i < size; offset += 8) {
            if (isInvalid(index)) index = API.readMemoryLong(table + offset) & FIX;
            if (isInvalid(index)) continue;

            long value = API.readMemoryLong(table + (offset += 8));
            if (isInvalid(value)) continue;

            if (pairs[i] == null) pairs[i] = new Pair();
            pairs[i++].set(API.readMemoryString(index), value & FIX, isDictionary);
            index = 0;
        }

        if (isDictionary) resetMissingObj();
    }

    @Override
    public void update(long address) {
        super.update(isDictionary ? API.readMemoryLong(address + 0x20) : address);
        if (autoUpdatable) update();
    }

    private void resetMissingObj() {
        this.lazy.entrySet().parallelStream()
                .filter(l -> l.getValue() != null && l.getValue().value != null)
                .filter(l -> l.getValue().value != 0)
                .filter(l -> !hasKey(l.getKey()))
                .forEach(l -> l.getValue().send(0L));
    }

    private boolean isInvalid(long address) {
        return isDictionary ? address < 10 : address == 0;
    }

    private class Pair {
        public String key;
        public long value;

        private void set(String index, long value, boolean onUpdate) {
            this.key   = index;
            this.value = value;

            Lazy<Long> l = lazy.get(index);
            if (l != null && (!onUpdate || (l.value != null && l.value != value))) l.send(value);
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "key='" + key + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
}
