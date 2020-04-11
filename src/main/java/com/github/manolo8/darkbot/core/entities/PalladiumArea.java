package com.github.manolo8.darkbot.core.entities;

import com.github.manolo8.darkbot.view.draw.GraphicDrawer;
import com.github.manolo8.darkbot.view.draw.Palette;

public class PalladiumArea
        extends ContainerEntity {

    public PalladiumArea(int id) {
        super(id);
    }

    @Override
    public void draw(GraphicDrawer drawer) {

        drawer.setColor(Palette.PALLADIUM_AREA);

        drawer.set(
                area.minX,
                area.minY
        );

        drawer.drawRect(
                drawer.translateX(area.maxX - area.minX),
                drawer.translateY(area.maxY - area.minY)
        );
    }
}
