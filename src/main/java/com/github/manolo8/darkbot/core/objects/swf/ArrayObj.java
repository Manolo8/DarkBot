package com.github.manolo8.darkbot.core.objects.swf;

/**
 * Reads array of objects. {@code Array Objects}
 */
public class ArrayObj extends VectorPtr {
    /**
     * Probably for Strings only
     */
    public ArrayObj() {
        this(0x28);
    }

    private ArrayObj(int sizeOffset) {
        super(sizeOffset, 0x20, 0x10);
    }

    /**
     * Probably for Objects
     */
    public static ArrayObj ofObject() {
        return new ArrayObj(0x38);
    }

    @Override
    public void update(long address) {
        super.update(address);
        update();
    }
}
