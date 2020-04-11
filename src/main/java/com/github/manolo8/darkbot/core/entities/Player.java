package com.github.manolo8.darkbot.core.entities;

import com.github.manolo8.darkbot.view.draw.GraphicDrawer;

import static com.github.manolo8.darkbot.view.draw.Palette.ALLIES;
import static com.github.manolo8.darkbot.view.draw.Palette.ENEMIES;

public class Player
        extends Ship {

    public Player(int id) {
        super(id);
    }

    @Override
    public void draw(GraphicDrawer drawer) {
        drawer.set(location.x, location.y);

        if (playerInfo.isEnemy())
            drawer.setColor(ENEMIES);
        else
            drawer.setColor(ALLIES);

        drawer.fillRectCenter(3, 3);
    }
}
