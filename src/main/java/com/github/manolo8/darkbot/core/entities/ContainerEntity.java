package com.github.manolo8.darkbot.core.entities;

import com.github.manolo8.darkbot.core.utils.pathfinder.Area;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public abstract class ContainerEntity
        extends Entity {

    protected final Area   area;
    public          double width;
    public          double height;

    public ContainerEntity(int id) {
        super(id);
        this.area = new Area(0, 0, 0, 0);
    }

    @Override
    public void update() {

        if (address == 0)
            return;

        super.update();

        area.minX = location.x - 20;
        area.minY = location.y - 20;
        area.maxX = location.x + (width = API.readMemoryDouble(address + 232)) + 20;
        area.maxY = location.y + (height = API.readMemoryDouble(address + 240)) + 20;
    }

    @Override
    public void update(long address) {
        super.update(address);
    }

    public Area getArea() {
        return area;
    }
}