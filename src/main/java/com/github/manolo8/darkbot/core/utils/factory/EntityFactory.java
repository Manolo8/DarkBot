package com.github.manolo8.darkbot.core.utils.factory;

import com.github.manolo8.darkbot.core.entities.Barrier;
import com.github.manolo8.darkbot.core.entities.BasePoint;
import com.github.manolo8.darkbot.core.entities.BattleStation;
import com.github.manolo8.darkbot.core.entities.Box;
import com.github.manolo8.darkbot.core.entities.Entity;
import com.github.manolo8.darkbot.core.entities.MapNpc;
import com.github.manolo8.darkbot.core.entities.NoCloack;
import com.github.manolo8.darkbot.core.entities.Npc;
import com.github.manolo8.darkbot.core.entities.Pet;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.core.utils.ByteUtils;
import org.intellij.lang.annotations.Language;

import java.util.function.Function;
import java.util.regex.Pattern;

import static com.github.manolo8.darkbot.Main.API;

public enum EntityFactory {
    BOX      (Box::new,       "box_.*"),
    ORE      (Box::new,       "ore_.*"),
    MINE     (Box::new,       "mine_.*"),
    FIREWORK (Entity::new,    "firework_box"),
    X2_BEACON(BasePoint::new, "beacon_.*"),

    LOW_RELAY (MapNpc::new, "relay"),
    NPC_BEACON(MapNpc::new, "npc-beacon.*"),

    CBS_ASTEROID    (BattleStation::new, "asteroid"),
    CBS_CONSTRUCTION(BattleStation::new, "cbs-construction"),
    CBS_MODULE      (BattleStation::new, "module_.*"),
    CBS_MODULE_CON  (BattleStation::new, "module-construction"),
    CBS_WRECK_MODULE(BattleStation::new, "wreck"),
    CBS_STATION     (BattleStation::new, "battleStation"),

    POD_HEAL        (BasePoint::new, "pod_heal"),
    SPACE_BALL      (BasePoint::new, "mapIcon_spaceball"),
    BUFF_CAPSULE    (BasePoint::new, "buffCapsule_.*"),
    BURNING_TRAIL   (BasePoint::new, "burning_trail_entity_.*"),
    PLUTUS_GENERATOR(BasePoint::new, "plutus-generator"),

    REFINERY      (BasePoint::new, "refinery_.*"),
    HOME_ZONE     (BasePoint::new, "ctbHomeZone_.*"),
    BASE_TURRET   (BasePoint::new, "turret_.*"),
    BASE_HANGAR   (BasePoint::new, "hangar_.*"),
    BASE_STATION  (BasePoint::new, "station_.*"),
    HEADQUARTER   (BasePoint::new, "headquarters_.*"),
    QUEST_GIVER   (BasePoint::new, "questgiver_.*"),
    REPAIR_STATION(BasePoint::new, "repairstation_.*"),

    BARRIER  (Barrier::new, EntityFactory::defineZoneType, "NOA|DMG"),
    MIST_ZONE(NoCloack::new),

    SHIP(Ship::new, EntityFactory::defineShipType),
    NPC (Npc::new),

    PET    (Pet::new),
    PORTAL ("[0-9]+$"),
    UNKNOWN(Entity::new);

    private Pattern pattern;
    private Function<Long, EntityFactory> customType;
    private Function<Integer, ? extends Entity> constructor;

    EntityFactory(@Language("RegExp") String regex) { this(null, regex); }
    EntityFactory(Function<Integer, ? extends Entity> constructor) { this(constructor, null, null); }
    EntityFactory(Function<Integer, ? extends Entity> constructor, @Language("RegExp") String regex) { this(constructor, null, regex); }
    EntityFactory(Function<Integer, ? extends Entity> constructor, Function<Long, EntityFactory> customType) { this(constructor, customType, null); }
    EntityFactory(Function<Integer, ? extends Entity> constructor, Function<Long, EntityFactory> customType, @Language("RegExp") String regex) {
        this.constructor = constructor;
        this.customType  = customType;
        if (regex != null) this.pattern = Pattern.compile(regex);
    }

    public EntityFactory get(long address) {
        return this.customType != null ? this.customType.apply(address) : this;
    }

    public Entity createEntity(int id) {
        return this.constructor != null ? this.constructor.apply(id) : new Entity(id);
    }

    public static EntityFactory find(long address, int id) {
        String assetId = getAssetId(address);
        System.out.println(assetId + " | " + getZoneKey(address));

        for (EntityFactory type : EntityFactory.values()) {
            if (type.pattern == null) continue;
            if (type.pattern.matcher(type == BARRIER ?
                    getZoneKey(address) : assetId).matches()) return type;
        }

        return isPet(address) ? PET : isShip(address, id) ? SHIP : UNKNOWN;
    }

    private static EntityFactory defineZoneType(long address) {
        String key = getZoneKey(address);
        return (key.equals("NOA")) ? BARRIER : key.equals("DMG") ? MIST_ZONE : UNKNOWN;
    }

    private static EntityFactory defineShipType(long address) {
        int isNpc = API.readMemoryInt(address + 112);

        return isNpc == 1 ? NPC : isNpc == 0 && address != HeroManager.instance.address &&
                address != HeroManager.instance.pet.address ? SHIP : UNKNOWN; //fix
    }

    private static String getZoneKey(long address) {
        return API.readMemoryString(address, 136).trim();
    }

    private static String getAssetId(long address) {
        long temp = API.readMemoryLong(address, 48, 48, 16) & ByteUtils.FIX;
        return API.readMemoryString(temp, 64, 32, 24, 8, 16, 24).trim();
    }

    private static boolean isPet(long address) {
        return API.readMemoryString(address, 192, 136).trim().equals("pet");
    }

    private static boolean isShip(long address, int id) {
        int isNpc   = API.readMemoryInt(address + 112);
        int visible = API.readMemoryInt(address + 116);
        int c       = API.readMemoryInt(address + 120);
        int d       = API.readMemoryInt(address + 124);

        return id > 0 && (isNpc == 1 || isNpc == 0) &&
                (visible == 1 || visible == 0) && (c == 1 || c == 0) && d == 0;
    }
}