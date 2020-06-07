package com.github.manolo8.darkbot.core.utils;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.config.NpcInfo;
import com.github.manolo8.darkbot.core.entities.Barrier;
import com.github.manolo8.darkbot.core.entities.BasePoint;
import com.github.manolo8.darkbot.core.entities.BattleStation;
import com.github.manolo8.darkbot.core.entities.Box;
import com.github.manolo8.darkbot.core.entities.Entity;
import com.github.manolo8.darkbot.core.entities.FakeNpc;
import com.github.manolo8.darkbot.core.entities.NoCloack;
import com.github.manolo8.darkbot.core.entities.Npc;
import com.github.manolo8.darkbot.core.entities.Portal;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.itf.Obstacle;
import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.objects.swf.ObjArray;
import com.github.manolo8.darkbot.core.utils.factory.EntityFactory;
import com.github.manolo8.darkbot.core.utils.factory.EntityListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.github.manolo8.darkbot.Main.API;

public class EntityList extends Updatable {
    private final EntityListener entityListener = new EntityListener();

    public final List<Obstacle> obstacles           = new ArrayList<>();
    public final List<Barrier> barriers             = register(EntityFactory.BARRIER);
    public final List<NoCloack> noCloack            = register(EntityFactory.MIST_ZONE);
    public final List<Box> boxes                    = register(EntityFactory.BOX, EntityFactory.ORE);
    public final List<Box> mines                    = register(EntityFactory.MINE);
    public final List<Npc> npcs                     = register(EntityFactory.NPC, EntityFactory.LOW_RELAY);
    public final List<Portal> portals               = register(EntityFactory.PORTAL);
    public final List<Ship> ships                   = register(EntityFactory.SHIP);
    public final List<BattleStation> battleStations = register(EntityFactory.CBS_WRECK_MODULE, EntityFactory.CBS_ASTEROID, EntityFactory.CBS_MODULE, EntityFactory.CBS_STATION);
    public final List<BasePoint> basePoints         = register(EntityFactory.BASE_HANGAR, EntityFactory.BASE_STATION, EntityFactory.HEADQUARTER, EntityFactory.QUEST_GIVER);
    public final List<Entity> unknown               = register(EntityFactory.UNKNOWN);
    public final FakeNpc fakeNpc;

    public final List<List<? extends Entity>> allEntities =
            Arrays.asList(barriers, noCloack, boxes, npcs, portals, ships, battleStations, basePoints, unknown);

    private final Main main;
    private final Set<Integer> ids = new HashSet<>();
    private final ObjArray entitiesArr = ObjArray.ofVector();

    public EntityList(Main main) {
        this.main    = main;
        this.fakeNpc = new FakeNpc(main);
        this.entityListener.setMain(main);

        this.entityListener.addForEach(entity -> {
            if (entity instanceof Obstacle)
                obstacles.add((Obstacle) entity);
        });

        this.main.status.add(this::refreshRadius);
        this.main.addInvalidTickListener(entityListener::clearCache);
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity> List<T> register(EntityFactory... types) {
        List <T> list = new ArrayList<>();
        for (EntityFactory type : types)
            this.entityListener.add(type, e -> list.add((T) e));

        return list;
    }

    @Override
    public void update() {
        synchronized (Main.UPDATE_LOCKER) {
            removeAllInvalidEntities();

            refreshEntities();

            updatePing(main.mapManager.pingLocation, main.guiManager.pet.getTrackedNpc());
        }

    }

    @Override
    public void update(long address) {
        super.update(address);

        entitiesArr.update(API.readMemoryLong(address + 40));

        clear();
    }

    private void refreshEntities() {
        entitiesArr.update();
        for (int i = 0; i < entitiesArr.getSize(); i++) {
            long entityPtr = entitiesArr.get(i);

            int id = API.readMemoryInt(entityPtr + 56);
            if (ids.add(id)) entityListener.sendEntity(entityPtr, id);
        }
    }

    private void whenRemove(Entity entity) {
        entity.removed();
    }

    private void removeAllInvalidEntities() {
        main.hero.pet.removed = main.hero.pet.isInvalid(address);

        for (List<? extends Entity> entities : allEntities) {
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);

                if (entity.isInvalid(address) ||
                        entity.address == main.hero.address || entity.address == main.hero.pet.address) {
                    entities.remove(i);
                    ids.remove(entity.id);
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

    public void updatePing(Location location, NpcInfo info) {
        fakeNpc.set(location, info);
        boolean shouldBeNpc = location != null && info != null && fakeNpc.isPingAlive() &&
                npcs.stream().noneMatch(n -> fakeNpc != n && n.npcInfo == info && n.locationInfo.distance(location) < 500);

        if (!shouldBeNpc) npcs.remove(fakeNpc);
        else if (!npcs.contains(fakeNpc)) npcs.add(fakeNpc);
    }

    private void doInEachEntity(Consumer<Entity> consumer) {
        for (List<? extends Entity> entities : allEntities) {
            entities.forEach(consumer);
        }
    }

    private void clear() {
        synchronized (Main.UPDATE_LOCKER) {
            ids.clear();

            obstacles.clear();
            fakeNpc.removed();

            for (List<? extends Entity> entities : allEntities) {
                for (Entity entity : entities) entity.removed();
                entities.clear();
            }
        }
    }

    private void refreshRadius(boolean value) {
        synchronized (Main.UPDATE_LOCKER) {
            if (value) doInEachEntity(entity -> entity.clickable.setRadius(0));
            else doInEachEntity(entity -> entity.clickable.reset());
        }
    }
}
