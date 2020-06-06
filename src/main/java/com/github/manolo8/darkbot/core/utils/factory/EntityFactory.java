package com.github.manolo8.darkbot.core.utils.factory;

import com.github.manolo8.darkbot.core.entities.Barrier;
import com.github.manolo8.darkbot.core.entities.BasePoint;
import com.github.manolo8.darkbot.core.entities.BattleStation;
import com.github.manolo8.darkbot.core.entities.Box;
import com.github.manolo8.darkbot.core.entities.Entity;
import com.github.manolo8.darkbot.core.entities.MapNpc;
import com.github.manolo8.darkbot.core.entities.NoCloack;
import com.github.manolo8.darkbot.core.entities.Npc;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.utils.ByteUtils;
import org.intellij.lang.annotations.Language;

import java.util.function.Function;
import java.util.regex.Pattern;

import static com.github.manolo8.darkbot.Main.API;

//Maybe split them same as classes in darkorbit?
public enum EntityFactory {
    UNKNOWN     (Entity::new),
    //Ship and Npc have same class
    SHIP        (Ship::new),
    NPC         (Npc::new),
    PORTAL      ("[0-9]+$"),
    BOX         (Box::new,           "box_.*"),
    ORE         (Box::new,           "ore_.*"),
    MINE        (Box::new,           "mine_.*"),
    NPC_BEACON  (MapNpc::new,        "npc-beacon.*"),
    LOW_RELAY   (MapNpc::new,        "relay"),
    //Barrier and Mist zone have same class
    BARRIER     (Barrier::new,       "NOA|DMG"),
    MIST_ZONE   (NoCloack::new),
    WRECK_MODULE(BattleStation::new, "wreck"),
    ASTEROID    (BattleStation::new, "asteroid|cbs-construction"),
    MODULE      (BattleStation::new, "module_.*|module-construction"),
    STATION     (BattleStation::new, "battleStation"),
    BASE_POINT  (BasePoint::new,     "(questgiver|repairstation|headquarters|refinery|hangar|turret|station)_.*");

    private Pattern pattern;
    private Function<Integer, ? extends Entity> constructor;

    EntityFactory(Function<Integer, ? extends Entity> constructor) { this.constructor = constructor; }
    EntityFactory(@Language("RegExp") String regex) { this.pattern = Pattern.compile(regex); }
    EntityFactory(Function<Integer, ? extends Entity> constructor, @Language("RegExp") String regex) {
        this.pattern     = Pattern.compile(regex);
        this.constructor = constructor;
    }

    public Entity createEntity(int id) {
        return constructor == null ? new Entity(id) : constructor.apply(id);
    }

    public static EntityFactory find(long address, int id) {
        String assetId = getAssetId(address);
        System.out.println(assetId + " | " + getZoneKey(address));

        for (EntityFactory type : EntityFactory.values()) {
            if (type.pattern == null) continue;
            if (type.pattern.matcher(type == BARRIER ?
                    getZoneKey(address) : assetId).matches()) return type;
        }

        return isShip(address, id) ? SHIP : UNKNOWN;
    }

    private static String getZoneKey(long address) {
        return API.readMemoryString(address, 136);
    }

    private static String getAssetId(long address) {
        long temp = API.readMemoryLong(address, 48, 48, 16) & ByteUtils.FIX;
        return API.readMemoryString(temp, 64, 32, 24, 8, 16, 24).trim();
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