package com.github.manolo8.darkbot.core.objects;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.manager.MapManager;
import com.github.manolo8.darkbot.core.manager.SchedulerManager;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class Gui
        extends Updatable {

    public long addressInfo;

    public boolean visible;

    private final Point minimized;
    private final Point size;
    private final Point pos;

    public int x;
    public int y;
    public int width;
    public int height;

    private long time;
    private long update;

    public Gui(long address) {

        this.address = address;

        this.size = new Point(0L);
        this.pos = new Point(0L);
        this.minimized = new Point(0L);

        update();
    }

    @Override
    public void update(long address) {
        if (address == 0) {
            reset();
        } else {
            super.update(address);
            this.addressInfo = API.readMemoryLong(address + 496);
            this.update = System.currentTimeMillis();
        }
    }

    public void update() {

        if (address == 0 || addressInfo == 0)
            return;

        size.update(API.readMemoryLong(addressInfo + 80L));
        pos.update(API.readMemoryLong(addressInfo + 72L));
        minimized.update(API.readMemoryLong(addressInfo + 112L));

        size.update();
        pos.update();
        minimized.update();

        width = (int) Math.round(size.x);
        height = (int) Math.round(size.y);
        x = (int) Math.round((MapManager.INSTANCE.clientWidth - size.x) * 0.01D * pos.x);
        y = (int) Math.round((MapManager.INSTANCE.clientHeight - size.y) * 0.01D * pos.y);

        visible = API.readMemoryBoolean(addressInfo + 32);
    }

    public void reset() {
        this.address = 0;
        this.visible = false;
        this.height = 0;
        this.width = 0;
        this.update = System.currentTimeMillis();
    }

    public boolean lastUpdatedIn(long time) {
        return System.currentTimeMillis() - update > time;
    }

    public long lastUpdate() {
        return System.currentTimeMillis() - update;
    }

    public void click(int x, int y) {
        SchedulerManager.INSTANCE.asyncClick(this.x + x, this.y + y);
    }

    public boolean show(boolean value) {
        if (value != visible && addressInfo != 0) {
            if (System.currentTimeMillis() - 1000 > time) {

                if (value)
                    API.mousePress((int) minimized.x + 5, (int) minimized.y + 5);
                else
                    API.mousePress(x + 5, y + 5);

                time = System.currentTimeMillis();
            }
            return false;
        }

        return System.currentTimeMillis() - 1000 > time && value == visible;
    }
}