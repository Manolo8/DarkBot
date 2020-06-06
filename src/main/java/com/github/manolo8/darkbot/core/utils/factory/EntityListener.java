package com.github.manolo8.darkbot.core.utils.factory;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.core.entities.Entity;
import com.github.manolo8.darkbot.core.utils.Lazy;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.manolo8.darkbot.Main.API;

public class EntityListener {
    private final Map<Long, EntityFactory> cachedTypes       = new HashMap<>();
    private final Map<EntityFactory, Lazy<Entity>> listeners = new EnumMap<>(EntityFactory.class);
    private Main main;

    public void add(EntityFactory type, Consumer<Entity> consumer) {
        getListener(type).add(consumer);
    }

    public void addForEach(Consumer<Entity> consumer) {
        listeners.forEach((f, e) -> add(f, consumer));
    }

    public void clearCache() {
        if (!cachedTypes.isEmpty()) cachedTypes.clear();
    }

    public void setMain(Main main) {
        if (this.main == null) this.main = main;
    }

    public void send(long address, int id) {
        EntityFactory type = getEntityType(address, id);
        switch (type) {
            case PORTAL:
                int portalType = API.readMemoryInt(address + 112);
                int x = (int) API.readMemoryDouble(address, 64, 32);
                int y = (int) API.readMemoryDouble(address, 64, 40);

                send(type, main.starManager.getOrCreate(id, portalType, x, y), address);
                break;
            case SHIP:
                int isNpc = API.readMemoryInt(address + 112);

                if (isNpc == 1) send(EntityFactory.NPC, id, address);
                else if ((isNpc == 0 && address != main.hero.address &&
                        address != main.hero.pet.address)) send(type, id, address);
                break;
            case BARRIER:
                String zoneKey = API.readMemoryString(address, 136);

                if (zoneKey.equals("NOA"))      send(type, id, address);
                else if (zoneKey.equals("DMG")) send(EntityFactory.MIST_ZONE, id, address);
                break;
            default:
                send(type, id, address);
        }
    }

    private void send(EntityFactory type, int id, long address) {
        send(type, type.createEntity(id), address);
    }

    private void send(EntityFactory type, Entity entity, long address) {
        getListener(type).send(onAdd(entity, address));
    }

    private Entity onAdd(Entity entity, long address) {
        entity.added(main);
        entity.update(address);
        entity.update();

        if (main.isRunning()) entity.clickable.setRadius(0);
        return entity;
    }

    private EntityFactory getEntityType(long address, int id) {
        return cachedTypes.computeIfAbsent(API.readMemoryLong(address + 0x10), l -> EntityFactory.find(address, id));
    }

    private Lazy<Entity> getListener(EntityFactory type) {
        return this.listeners.computeIfAbsent(type, k -> new Lazy.NoCache<>());
    }
}