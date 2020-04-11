package com.github.manolo8.darkbot.core;

import com.github.manolo8.darkbot.core.manager.Core;

import java.nio.charset.StandardCharsets;

public class DarkFlash {

    static {
        System.loadLibrary("DarkFlash");
    }

    public native void setCookie(String key, String value);

    public void load(String url, String vars, String base) {
        new Thread(
                () -> loadSWF(url, vars, base)
        ).start();
    }

    public void reload() {
        Core.WAIT.back(10_000);
        reloadSWF();
    }

    private native void loadSWF(String url, String vars, String base);

    private native void reloadSWF();

    public native void mousePress(int x, int y);

    public native void keyPress(char key);

    public native byte[] readMemory(long address, int size);

    public native double readMemoryDouble(long address);

    public native long readMemoryLong(long address);

    public native int readMemoryInt(long address);

    public String readMemoryString(long address) {

        if (address == 0)
            return "ERROR";

        int flags = readMemoryInt(address + 36);
        int width = (flags & 0x00000001);
        int size  = readMemoryInt(address + 32) << width;
        int type  = (flags & 0x00000006) >> 1;

        if (size > 512 || size < 0)
            return "ERROR";

        byte[] bytes;

        if (type == 2)
            bytes = readMemory(readMemoryLong(readMemoryLong(address + 24) + 16) + readMemoryInt(address + 16), size);
        else
            bytes = readMemory(readMemoryLong(address + 16), size);

        return width == 0 ? new String(bytes, StandardCharsets.ISO_8859_1) : new String(bytes, StandardCharsets.UTF_16LE);
    }

    public boolean readMemoryBoolean(long address) {
        return readMemoryInt(address) == 1;
    }

    public native void writeMemory(long address, byte[] data);

    public native void writeMemoryDouble(long address, double value);

    public native void writeMemoryLong(long address, long value);

    public native void writeMemoryInt(long address, int value);

    public native void writeMemoryIntIfOldValueMatch(long address, int value, int oldValue);

    public native long[] queryMemory(byte[] data, int max);

    public native long[] queryMemoryInt(int value, int maxQuantity);

    public native long[] queryMemoryLong(long value, int maxQuantity);

    public native void fix();

    public native void setVisible(boolean visible);

    public native void setRender(boolean render);
}
