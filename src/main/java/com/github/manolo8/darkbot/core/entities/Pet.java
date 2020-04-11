package com.github.manolo8.darkbot.core.entities;

import com.github.manolo8.darkbot.view.draw.GraphicDrawer;

import static com.github.manolo8.darkbot.core.manager.Core.API;
import static com.github.manolo8.darkbot.view.draw.Palette.PET;
import static com.github.manolo8.darkbot.view.draw.Palette.PET_IN;

public class Pet
        extends Ship {

    public Pet(int id) {
        super(id);
    }

    @Override
    public void update() {

        if (address == 0)
            return;

        super.update();

        id = API.readMemoryInt(address + 56);
    }

    @Override
    public void update(long address) {

        super.update(address);

        if (address == 0)
            return;

        clickable.setRadius(0);
    }

    @Override
    public void draw(GraphicDrawer drawer) {

        if (!removed) {
            drawer.set(location.x, location.y);

            drawer.setColor(PET);
            drawer.fillRectCenter(7, 7);

            drawer.move(2, 2);

            drawer.setColor(PET_IN);
            drawer.fillRectCenter(4, 4);
        }

    }
}
