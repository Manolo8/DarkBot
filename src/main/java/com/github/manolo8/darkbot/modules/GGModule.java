package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.config.Config;
import com.github.manolo8.darkbot.core.entities.Box;
import com.github.manolo8.darkbot.core.entities.Npc;
import com.github.manolo8.darkbot.core.itf.Module;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.core.objects.LocationInfo;
import com.github.manolo8.darkbot.core.utils.Drive;
import com.github.manolo8.darkbot.core.utils.Location;
import com.github.manolo8.darkbot.utils.Time;

import java.util.Comparator;
import java.util.List;

import static com.github.manolo8.darkbot.Main.API;
import static java.lang.Double.max;
import static java.lang.Double.min;

public class GGModule implements Module {

    private static final double TAU = Math.PI * 2;

    private Main main;
    private Config config;
    private List<Npc> npcs;
    private List<Box> boxes;
    private HeroManager hero;
    private Drive drive;
    private Location direction;
    private int radiusFix;
    private Long ability;
    private boolean sab;

    public Npc target;
    private boolean shooting;
    private long clickDelay;
    private long lastNpc = System.currentTimeMillis();
    private long timeSinceNpc;
    private int currentTarget = 0;
    private final CollectorModule collectorModule;
    private boolean repairing;
    private boolean jump;
    private long waiting;

    public GGModule(){
        this.collectorModule = new CollectorModule();
    }

    @Override

    public void install(Main main) {
        this.main = main;
        this.config = main.config;

        this.hero = main.hero;
        this.drive = main.hero.drive;

        this.npcs = main.mapManager.entities.npcs;
        this.boxes = main.mapManager.entities.boxes;
    }

    @Override
    public boolean canRefresh() {
        if(collectorModule.isNotWaiting()) {
            return target == null;
        }

        return false;
    }

    @Override
    public String status() {
        return "Loot: " + lootStatus() + " - Collect: " + collectorModule.status();
    }

    private String lootStatus() {
        return jump ? "Jumping port" : repairing ? "Repairing" :
                target != null ? "Killing npc" + (shooting ? " S" : "") + (ability != null ? " A" : "") + (sab ? " SAB" : "")
                        : "Roaming";
    }

    @Override
    public void tick() {
        if (System.currentTimeMillis() < waiting) return;

        if (checkCurrentMap()) {

            if (findTarget()) {
                hero.attackMode();
                lastNpc = System.currentTimeMillis();
                setTargetAndTryStartLaserAttack();
                removeLowHeal();
                moveToAnSafePosition();
            } /*else if (target == null) {
                hero.roamMode();
                collectorModule.findBox();

                if(collectorModule.current != null) {
                    if (!collectorModule.tryCollectNearestBox() && (!drive.isMoving() || drive.isOutOfMap())) {
                        drive.moveRandom();
                    }
                }
            }*/ else if (timeSinceNpc > 10000 && !hero.drive.isMoving()) hero.attackMode();
        }
    }

    private boolean findTarget() {
        if (target == null || target.removed) {
            if (!npcs.isEmpty()) {
                for ( int i = 0; npcs.size()<i;i++ ){
                    if(!isLowHealh(npcs.get(i))){
                        target = npcs.get(i);
                        currentTarget = i;
                    }
                }
                target = npcs.get(0);
            } else {
                target = null;
            }
        } else if (target.health.hpPercent() < 0.25 && !allLowLife()) {
            target = null;
        }
        return target != null;
    }

    private void removeLowHeal() {
        if (main.mapManager.isTarget(target) && (target.health.hpPercent() < 0.25)) {
            if (!allLowLife()) {
                if(!isLowHealh(target)){
                    npcs.remove(currentTarget);
                    npcs.add(target);
                    target = null;
                }
            }
        }
    }

    public boolean isLowHealh(Npc npc){
        if (npc.health.hpPercent() < 0.25)  return true;

        return false;
    }

    private boolean allLowLife(){
        boolean alllowl = true;

        for (int i=0; i < npcs.size();i++) {
            if (!isLowHealh(npcs.get(i))) {
                alllowl = false;
            }
        }

        return alllowl;
    }

    private void setTargetAndTryStartLaserAttack() {
        boolean locked = main.mapManager.isTarget(target);
        double distance = hero.locationInfo.distance(target);
        if (locked && !shooting) {
            if (distance > 550) return;
            API.keyboardClick(config.LOOT.AMMO_KEY);
            shooting = true;
            if (target.health.maxHp > 0) API.keyboardClick(config.EVENT.SHIP_ABILITY);
            return;
        }
        if (locked) return;

        if (hero.locationInfo.distance(target) < 750 && System.currentTimeMillis() - clickDelay > 1000) {
            hero.setTarget(target);
            setRadiusAndClick();
            clickDelay = System.currentTimeMillis();
            shooting = false;
        }
    }

    private void setRadiusAndClick() {
        target.clickable.setRadius(800);
        drive.clickCenter(true, target.locationInfo.now);
        target.clickable.setRadius(0);
    }

    private void moveToAnSafePosition() {

        if (!hero.drive.isMoving()) direction = null;
        Location heroLoc = hero.locationInfo.now;
        if (target == null || target.locationInfo == null) return;
        Location targetLoc = target.locationInfo.destinationInTime(400);

        double angle = targetLoc.angle(heroLoc), distance = heroLoc.distance(targetLoc),
                angleDiff = Math.abs(target.locationInfo.angle - heroLoc.angle(target.locationInfo.now)) % TAU;

        double radius = target.npcInfo.radius;
        if (radius < 500) {
            radius = 550;
        }

        if (distance > radius) {
            radiusFix -= (distance - radius) / 2;
            radiusFix = (int) max(radiusFix, -target.npcInfo.radius / 2);
        } else {
            radiusFix += (radius - distance) / 6;
            radiusFix = (int) min(radiusFix, target.npcInfo.radius / 2);
        }
        distance = (radius += radiusFix);
        // Moved distance + speed - distance to chosen radius same angle, divided by radius
        angle += Math.max((hero.shipInfo.speed * 0.625) + (min(200, target.locationInfo.speed) * 0.625)
                - heroLoc.distance(Location.of(targetLoc, angle, radius)), 0) / radius;

        direction = Location.of(targetLoc, angle, distance);
        while (!drive.canMove(direction) && distance < 10000)
            direction.toAngle(targetLoc, angle += 0.3, distance += 2);
        if (distance >= 10000) direction.toAngle(targetLoc, angle, 500);

        drive.move(direction);
    }

    private boolean checkCurrentMap() {
        boolean mapWrong = config.GENERAL.WORKING_MAP != hero.map.id;

        if (mapWrong) {

            hero.runMode();

            main.setModule(new MapModule()).setTarget(main.starManager.byId(main.config.GENERAL.WORKING_MAP));

            return false;
        }

        return true;
    }

}
