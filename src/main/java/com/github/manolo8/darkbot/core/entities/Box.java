package com.github.manolo8.darkbot.core.entities;

import com.github.manolo8.darkbot.config.BoxInfo;
import com.github.manolo8.darkbot.config.ConfigEntity;
import com.github.manolo8.darkbot.view.draw.GraphicDrawer;
import com.github.manolo8.darkbot.view.draw.Palette;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class Box
        extends Entity {

    public BoxInfo boxInfo;
    public boolean ignore;

    public Box(int id) {
        super(id);
    }

    @Override
    public void update(long address) {

        super.update(address);

        if (address == 0)
            return;

        if (traits.size == 0) {
            boxInfo = ConfigEntity.INSTANCE.getOrCreateBoxInfo("UNKNOWN");
            return;
        }

        long data = traits.elements[0];

        data = API.readMemoryLong(data + 64);
        data = API.readMemoryLong(data + 32);
        data = API.readMemoryLong(data + 24);
        data = API.readMemoryLong(data + 8);
        data = API.readMemoryLong(data + 16);
        data = API.readMemoryLong(data + 24);

        String type = API.readMemoryString(data);

        if (type.length() > 5) {
            int index;
            type = (index = type.indexOf(',')) > 0 ? type.substring(4, index) : type.substring(4);
        }

        boxInfo = ConfigEntity.INSTANCE.getOrCreateBoxInfo(type);
    }

    @Override
    public void draw(GraphicDrawer drawer) {
        drawer.set(location.x, location.y);

        drawer.setColor(Palette.BOXES);

        if (boxInfo.collect)
            drawer.fillRectCenter(3, 3);
        else
            drawer.drawRectCenter(3, 3);
    }
}
