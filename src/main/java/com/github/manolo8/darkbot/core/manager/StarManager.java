package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.config.ConfigManager;
import com.github.manolo8.darkbot.config.MapInfo;
import com.github.manolo8.darkbot.core.entities.Portal;
import com.github.manolo8.darkbot.core.itf.Manager;
import com.github.manolo8.darkbot.core.objects.Location;
import com.github.manolo8.darkbot.core.objects.Map;
import com.github.manolo8.darkbot.core.utils.StarSystemBuilder;

import java.util.*;
import java.util.Map.Entry;

import static com.github.manolo8.darkbot.core.manager.Core.API;
import static com.github.manolo8.darkbot.core.manager.HeroManager.instance;

public class StarManager
        implements Manager {

    private HashMap<Integer, Map>    starSystemIndex;
    private TreeMap<String, Map>     starSystemName;
    private HashMap<Integer, Portal> portals;
    private HashMap<Integer, Portal> typePortals;

    private ConfigManager configManager;

    private HeroManager heroManager;

    public StarManager(Core core) {

        this.heroManager = core.getHeroManager();
        this.configManager = core.getConfigManager();

        this.starSystemName = new TreeMap<>();
        this.starSystemIndex = new HashMap<>();
        this.portals = new HashMap<>();
        this.typePortals = new HashMap<>();

        new StarSystemBuilder()
                .map(1, "1-1")
                .portal(18500, 11500, "1-2")
                .then()
                .map(2, "1-2")
                .portal(2000, 2000, "1-1")
                .portal(18500, 2000, "1-3")
                .portal(18500, 11500, "1-4")
                .then()
                .map(3, "1-3")
                .portal(2000, 11500, "1-2")
                .portal(18500, 11500, "1-4")
                .portal(18500, 2000, "2-3")
                .then()
                .map(4, "1-4")
                .portal(2000, 2000, "1-2")
                .portal(18500, 2000, "1-3")
                .portal(19000, 6000, "4-1")
                .portal(18500, 11500, "3-4")
                .then()
                .map(17, "1-5")
                .portal(19000, 6000, "4-4")
                .portal(10000, 12000, "4-5")
                .portal(2000, 2000, "1-6")
                .portal(2000, 11500, "1-7")
                .then()
                .map(18, "1-6")
                .portal(18500, 11500, "1-5")
                .portal(2000, 11500, "1-8")
                .then()
                .map(19, "1-7")
                .portal(2000, 2000, "1-8")
                .portal(18500, 2000, "1-5")
                .then()
                .map(20, "1-8")
                .portal(18500, 2000, "1-6")
                .portal(18500, 11500, "1-7")
                .portal(11084, 11084, "1BL")
                .then()

                .map(5, "2-1")
                .portal(2000, 11500, "2-2")
                .then()
                .map(6, "2-2")
                .portal(2000, 11500, "2-3")
                .portal(18500, 11500, "2-4")
                .portal(18500, 2000, "2-1")
                .then()
                .map(7, "2-3")
                .portal(2000, 11500, "1-3")
                .portal(18500, 11500, "2-4")
                .portal(18500, 2000, "2-2")
                .then()
                .map(8, "2-4")
                .portal(2000, 2000, "2-2")
                .portal(18500, 2000, "2-3")
                .portal(2000, 11500, "3-3")
                .portal(10000, 12000, "4-2")
                .then()
                .map(21, "2-5")
                .portal(2000, 11500, "4-4")
                .portal(18500, 11500, "4-5")
                .portal(2000, 2000, "2-6")
                .portal(18500, 2000, "2-7")
                .then()
                .map(22, "2-6")
                .portal(2000, 11500, "2-5")
                .portal(18500, 2000, "2-8")
                .then()
                .map(23, "2-7")
                .portal(2000, 11500, "2-5")
                .portal(18500, 2000, "2-8")
                .then()
                .map(24, "2-8")
                .portal(2000, 11500, "2-6")
                .portal(18500, 11500, "2-7")
                .portal(11084, 11084, "2BL")
                .then()

                .map(9, "3-1")
                .portal(2000, 2000, "3-2")
                .then()
                .map(10, "3-2")
                .portal(18500, 2000, "3-3")
                .portal(2000, 2000, "3-4")
                .portal(18500, 11500, "3-1")
                .then()
                .map(11, "3-3")
                .portal(2000, 2000, "2-4")
                .portal(2000, 11500, "3-4")
                .portal(18500, 11500, "3-2")
                .then()
                .map(12, "3-4")
                .portal(2000, 2000, "1-4")
                .portal(10000, 1500, "4-3")
                .portal(18500, 2000, "3-3")
                .portal(18500, 11500, "3-2")
                .then()
                .map(25, "3-5")
                .portal(2000, 2000, "4-4")
                .portal(16500, 1500, "4-5")
                .portal(2000, 11500, "3-6")
                .portal(18500, 11500, "3-7")
                .then()
                .map(26, "3-6")
                .portal(2000, 2000, "3-5")
                .portal(18500, 11500, "3-8")
                .then()
                .map(27, "3-7")
                .portal(2000, 11500, "3-5")
                .portal(18500, 11500, "3-8")
                .then()
                .map(28, "3-8")
                .portal(2000, 2000, "3-7")
                .portal(2000, 11500, "3-6")
                .portal(11084, 11084, "3BL")
                .then()

                .map(13, "4-1")
                .portal(1500, 6000, "1-4")
                .portal(18500, 2000, "4-2")
                .portal(18500, 11500, "4-3")
                .portal(10500, 6750, "4-4")
                .then()
                .map(14, "4-2")
                .portal(10000, 1500, "2-4")
                .portal(2000, 11500, "4-1")
                .portal(18500, 11500, "4-3")
                .portal(10500, 6750, "4-4")
                .then()
                .map(15, "4-3")
                .portal(19000, 6000, "3-4")
                .portal(2000, 2000, "4-2")
                .portal(2000, 11500, "4-1")
                .portal(10500, 6750, "4-4")
                .then()
                .map(16, "4-4")
                .portal(7000, 13500, "1-5")
                .portal(28000, 1376, "2-5")
                .portal(28000, 25124, "3-5")
                .portal(19200, 13500, "4-1")
                .portal(21900, 11941, "4-2")
                .portal(21900, 14559, "4-3")
                .then()
                .map(29, "4-5")
                .portal(7000, 13500, "1-5")
                .portal(28000, 1376, "2-5")
                .portal(28000, 25624, "3-5")
                .portal(12200, 13300, "5-1")
                .portal(25000, 6300, "5-1")
                .portal(25000, 20700, "5-1")
                .then()

                .map(91, "5-1")
                .portal(5200, 6800, "5-2")
                .portal(2900, 13500, "5-2")
                .portal(5200, 20600, "5-2")
                .then()
                .map(92, "5-2")
                .portal(2800, 3600, "5-3")
                .portal(1300, 6750, "5-3")
                .portal(2800, 10900, "5-3")
                .then()
                .map(93, "5-3")
                .portal(2000, 9500, "4-4")
                .portal(2000, 13500, "4-4")
                .portal(2000, 17500, "4-4")
                .then()

                .map(306, "1BL")
                .portal(786, 11458, "1-8")
                .portal(7589, 1456, "2BL")
                .portal(20072, 11732, "3BL")
                .then()
                .map(307, "2BL")
                .portal(9893, 862, "1BL")
                .portal(593, 5884, "2-8")
                .portal(20377, 7996, "3BL")
                .then()
                .map(308, "3BL")
                .portal(1545, 12210, "1BL")
                .portal(19400, 11854, "2BL")
                .portal(14027, 3181, "3-8")
                .then()
                .map(51, "GG ALPHA", true)
                .then()
                .map(52, "GG BETA", true)
                .then()
                .map(53, "GG GAMMA", true)

                .then()

                .build(this.starSystemIndex, this.starSystemName, this.portals);

        this.typePortals.put(2, new Portal(0, 0, fromName("GG ALPHA")));
        this.typePortals.put(3, new Portal(0, 0, fromName("GG BETA")));
        this.typePortals.put(4, new Portal(0, 0, fromName("GG GAMMA")));
    }

    public Portal portalFromId(int id, int rnd, long address) {

        Portal portal;

        if (rnd == 1) {
            portal = portals.get(id);
        } else {
            portal = typePortals.get(rnd);

            if (portal != null)
                portal.id = id;
        }

        if (portal == null) {

            Location info = new Location(API.readMemoryLong(address + 64));
            info.update();

            long position = ((long) ((int) info.x) << 32) | ((int) info.y);

            Map map = heroManager.map;

            for (Portal loop : map.portals) {
                if (loop.position == position) {
                    portal = loop;
                    portal.id = id;
                    break;
                }
            }
        }

        if (portal == null)
            portal = new Portal(id, 0, null);

        return portal;
    }

    public Portal next(Map target) {

        Map      current = heroManager.map;
        Location loc     = heroManager.location;
        Portal   closest = null;

        double distance = 1000000;
        int    min      = -1;

        for (Portal portal : current.portals) {
            int dist = portal.target.distances.get(target);
            if (min == -1 || dist <= min) {
                if (dist != min || distance > portal.location.distance(loc)) {
                    min = dist;
                    closest = portal;
                    distance = portal.location.distance(loc);
                }
            }
        }

        return closest;
    }

    public Map fromId(int id) {

        if (heroManager.map != null && heroManager.map.mapInfo != null)
            configManager.unload("config/maps/" + heroManager.map.name + ".json");

        Map map = starSystemIndex.get(id);

        if (map == null)
            map = new Map(id, "Unknown " + id);

        map.mapInfo = configManager.loadOrCreate(MapInfo.class, "config/maps/" + map.name + ".json");

        return map;
    }

    public Map fromName(String name) {
        return starSystemName.get(name);
    }

    public Map baseMap() {
        switch (instance.playerInfo.factionId) {
            case 1:
                return fromName("1-1");
            case 2:
                return fromName("2-1");
            case 3:
                return fromName("3-1");
            default:
                return fromName("4-4");
        }
    }

    public Collection<String> getAllAvailableMaps() {
        List<String> stringList = new ArrayList<>();

        for (Entry<String, Map> entry : this.starSystemName.entrySet())
            if (!entry.getValue().gg)
                stringList.add(entry.getKey());

        return stringList;
    }

    public Collection<Map> getMaps() {
        return this.starSystemName.values();
    }
}
