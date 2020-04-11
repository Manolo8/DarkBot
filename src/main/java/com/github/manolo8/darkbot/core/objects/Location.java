package com.github.manolo8.darkbot.core.objects;

import com.github.manolo8.darkbot.core.itf.Updatable;

import static com.github.manolo8.darkbot.core.manager.Core.API;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.StrictMath.atan2;

public class Location
        extends Updatable {

    public double  x;
    public double  y;
    public boolean valid;

    public Location() {
        valid = false;
    }

    public Location(double x, double y) {
        this();
        this.x = x;
        this.y = y;
    }

    public Location(long address) {
        update(address);
    }

    public double distance(double ox, double oy) {
        return sqrt(pow(x - ox, 2) + pow(y - oy, 2));
    }

    public double distance(Location o) {
        return sqrt(pow(x - o.x, 2) + pow(y - o.y, 2));
    }

    public double angle(Location o) {
        return atan2(y - o.y, x - o.x);
    }

    public Location copy() {
        return new Location(x, y);
    }

    @Override
    public void update(long address) {
        super.update(address);

        valid = address != 0;

        x = 0;
        y = 0;
    }

    @Override
    public void update() {

        if (address == 0)
            return;

        x = API.readMemoryDouble(address + 32);
        y = API.readMemoryDouble(address + 40);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return false;

        if (obj instanceof Location) {
            Location location = (Location) obj;

            return location.x == x && location.y == y;
        }

        return false;
    }
}
