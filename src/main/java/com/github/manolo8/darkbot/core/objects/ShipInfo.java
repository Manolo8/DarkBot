package com.github.manolo8.darkbot.core.objects;

import com.github.manolo8.darkbot.core.itf.Updatable;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class ShipInfo
        extends Updatable {

    public int  speed;
    public long target;
    public int  angle;

    public Location destination;

    public ShipInfo() {
        this.destination = new Location(0);
    }

    @Override
    public void update() {

        if (address == 0)
            return;

        speed = API.readMemoryInt(API.readMemoryLong(address + 72) + 40);
        angle = API.readMemoryInt(API.readMemoryLong(address + 48) + 32);
        target = API.readMemoryLong(address + 112);

        destination.update(API.readMemoryLong(address + 96));
        destination.update();
    }

}
