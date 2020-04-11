package com.github.manolo8.darkbot.core.objects;

import com.github.manolo8.darkbot.core.itf.Updatable;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class Clickable
        extends Updatable {

    public int radius;
    public int priority;

    private int defRadius = -1;

    public void setRadius(int value) {
        if (this.radius != value) {

            if (defRadius == -1)
                defRadius = this.radius;

            API.writeMemoryIntIfOldValueMatch(address + 40, value, radius);
            this.radius = value;
        }
    }

    public void reset() {
        if (defRadius != -1 && defRadius != radius) {
            API.writeMemoryIntIfOldValueMatch(address + 40, defRadius, radius);
            radius = defRadius;
            defRadius = -1;
        }
    }

    @Override
    public void update() {

        if (address == 0)
            return;

        this.radius = API.readMemoryInt(address + 40);
        this.priority = API.readMemoryInt(address + 44);
    }

    @Override
    public void update(long address) {

        if (address == 0) {
            this.address = 0;
            return;
        }

        super.update(address);
        this.update();
    }
}
