package com.github.manolo8.darkbot.core.entities;

import com.github.manolo8.darkbot.view.draw.GraphicDrawer;
import com.github.manolo8.darkbot.view.draw.Palette;

public class BasePiece
        extends Entity {

    public BasePiece(int id) {
        super(id);
    }

    @Override
    public void draw(GraphicDrawer drawer) {
        drawer.setColor(Palette.ALLIES);
        drawer.set(location.x, location.y);
        drawer.fillOvalCenter(5, 5);
    }
}
