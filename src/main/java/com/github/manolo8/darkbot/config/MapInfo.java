package com.github.manolo8.darkbot.config;

import com.github.manolo8.darkbot.core.manager.MapManager;
import com.github.manolo8.darkbot.core.utils.pathfinder.Area;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MapInfo {

    private static   int     WIDTH  = 70;
    private static   int     HEIGHT = 35;
    private final    byte[]  area;
    private          boolean changed;
    public transient long    lastChanged;

    private transient List<Area> avoidAreas;
    private transient List<Area> workAreas;

    public MapInfo() {
        this.area = new byte[(WIDTH * HEIGHT) / 4 + 1];
        this.lastChanged = System.currentTimeMillis();
    }

    private int type(int index) {
        return (area[index / 4] >> (index % 4) * 2) & 0b11;
    }

    public List<Area> getAvoidAreas() {
        checkChanged();
        return avoidAreas;
    }

    public List<Area> getWorkAreas() {
        checkChanged();
        return workAreas;
    }

    private void setMode(int index, int set) {

        if (set > 3 || set < 0) return;

        int old = type(index);

        if (old != set) {
            changed = true;
            lastChanged = System.currentTimeMillis();

            int pos = (index % 4) * 2;
            area[index / 4] &= ~(0b11 << (pos));
            area[index / 4] |= ((set & 0b11) << (pos));
        }
    }

    private void checkChanged() {
        if (avoidAreas == null || changed) {
            rebuild();
            changed = false;
        }
    }

    private void rebuild() {

        avoidAreas = new ArrayList<>();
        workAreas = new ArrayList<>();

        foreach(tile -> {
            if (tile.type() == 1)
                workAreas.add(tile.toArea());
            else if (tile.type() == 2)
                avoidAreas.add(tile.toArea());
        });
    }

    public void foreach(Consumer<Tile> areaConsumer) {
        double pieceWidth  = MapManager.INSTANCE.internalWidth / (double) WIDTH;
        double pieceHeight = MapManager.INSTANCE.internalHeight / (double) HEIGHT;

        int maxX = WIDTH;
        int maxY = HEIGHT;

        int index = 0;

        double posX = 0;
        double posY = 0;

        Tile tile = new Tile();

        tile.width = pieceWidth;
        tile.height = pieceHeight;

        for (int x = 0; x < maxX; x++) {

            for (int y = 0; y < maxY; y++) {

                tile.x = posX;
                tile.y = posY;
                tile.index = index;

                areaConsumer.accept(tile);

                index++;
                posY += pieceHeight;
            }

            posX += pieceWidth;
            posY = 0;
        }
    }

    public class Tile {

        public double x;
        public double y;
        public double width;
        public double height;
        public int    mode;
        public int    index;

        public boolean intersect(double ox, double oy, double owidth, double oheight) {
            return (ox + owidth >= x &&
                    oy + oheight >= y &&
                    x + width >= ox &&
                    y + height >= oy);
        }

        public void setMode(int mode) {
            this.mode = mode;
            MapInfo.this.setMode(index, mode);
        }

        public int type() {
            return MapInfo.this.type(index);
        }

        public Area toArea() {
            return new Area(x + width, x, y + height, y);
        }
    }


}
