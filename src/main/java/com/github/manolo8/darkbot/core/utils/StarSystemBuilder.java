package com.github.manolo8.darkbot.core.utils;


import com.github.manolo8.darkbot.core.entities.Portal;
import com.github.manolo8.darkbot.core.objects.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class StarSystemBuilder {

    private List<MapBuilder> maps;

    private TreeMap<String, MapBuilder> starSystemName;

    public StarSystemBuilder() {
        this.maps = new ArrayList<>();
        this.starSystemName = new TreeMap<>();
    }

    public MapBuilder map(int id, String name) {
        return map(id, name, false);
    }

    public MapBuilder map(int id, String name, boolean gg) {

        MapBuilder builder = new MapBuilder(id, name, gg);
        maps.add(builder);

        return builder;
    }

    public void build(HashMap<Integer, Map> starSystemIndex,
                      TreeMap<String, Map> starSystemName,
                      HashMap<Integer, Portal> portalsIndex) {

        for (MapBuilder builder : maps) {
            builder.addToStarSystem();
            Map map;
            starSystemName.put(builder.name, map = new Map(builder.id, builder.name, builder.gg));
            starSystemIndex.put(builder.id, map);
        }

        for (MapBuilder builder : maps)
            builder.createPortals();

        for (MapBuilder builder : maps)
            builder.addDistance();

        for (MapBuilder builder : maps) {
            Map                 map         = starSystemName.get(builder.name);
            List<PortalBuilder> prtBuilders = builder.portals;
            Portal[]            portals     = new Portal[prtBuilders.size()];

            for (int i = 0; i < prtBuilders.size(); i++) {
                PortalBuilder prtBuilder = prtBuilders.get(i);

                Portal portal = new Portal(prtBuilder.id, prtBuilder.position, starSystemName.get(prtBuilder.targetName));

                portal.location.x = prtBuilder.x;
                portal.location.y = prtBuilder.y;

                portals[i] = portal;

                portalsIndex.put(portal.id, portal);
            }

            for (MapBuilder loop : maps) {
                Map target = starSystemName.get(loop.name);

                map.distances.put(target, loop.distances.get(builder));
            }

            map.portals = portals;
        }
    }


    public class MapBuilder {

        int                 id;
        String              name;
        List<PortalBuilder> portals;
        boolean             gg;

        HashMap<MapBuilder, Integer> distances;

        MapBuilder(int id, String name) {
            this.id = id;
            this.name = name;
            this.portals = new ArrayList<>();
            this.distances = new HashMap<>();
        }

        MapBuilder(int id, String name, boolean gg) {
            this(id, name);

            this.gg = gg;
        }

        public MapBuilder portal(int x, int y, String targetName) {

            this.portals.add(new PortalBuilder(x, y, targetName));

            return this;
        }

        public StarSystemBuilder then() {
            return StarSystemBuilder.this;
        }

        private void addToStarSystem() {
            starSystemName.put(name, this);
        }

        private void createPortals() {
            for (PortalBuilder builder : portals) {
                builder.target = starSystemName.get(builder.targetName);
            }
        }

        private void addDistance() {
            addDistance(this, 0);
        }

        private void addDistance(MapBuilder current, int distance) {
            if (distances.containsKey(current)) {
                int value = distances.get(current);
                if (value > distance) {
                    distances.put(current, distance);

                    for (PortalBuilder portal : portals)
                        portal.target.addDistance(current, distance + 1);

                }
            } else {

                distances.put(current, distance);

                for (PortalBuilder portal : portals)
                    portal.target.addDistance(current, distance + 1);

            }
        }
    }

    public class PortalBuilder {

        int        id;
        long       position;
        int        x;
        int        y;
        MapBuilder target;
        String     targetName;

        public PortalBuilder(int id, String targetName) {
            this.id = id;
            this.targetName = targetName;
        }

        public PortalBuilder(int x, int y, String targetName) {
            this(-10, targetName);
            position = ((long) x << 32) | y;
            this.x = x;
            this.y = y;
        }
    }

}
