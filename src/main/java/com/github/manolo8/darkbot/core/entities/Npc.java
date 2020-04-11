package com.github.manolo8.darkbot.core.entities;

import com.github.manolo8.darkbot.config.ConfigEntity;
import com.github.manolo8.darkbot.config.NpcInfo;
import com.github.manolo8.darkbot.core.manager.MapManager;
import com.github.manolo8.darkbot.core.objects.Location;
import com.github.manolo8.darkbot.core.utils.Clock;
import com.github.manolo8.darkbot.view.draw.GraphicDrawer;
import com.github.manolo8.darkbot.view.draw.Palette;

public class Npc
        extends Ship {

    public NpcInfo npcInfo;

    private Clock    clock;
    private Location temp;

    public Npc(int id) {
        super(id);

        this.clock = new Clock();
        this.temp = new Location();
    }

    @Override
    public void update() {
        String oldName = playerInfo.username;

        super.update();

        //noinspection StringEquality
        if (oldName != playerInfo.username)
            npcInfo = ConfigEntity.INSTANCE.getOrCreateNpcInfo(playerInfo.username);

        //Since the npcs does not have speed updated, we need to do that
        if (clock.isBiggerThenReset(200)) {

            int current = (int) ((temp.distance(location)) * 5);

            if (current != 0) {
                if (shipInfo.speed == 0)
                    shipInfo.speed = current;
                else
                    shipInfo.speed = (shipInfo.speed * 3 + current) / 4;
            }

            temp.x = location.x;
            temp.y = location.y;
        }
    }

    public boolean isInCorner() {
        return location.x == 0 && location.y == 0 || location.x == MapManager.INSTANCE.internalWidth && location.y == MapManager.INSTANCE.internalHeight;
    }

    @Override
    public void draw(GraphicDrawer drawer) {

        drawer.set(location.x, location.y);
        drawer.setColor(Palette.NPCS);

        if (npcInfo.kill)
            drawer.fillRectCenter(3, 3);
        else
            drawer.drawRectCenter(3, 3);
    }
}
