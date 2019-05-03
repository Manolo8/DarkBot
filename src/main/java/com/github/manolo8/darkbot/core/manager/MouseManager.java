package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.core.utils.ClickPoint;
import com.github.manolo8.darkbot.core.utils.Location;

import static com.github.manolo8.darkbot.Main.API;

public class MouseManager {

    private static MouseManager INSTANCE;
    private Location center = new Location();
    private ClickPoint holding = null;
    private final MapManager map;

    public MouseManager(MapManager map) {
        INSTANCE = this;
        this.map = map;
    }

    public void clickCenter(boolean single, Location aim) {
        ClickPoint clickPoint = pointCenter(aim);
        internalClick(clickPoint.x, clickPoint.y, single);
    }

    public static void mouseClick(int x, int y) {
        if (INSTANCE != null) INSTANCE.internalClick(x, y, true);
    }

    public void holdTowards(Location loc, boolean confirm) {
        holdTowards(pointCenter(loc), confirm);
    }

    public void release() {
        if (holding == null) holding = pointLoc(center());
        releaseInternal();
        holding = null;
    }

    private void holdTowards(ClickPoint click, boolean confirm) {
        if (holding == null || confirm ) Main.API.mousePress(click.x, click.y);
        else Main.API.mouseMove(click.x, click.y);
        holding = click;
    }

    private void internalClick(int x, int y, boolean single) {
        if (holding != null) releaseInternal();
        long l = System.currentTimeMillis();
        API.mouseClick(x, y);
        if (!single) API.mouseClick(x, y);
        System.out.println(System.currentTimeMillis() - l);

        if (holding != null) holdTowards(holding, true);
    }

    private void releaseInternal() {
        Main.API.mouseRelease(holding.x, holding.y);
    }

    private Location center() {
        center.x = map.boundX + map.width / 2;
        center.y = map.boundY + map.height / 2;
        return center;
    }

    private ClickPoint pointCenter(Location aim) {
        return pointLoc(center().toAngle(center, center.angle(aim), 200 + Math.random() * 50));
    }

    private ClickPoint pointLoc(Location loc) {
        return new ClickPoint((int) ((loc.x - map.boundX) / (map.boundMaxX - map.boundX) * (double) MapManager.clientWidth),
                (int) ((loc.y - map.boundY) / (map.boundMaxY - map.boundY) * (double) MapManager.clientHeight));
    }

}
