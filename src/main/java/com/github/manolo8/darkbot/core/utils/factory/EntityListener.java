package com.github.manolo8.darkbot.core.utils.factory;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.core.entities.Entity;
import com.github.manolo8.darkbot.core.entities.Portal;
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

    public boolean remove(EntityFactory type, Consumer<Entity> consumer) {
        return getListener(type).remove(consumer);
    }

    public void clearCache() {
        if (!cachedTypes.isEmpty()) cachedTypes.clear();
    }

    public void setMain(Main main) {
        if (this.main == null) this.main = main;
    }


    public void sendEntity(long address, int id) {
        EntityFactory type = getEntityType(address, id).get(address);
        if (type == EntityFactory.NONE) return;

        Entity entity;

        if (type == EntityFactory.PORTAL) entity = getOrCreatePortal(address, id);
        else entity = type.createEntity(id);

        entity.added(main);
        entity.update(address);
        entity.update();
        if (main.isRunning()) entity.clickable.setRadius(0);

        getListener(type).send(entity);
    }

    private EntityFactory getEntityType(long address, int id) {
        return cachedTypes.computeIfAbsent(API.readMemoryLong(address + 0x10), l -> EntityFactory.find(address, id));
    }

    private Lazy<Entity> getListener(EntityFactory type) {
        return this.listeners.computeIfAbsent(type, k -> new Lazy.NoCache<>());
    }

    private Portal getOrCreatePortal(long address, int id) {
        int portalType = API.readMemoryInt(address + 112);
        int x = (int) API.readMemoryDouble(address, 64, 32);
        int y = (int) API.readMemoryDouble(address, 64, 40);

        return main.starManager.getOrCreate(id, portalType, x, y);
    }
}