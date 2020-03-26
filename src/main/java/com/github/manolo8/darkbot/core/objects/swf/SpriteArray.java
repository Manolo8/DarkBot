package com.github.manolo8.darkbot.core.objects.swf;

import static com.github.manolo8.darkbot.Main.API;

public class SpriteArray extends VectorPtr {

    public SpriteArray() {
        super(0x18, 0x8, 0x8);
    }

    @Override
    public void update(long address) {
        super.update(API.readMemoryLong(address, 0x48, 0x40));
    }
}
