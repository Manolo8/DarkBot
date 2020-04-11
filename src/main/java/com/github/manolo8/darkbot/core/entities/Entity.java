package com.github.manolo8.darkbot.core.entities;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.manager.MapManager;
import com.github.manolo8.darkbot.core.objects.Clickable;
import com.github.manolo8.darkbot.core.objects.swf.SwfArray;
import com.github.manolo8.darkbot.core.objects.Location;
import com.github.manolo8.darkbot.view.draw.Drawable;

import java.util.Arrays;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public abstract class Entity
        extends Updatable
        implements Drawable {

    public int id;

    public Location  location;
    public Clickable clickable;
    public long[]    timer;

    public boolean removed;

    public SwfArray traits;

    public Entity() {
        this.location = new Location(0);
        this.clickable = new Clickable();
        this.traits = new SwfArray(0);
        this.timer = new long[0];
    }

    public Entity(int id) {
        this();
        this.id = id;
    }

    public boolean isInvalid() {

        if (address == 0)
            return true;

        int  id        = API.readMemoryInt(address + 56);
        long container = API.readMemoryLong(address + 96);

        return container != MapManager.address || this.id != id;
    }

    @Override
    public void update() {

        if (address == 0)
            return;

        location.update();
    }

    @Override
    public void update(long address) {

        super.update(address);

        if (address == 0)
            return;

        this.traits.update(API.readMemoryLong(address + 48));
        this.location.update(API.readMemoryLong(address + 64));

        traits.update();

        searchClickableTrait();
    }

    private void searchClickableTrait() {
        for (int c = 0; c < traits.size; c++) {

            long adr = traits.elements[c];

            if (adr == 0)
                continue;

            int radius   = API.readMemoryInt(adr + 40);
            int priority = API.readMemoryInt(adr + 44);
            int enabled  = API.readMemoryInt(adr + 48);

            if (radius >= 0 && radius < 4000
                    && priority > -4 && priority < 1000
                    && (enabled == 1 || enabled == 0)) {

                clickable.update(adr);
                break;
            }
        }
    }

    public double distance(Entity entity) {
        return location.distance(entity.location);
    }

    public double distance(Location location) {
        return this.location.distance(location);
    }

    public void setTimerTo(int id, long time) {

        if (this.timer.length <= id)
            timer = Arrays.copyOf(timer, id + 1);

        timer[id] = System.currentTimeMillis() + time;
    }

    public boolean isInTimer(int id) {
        return this.timer.length > id && this.timer[id] > System.currentTimeMillis();
    }

    public boolean isInAnyTimer() {
        for (long value : timer)
            if (value > System.currentTimeMillis())
                return true;

        return false;
    }
}