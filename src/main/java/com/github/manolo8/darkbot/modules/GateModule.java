package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.core.entities.Npc;
import com.github.manolo8.darkbot.core.entities.Portal;
import com.github.manolo8.darkbot.core.manager.*;
import com.github.manolo8.darkbot.core.objects.Location;
import com.github.manolo8.darkbot.core.utils.Clock;
import com.github.manolo8.darkbot.core.utils.module.Module;
import com.github.manolo8.darkbot.core.utils.module.ModuleConfig;
import com.github.manolo8.darkbot.core.utils.module.ModuleOptions;
import com.github.manolo8.darkbot.modules.helper.LootHelper;
import com.github.manolo8.darkbot.modules.helper.PetHelper;
import com.github.manolo8.darkbot.view.builder.element.component.ICharField;
import com.github.manolo8.darkbot.view.builder.element.component.ILabel;
import com.github.manolo8.darkbot.view.builder.element.component.IPetModules;

import java.util.List;

import static java.lang.Double.max;
import static java.lang.Double.min;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

@ModuleOptions("GateModule")
public class GateModule
        implements Module {

    private final LootHelper loot;
    private final PetHelper  pet;

    private final HeroManager      hero;
    private final StarManager      starManager;
    private final MapManager       mapManager;
    private final ModuleManager    moduleManager;
    private final SchedulerManager scheduler;
    private final DriveManager     drive;

    private final List<Npc>    npcs;
    private final List<Portal> portals;
    private final CommonConfig config;

    private final Clock portalClock;

    private boolean baseMap;

    private boolean repairing;

    private Npc target;
    private Npc closest;

    private boolean letLowHealth;

    private boolean top;
    private boolean corner;

    private Location attackingCenter;
    private Location kamikazeCenter;

    public GateModule(Core core, InternalConfig config) {

        this.loot = new LootHelper(config, this::shouldAttack, this::shouldStopAttack);
        this.loot.getTargetObservable().subscribe(npc -> target = npc);
        this.pet = new PetHelper(config);

        this.loot.install(core);
        this.pet.install(core);

        this.hero = core.getHeroManager();
        this.starManager = core.getStarManager();
        this.mapManager = core.getMapManager();
        this.npcs = core.getEntityManager().npcs;
        this.portals = core.getEntityManager().portals;
        this.config = core.getCommonConfig();
        this.drive = core.getDriveManager();
        this.scheduler = core.getSchedulerManager();
        this.moduleManager = core.getModuleManager();

        this.attackingCenter = new Location(4100, 4100);
        this.kamikazeCenter = new Location(9000, 6000);

        this.portalClock = new Clock();
    }

    @Override
    public void resume() {

    }

    @Override
    public boolean canRefresh() {
        return !isInGGMap();
    }

    @Override
    public void tick() {
        if (isInGGMap())
            tickOnGG();
        else
            findGGMap();
    }

    private void tickOnGG() {
        if (hasNextWave())
            goToNextWave();
        else if (hasNpcs())
            doPreKillTick();
        else
            moveGGCenter();
    }

    private boolean hasNextWave() {
        return portals.size() == 2;
    }

    private boolean hasNpcs() {
        return npcs.size() != 0;
    }

    private void doPreKillTick() {

        updateNpcInfo();

        if (checkLife())
            doRepairTick();
        else
            doKillTick();
    }

    private boolean checkLife() {

        if (repairing)
            repairing = hero.health.hpPercent() < Math.max(0.8, config.REPAIR_HP);

        return repairing || (repairing = (hero.health.hpPercent() < config.REPAIR_HP));
    }

    private void doKillTick() {
        if (shouldUseKamikaze())
            doKamikazeTick();
        else
            doDefaultTick();
    }

    private boolean shouldUseKamikaze() {

//        if (closest.npcInfo.kamikaze)
//            return npcs.stream().mapToInt(value -> value.health.hpPercent() > 0.25 ? 1 : corner ? 1 : 0).sum() > 10;

        return false;
    }

    private void doKamikazeTick() {

        scheduler.asyncSetConfig(config.RUN_CONFIG);
        pet.overrideGear(10);
        pet.check();
        target = closest;

        if (pet.isGearReady())
            waitForKamikaze();
        else
            moveSmart(kamikazeCenter, 5000);
    }

    private void waitForKamikaze() {
        drive.stop(true);
    }

    private void doDefaultTick() {

        scheduler.asyncSetConfig(config.OFFENSIVE_CONFIG);
        pet.removeOverride();
        pet.check();

        if (loot.findTarget()) {
            moveSmart(attackingCenter, target.npcInfo.radius);
            loot.doKillTick();
        }
    }

    private void moveSmart(Location base, double radius) {
        if (corner) {
            if (top)
                drive.move(424, 425);
            else
                drive.move(20576, 13075);
        } else
            move(closest.destinationInTime(100), base, radius);
    }

    private boolean shouldAttack(Npc npc) {

        if (letLowHealth)
            return true;

        double percent = npc.health.hpPercent();

        return percent > 0.5 || percent < 0.25;
    }

    private boolean shouldStopAttack(Npc npc) {

        double percent = npc.health.hpPercent();

        if (!letLowHealth && percent < 0.5 && percent > 0.25)
            return true;

        Location current = hero.location;

        double d1 = current.distance(closest.location);
        double d2 = current.distance(npc.location);

        return d1 < d2 - 100;
    }


    private void doRepairTick() {

        scheduler.asyncSetConfig(config.RUN_CONFIG);

        if (closest != null)
            move(closest.location, attackingCenter, 2000);
    }

    private void move(Location beAway, Location base, double minDistance) {

        Location now = hero.location;

        double angle = now.angle(base);

        double temp     = minDistance;
        double distance = beAway.distance(now);

        if (target.health.hpPercent() < 0.25 && target.isGoingAway(now))
            minDistance *= 0.8;

        minDistance -= (distance - minDistance);
        minDistance = max(min(minDistance, temp * 1.5), 0);

        if (minDistance > distance) {

            double radius = 1000;
            double move   = (minDistance - distance);

            move -= (distance - radius);
            move = max(min(move, (minDistance - distance) * 1.5), 0);

            if (move < 80)
                move = 80;

            double centerDistance = base.distance(now);
            double diff           = centerDistance - radius;

            if (diff > 0 && diff > minDistance) {
                radius = centerDistance - minDistance + distance;
            } else if (-diff > minDistance) {
                radius = centerDistance + minDistance - distance;
            } else {
                angle += move / radius;
            }

            drive.move(
                    base.x + cos(angle) * radius,
                    base.y + sin(angle) * radius
            );

        } else if (Math.abs(minDistance - distance) > 200) {

            angle = beAway.angle(base);

            drive.move(
                    beAway.x - cos(angle) * minDistance,
                    beAway.y - sin(angle) * minDistance
            );

        }


    }

    private void updateNpcInfo() {

        Location now                = hero.location;
        Npc      bestNpcTop         = null;
        double   bestDistanceTop    = 0;
        Npc      bestNpcBottom      = null;
        double   bestDistanceBottom = 0;

        boolean allLowAtTop         = true;
        boolean allInCornerAtTop    = true;
        boolean anyAtTop            = false;
        boolean allLowAtBottom      = true;
        boolean allInCornerAtBottom = true;
        boolean anyAtBottom         = false;

        boolean forceLowHealth = true;

        for (Npc npc : npcs) {
            double percent = npc.health.hpPercent();
            if (percent > 0.25) {
                if (mapManager.isInTop(npc.location)) {
                    allLowAtTop = false;
                } else {
                    allLowAtBottom = false;
                }
            }

            if (percent > 0.4 && npc.health.maxHp <= 40_000)
                forceLowHealth = false;
        }

        for (Npc npc : npcs) {

            Location location = npc.location;
            boolean  isInTop  = mapManager.isInTop(location);
            boolean  low      = npc.health.hpPercent() < 0.25;

            double distance = now.distance(location);

            if (isInTop) {

                anyAtTop = true;

                if (location.x != 0 || location.y != 0)
                    allInCornerAtTop = false;
                else
                    continue;

                if (!(allLowAtTop || !low))
                    continue;

                if (bestNpcTop == null || distance < bestDistanceTop) {
                    bestDistanceTop = distance;
                    bestNpcTop = npc;
                }

            } else {

                anyAtBottom = true;

                if (location.x != 21000 || location.y != 13500)
                    allInCornerAtBottom = false;
                else
                    continue;

                if (!(allLowAtBottom || !low))
                    continue;

                if (bestNpcBottom == null || distance < bestDistanceBottom) {
                    bestDistanceBottom = distance;
                    bestNpcBottom = npc;
                }
            }
        }

        if (bestNpcTop == null && anyAtTop)
            for (Npc npc : npcs)
                if (mapManager.isInTop(npc.location)) {
                    bestNpcTop = npc;
                    break;
                }

        if (bestNpcBottom == null && anyAtBottom)
            for (Npc npc : npcs)
                if (!mapManager.isInTop(npc.location)) {
                    bestNpcBottom = npc;
                    break;
                }

        if (anyAtTop) {
            top = true;
            corner = allInCornerAtTop;
            closest = bestNpcTop;
        } else {
            top = false;
            corner = allInCornerAtBottom;
            closest = bestNpcBottom;
        }

        if ((allLowAtTop && allLowAtBottom) || forceLowHealth) {
            letLowHealth = true;
        } else {

            double angle = now.angle(attackingCenter);

            if (angle < 0)
                angle += Math.PI * 2;

            letLowHealth = !(angle >= 1.8 && angle <= 3.2);
        }
    }

    private void moveGGCenter() {

        int width  = mapManager.internalWidth;
        int height = mapManager.internalHeight;

        drive.move(-500 + (width * 0.5D), -500 + (height * 0.5D));
    }

    private void goToNextWave() {
        for (Portal portal : portals)
            if (portal.type != 1)
                moveToPortal(portal);
    }

    public boolean isInGGMap() {
        return hero.map.gg;
    }

    private void findGGMap() {

        if (baseMap) {

            for (Portal portal : portals)
                if (portal.target != null && portal.target.gg)
                    moveToPortal(portal);

        } else {
            if (starManager.baseMap() == hero.map) {
                baseMap = true;
            } else {
                baseMap = false;
                moduleManager.setModule(MapModule.class).setTarget(starManager.baseMap());
            }
        }

    }

    private void moveToPortal(Portal portal) {

        drive.move(portal);

        if (hero.distance(portal) < 300 && portalClock.isBiggerThenReset(5000)) {
            scheduler.asyncKeyboardClick('j');
            Location loc = hero.location;
            drive.move(loc.x + Math.random() * 50, loc.y + Math.random() * 50);
        }
    }

    private static class InternalConfig
            implements ModuleConfig,
            LootHelper.LootHelperConfig,
            PetHelper.PetHelperConfig {

        @ILabel("Ammo key")
        @ICharField
        public char AMMO_KEY = '3';
        @ILabel("Pet gear")
        @IPetModules
        public int  petGearId;

        @Override
        public char ammoKey() {
            return AMMO_KEY;
        }

        @Override
        public boolean autoSab() {
            return false;
        }

        @Override
        public char autoSabKey() {
            return 0;
        }

        @Override
        public int gearId() {
            return petGearId;
        }
    }
}
