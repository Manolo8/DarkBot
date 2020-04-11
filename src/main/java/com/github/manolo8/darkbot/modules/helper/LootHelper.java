package com.github.manolo8.darkbot.modules.helper;

import com.github.manolo8.darkbot.core.entities.Npc;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.manager.*;
import com.github.manolo8.darkbot.core.objects.Location;
import com.github.manolo8.darkbot.core.utils.Clock;
import com.github.manolo8.darkbot.core.utils.Observable;
import com.github.manolo8.darkbot.core.utils.module.ModuleHelper;
import com.github.manolo8.darkbot.modules.itf.Filter;

import java.util.Comparator;
import java.util.List;

public final class LootHelper
        implements ModuleHelper {

    private final Filter<Npc> shouldKill;
    private final Filter<Npc> shouldStopAttack;
    private final Clock       attackClock;
    private final Clock       selectClock;

    private MapManager       mapManager;
    private SchedulerManager scheduler;
    private HeroManager      hero;
    private ItemManager      itemManager;

    private final LootHelperConfig config;

    private List<Ship> ships;
    private List<Npc>  npcs;

    private final Observable<Npc> targetObservable;
    private       Npc             target;
    private       boolean         sab;
    private       boolean         locked;

    public LootHelper(LootHelperConfig config, Filter<Npc> shouldKill, Filter<Npc> shouldStopAttack) {
        this.config = config;
        this.targetObservable = new Observable<>();
        this.shouldKill = shouldKill;
        this.shouldStopAttack = shouldStopAttack;
        this.attackClock = new Clock();
        this.selectClock = new Clock();
    }

    public LootHelper(LootHelperConfig config) {
        this(config, element -> element.npcInfo.kill, element -> false);
    }

    public void install(Core core) {
        this.mapManager = core.getMapManager();
        this.scheduler = core.getSchedulerManager();
        this.hero = core.getHeroManager();
        this.itemManager = core.getItemManager();

        this.ships = core.getEntityManager().ships;
        this.npcs = core.getEntityManager().npcs;
    }

    public boolean findTarget() {

        if (target == null || target.removed || target.isInvalid())
            setNewTarget(bestNpc(hero.location));
        else if (target != null && shouldStopAttack.test(target))
            setNewTarget(null);

        return target != null;
    }

    public void doKillTick() {

        if (mapManager.isTarget(target)) {
            if (mapManager.isCurrentTargetOwned()) {
                if (!locked)
                    startAttack();
                else
                    checkIfIsAttacking();
                checkSab();
            } else {
                target.setTimerTo(0, 45000);
                setNewTarget(null);
            }
        } else
            selectTarget();
    }

    private void setNewTarget(Npc npc) {
        targetObservable.next(npc);
        target = npc;
    }

    private void checkSab() {
        if (config.autoSab() && hero.health.shieldPercent() < 0.6 && target.health.shield > 12000) {
            if (!sab) {
                sab = true;
                locked = false;
            }
        } else if (sab) {
            sab = false;
            locked = false;
        }
    }

    private void checkIfIsAttacking() {

        if (!itemManager.selectedLaser.active
                && attackClock.isBiggerThenReset(1000)) {
            locked = false;
        }
    }

    private void startAttack() {
        scheduler.asyncKeyboardClick(ammoKey());
        attackClock.reset();
        locked = true;
    }

    private void selectTarget() {
        if (isAttackedByOtherPlayer(target)) {
            target.setTimerTo(0, 30_000);
            setNewTarget(null);
        } else if (hero.distance(target) < 1000
                && selectClock.isBiggerThenReset(500)) {

            hero.setTarget(target);
            scheduler.asyncSelectTarget(target, false);
            locked = false;
        }
    }


    private Npc bestNpc(Location location) {
        return npcs.stream()
                .filter(this::shouldKill)
                .min(Comparator.<Npc>comparingInt(npc -> npc.npcInfo.priority).thenComparingDouble(value -> value.distance(location)))
                .orElse(null);
    }

    public Observable<Npc> getTargetObservable() {
        return targetObservable;
    }

    public Npc getTarget() {
        return target;
    }

    public boolean isAttacking() {
        return target != null;
    }

    private boolean isAttackedByOtherPlayer(Ship entity) {

        for (Ship ship : ships)
            if (ship.isAttacking(entity))
                return true;

        return false;
    }

    private boolean shouldKill(Npc npc) {

        if (shouldKill.test(npc)
                && !npc.isInTimer(0)) {
            if (isAttackedByOtherPlayer(npc))
                npc.setTimerTo(0, 40_000);
            else
                return true;
        }

        return false;
    }

    private char ammoKey() {

        char temp = sab ? config.autoSabKey() : target.npcInfo.ammo;

        return temp == '\0' ? config.ammoKey() : temp;
    }

    public interface LootHelperConfig {

        char ammoKey();

        boolean autoSab();

        char autoSabKey();
    }
}
