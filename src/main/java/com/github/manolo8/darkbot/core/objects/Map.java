package com.github.manolo8.darkbot.core.objects;

import com.github.manolo8.darkbot.config.MapInfo;
import com.github.manolo8.darkbot.core.entities.Portal;

import java.util.HashMap;

public class Map {

    public int                   id;
    public String                name;
    public MapInfo               mapInfo;
    public Portal[]              portals;
    public HashMap<Map, Integer> distances;

    public boolean gg;

    public Map(int id, String name) {
        this.id = id;
        this.name = name;
        this.distances = new HashMap<>();
        this.portals = new Portal[0];
        this.mapInfo = new MapInfo();
    }

    public Map(int id, String name, boolean gg) {
        this(id, name);

        this.gg = gg;
    }
}
