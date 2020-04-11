package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.DarkBotApp;
import com.github.manolo8.darkbot.core.entities.*;
import com.github.manolo8.darkbot.core.itf.Manager;
import com.github.manolo8.darkbot.core.itf.Obstacle;
import com.github.manolo8.darkbot.core.objects.swf.SwfArray;

import java.util.*;
import java.util.function.Consumer;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class EntityManager
        implements Manager {

    private final Core        core;
    private final StarManager starManager;
    private final HeroManager heroManager;

    private final SwfArray                     entitiesAddress;
    private final List<List<? extends Entity>> allEntities;
    private final HashSet<Integer>             ids;
    private final HashMap<Integer, long[]>     cached;

    public final List<Obstacle> obstacles;

    public final List<Barrier>       barriers;
    public final List<PalladiumArea> palladiumAreas;

    public final List<Box>           boxes;
    public final List<Npc>           npcs;
    public final List<Portal>        portals;
    public final List<Ship>          ships;
    public final List<BasePiece>     basePieces;
    public final List<BattleStation> battleStations;

    public final List<Entity> unknown;

    private long address;

    public EntityManager(Core core) {

        this.core = core;
        this.starManager = core.getStarManager();
        this.heroManager = core.getHeroManager();

        this.entitiesAddress = new SwfArray(0);
        this.allEntities = new ArrayList<>();

        this.ids = new HashSet<>();
        this.cached = new HashMap<>();

        this.obstacles = new ArrayList<>();

        this.barriers = new ArrayList<>();
        this.palladiumAreas = new ArrayList<>();
        this.boxes = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.portals = new ArrayList<>();
        this.ships = new ArrayList<>();
        this.battleStations = new ArrayList<>();
        this.basePieces = new ArrayList<>();
        this.unknown = new ArrayList<>();

        this.allEntities.add(barriers);
        this.allEntities.add(palladiumAreas);
        this.allEntities.add(boxes);
        this.allEntities.add(npcs);
        this.allEntities.add(portals);
        this.allEntities.add(ships);
        this.allEntities.add(battleStations);
        this.allEntities.add(basePieces);
        this.allEntities.add(unknown);

        core.status.subscribe(this::refreshRadius);
    }

    void update() {

        if (this.address != MapManager.address)
            this.update(MapManager.address);

        synchronized (DarkBotApp.UPDATE_LOCKER) {

            removeAllInvalidEntities();
            refreshEntities();
            clearCache();

        }

    }

    private void update(long address) {
        this.address = address;
        entitiesAddress.update(API.readMemoryLong(address + 40));
        clear();
    }

    private void refreshEntities() {

        if (heroManager.address == 0)
            return;

        entitiesAddress.update();

        for (int i = 0; i < entitiesAddress.size; i++) {

            long found = entitiesAddress.elements[i];

            if (found == 0)
                continue;

            int id = API.readMemoryInt(found + 56);

            if (!ids.add(id))
                continue;

            int rnd  = API.readMemoryInt(found + 112);
            int rnd2 = API.readMemoryInt(found + 116);

            String key = API.readMemoryString(API.readMemoryLong(found + 136));

            if (key.equals("NOA")) {
                barriers.add(whenAdd(new Barrier(id), found));
            } else if (key.equals("DMG")) {
                palladiumAreas.add(whenAdd(new PalladiumArea(id), found));
            } else if (id < 0 && rnd == 3) {
                boxes.add(whenAdd(new Box(id), found));
            } else if (id <= 150000499 && id >= 150000156) {
                portals.add(whenAdd(starManager.portalFromId(id, rnd, found), found));
            } else if (id <= 150000950 && id >= 150000532 && rnd2 > 0 && rnd2 < 255) {
                battleStations.add(whenAdd(new BattleStation(id), found));
            } else if (id <= 150000147 && id >= 150000000) {
                basePieces.add(whenAdd(new BasePiece(id), found));
            } else {

                int npc     = API.readMemoryInt(found + 112);
                int visible = API.readMemoryInt(found + 116);
                int c       = API.readMemoryInt(found + 120);
                int d       = API.readMemoryInt(found + 124);

                if ((visible == 1 || visible == 0) && (c == 1 || c == 0) && d == 0) {
                    if (npc == 1)
                        npcs.add(whenAdd(new Npc(id), found));
                     else if (npc == 0 && found != heroManager.address && found != heroManager.pet.address)
                        ships.add(whenAdd(new Player(id), found));
                } else
                    unknown.add(whenAdd(new Unknown(id), found));

            }

        }

    }

    private void clearCache() {
        Iterator<Map.Entry<Integer, long[]>> i = cached.entrySet().iterator();

        main:
        while (i.hasNext()) {
            for (long value : i.next().getValue())
                if (value > System.currentTimeMillis())
                    continue main;

            i.remove();
        }
    }

    private <E extends Entity> E whenAdd(E entity, long address) {

        long[] temp = cached.get(entity.id);

        if (temp != null)
            entity.timer = temp;

        entity.update(address);
        entity.update();

        if (entity instanceof Obstacle)
            obstacles.add((Obstacle) entity);

        if (core.isRunning())
            entity.clickable.setRadius(0);

        return entity;
    }

    private void whenRemove(Entity entity) {
        entity.removed = true;
        ids.remove(entity.id);

        if (entity.isInAnyTimer())
            cached.put(entity.id, entity.timer);
    }

    private void removeAllInvalidEntities() {

        Pet pet = heroManager.pet;

        pet.removed = pet.isInvalid();

        for (List<? extends Entity> entities : allEntities) {
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);

                if (entity.isInvalid()) {
                    entities.remove(i);
                    whenRemove(entity);
                    i--;
                } else {
                    entity.update();
                }
            }
        }

        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obstacle = obstacles.get(i);
            if (obstacle.isRemoved()) {
                obstacles.remove(i);
                i--;
            }
        }
    }

    public void doInEachEntity(Consumer<Entity> consumer) {
        allEntities.forEach(entities -> entities.forEach(consumer));
    }

    private void clear() {

        ids.clear();

        obstacles.clear();

        for (List<? extends Entity> entities : allEntities) {
            for (Entity entity : entities)
                entity.removed = true;
            entities.clear();
        }
    }

    private void refreshRadius(boolean value) {
        if (value)
            doInEachEntity(entity -> entity.clickable.setRadius(0));
        else
            doInEachEntity(entity -> entity.clickable.reset());
    }
}