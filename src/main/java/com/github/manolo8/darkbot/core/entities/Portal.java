package com.github.manolo8.darkbot.core.entities;

import com.github.manolo8.darkbot.core.objects.Map;
import com.github.manolo8.darkbot.view.draw.GraphicDrawer;
import com.github.manolo8.darkbot.view.draw.Palette;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class Portal
        extends Entity {

    public Map target;

    public long position;
    public int  type;

    public Portal(int id, long position, Map target) {
        super(id);

        this.target = target;
        this.position = position;
    }

    @Override
    public void update() {

        if (address == 0)
            return;

        super.update();


        type = API.readMemoryInt(address + 112);
    }

    @Override
    public void draw(GraphicDrawer drawer) {
        drawer.setColor(Palette.PORTALS);

        drawer.set(location.x, location.y);
        drawer.drawOvalCenter(12, 12);
    }
}
