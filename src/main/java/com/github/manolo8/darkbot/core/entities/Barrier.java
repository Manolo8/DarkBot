package com.github.manolo8.darkbot.core.entities;

import com.github.manolo8.darkbot.core.itf.Obstacle;
import com.github.manolo8.darkbot.core.utils.pathfinder.Area;
import com.github.manolo8.darkbot.view.draw.GraphicDrawer;
import com.github.manolo8.darkbot.view.draw.Palette;
import javafx.scene.paint.Color;

public class Barrier
        extends ContainerEntity
        implements Obstacle {

    public Barrier(int id) {
        super(id);
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
        return true;
    }

    @Override
    public void draw(GraphicDrawer drawer) {

        drawer.set(
                location.x,
                location.y
        );

        double x = drawer.translateX(width) - 1;
        double y = drawer.translateY(height) - 1;

        drawer.setColor(Palette.BARRIER);
        drawer.fillRect(x, y);
    }
}
