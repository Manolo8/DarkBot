package com.github.manolo8.darkbot.view.utils;

import java.util.HashSet;

public class MapGroup {

    public static long SHOW_RADIUS           = 1 << 1;
    public static long SHOW_CIRCLE           = 1 << 2;
    public static long SHOW_PRIORITY         = 1 << 3;
    public static long SHOW_AMMO             = 1 << 4;
    public static long SHOW_KILL             = 1 << 5;
    public static long SHOW_USE_PET_KAMIKAZE = 1 << 6;

    private final String name;
    private final long   columns;
    private final int[]  maps;

    public MapGroup(String name, int[] maps, long columns) {
        this.name = name;
        this.maps = maps;
        this.columns = columns;
    }

    public String getName() {
        return name;
    }

    public boolean hasColumn(int id) {
        return id == 0 || (columns >> id & 1) == 1;
    }

    public boolean contains(HashSet<Integer> mapSet) {

        if (maps == null)
            return true;

        for (int map : maps)
            if (mapSet.contains(map))
                return true;

        return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
