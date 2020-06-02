package com.github.manolo8.darkbot.core.objects.facades;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.objects.swf.ObjArray;

import static com.github.manolo8.darkbot.Main.API;

public class LogMediator extends Updatable {
    private ObjArray messageBuffer = ObjArray.ofArrStr();

    @Override
    public void update() {
        messageBuffer.update(API.readMemoryLong(address + 0x60));
        if (messageBuffer.size <= 0 || 50 < messageBuffer.size) return;

        messageBuffer.forEachMemorized(this::print);
    }

    private void print(long pointer) {
        String val = API.readMemoryString(API.readMemoryLong(pointer + 0x28));
        if (val != null && !val.trim().isEmpty()) System.out.println(val);
    }
}