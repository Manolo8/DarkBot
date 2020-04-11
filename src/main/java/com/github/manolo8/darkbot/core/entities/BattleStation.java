package com.github.manolo8.darkbot.core.entities;

import com.github.manolo8.darkbot.core.itf.Obstacle;
import com.github.manolo8.darkbot.core.objects.PlayerInfo;
import com.github.manolo8.darkbot.core.utils.pathfinder.Area;
import com.github.manolo8.darkbot.view.draw.GraphicDrawer;
import com.github.manolo8.darkbot.view.draw.Palette;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class BattleStation
        extends Entity
        implements Obstacle {

    public PlayerInfo info;

    private Area area;

    public BattleStation(int id) {
        super(id);

        this.info = new PlayerInfo();
        this.area = new Area(0, 0, 0, 0);
    }

    @Override
    public void update() {

        if (address == 0)
            return;

        super.update();

        info.update();

        if (location.valid && area.minX == 0 && area.minY == 0)
            area.set(location, 1400, 1000);
    }

    @Override
    public void update(long address) {

        super.update(address);

        if (address == 0) {
            return;
        }

        info.update(API.readMemoryLong(address + 120));
    }

    @Override
    public Area getArea() {
        return area;
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    @Override
    public boolean use() {
        return info.isEnemy();
    }

    @Override
    public void draw(GraphicDrawer drawer) {
        drawer.set(
                location.x,
                location.y
        );

        if (info.isEnemy())
            drawer.setColor(Palette.ENEMIES);
        else
            drawer.setColor(Palette.ALLIES);

        drawer.fillRectCenter(
                25,
                19
        );
    }
}
