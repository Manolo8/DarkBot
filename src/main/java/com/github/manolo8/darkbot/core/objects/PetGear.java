package com.github.manolo8.darkbot.core.objects;

import com.github.manolo8.darkbot.core.itf.Updatable;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class PetGear
        extends Updatable {

    public final int  id;
    public final long cooldown;

    public final String  name;
    public       String  inGameName;
    public       boolean active;
    public       long    code;

    private long lastUse;

    public PetGear(int id, String name, long cooldown) {
        this.id = id;
        this.name = name;
        this.cooldown = cooldown;
    }

    @Override
    public void update() {

        if (inGameName == null && id != 0)
            inGameName = API.readMemoryString(API.readMemoryLong(address + 200));

        code = API.readMemoryLong(API.readMemoryLong(address + 208) + 152);
    }

    public void used() {
        lastUse = System.currentTimeMillis();
    }

    public boolean isInCooldown() {
        return System.currentTimeMillis() - lastUse < cooldown;
    }

    @Override
    public String toString() {
        return inGameName == null ? name : inGameName;
    }
}