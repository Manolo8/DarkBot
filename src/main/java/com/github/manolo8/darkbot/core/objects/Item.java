package com.github.manolo8.darkbot.core.objects;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.modules.LootNCollectorModule;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class Item
        extends Updatable {

    private long cooldownAddress;

    public final String  name;
    public       boolean selected;
    public       boolean active;

    public Item(String name) {
        this.name = name;
    }

    public void updateCooldownAddress(long value) {
        cooldownAddress = value;
    }

    @Override
    public void update() {

        if (address == 0)
            return;

        selected = API.readMemoryBoolean(address + 44);
        active = cooldownAddress != 0 && API.readMemoryBoolean(cooldownAddress);

        active = selected && API.readMemoryBoolean(cooldownAddress + 56);
    }
}
