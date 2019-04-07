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

    public Npc target;
    private boolean shooting;
    private long clickDelay;
    private long lastNpc = System.currentTimeMillis();
    private long timeSinceNpc;
    private int currentTarget = 0;
    private CollectorModule collectorModule;
    private boolean repairing;
    private boolean jump;
    private long waiting;

    @Override
    public void install(Main main) {
        this.collectorModule = new CollectorModule();
        this.main = main;
        this.config = main.config;

        this.hero = main.hero;
        this.drive = main.hero.drive;

        this.npcs = main.mapManager.entities.npcs;
        this.boxes = main.mapManager.entities.boxes;
    }

    @Override
    public boolean canRefresh() {
        return timeSinceNpc > 2 * 60 * 1000 && (timeSinceNpc <  15 * 60 * 1000 || timeSinceNpc > 25 * 60 * 1000);
    }

    @Override
    public String status() {
        timeSinceNpc = System.currentTimeMillis() - this.lastNpc;
        return timeSinceNpc > 1000 ? "Waiting: " + Time.toString(timeSinceNpc) : null;
    }

    @Override
    public void tick() {
        if (System.currentTimeMillis() < waiting) return;

        //if (checkDangerousAndCurrentMap()) {
            if (findTarget()) {
                hero.attackMode();
                lastNpc = System.currentTimeMillis();
                setTargetAndTryStartLaserAttack();
                removeLowHeal();
                moveToAnSafePosition();
            } else if (target == null) {
                hero.roamMode();
                collectorModule.findBox();

                if(collectorModule.current != null) {
                    if (!collectorModule.tryCollectNearestBox() && (!drive.isMoving() || drive.isOutOfMap())) {
                        drive.moveRandom();
                    }
                }
            } else if (timeSinceNpc > 10000 && !hero.drive.isMoving()) hero.attackMode();
        //}
    }

    private boolean findTarget() {
        if (target == null || target.removed) {
            if (!npcs.isEmpty()) {
                for ( int i = 0; npcs.size()<i;i++ ){
                    if(!npcs.get(i).isLowHealh()){
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
                if(!target.isLowHealh()){
                    npcs.remove(currentTarget);
                    target.setLowHealh();
                    npcs.add(target);
                    target = null;
                }
            }
        }
    }

    private boolean allLowLife(){
        boolean alllowl = true;

        for (int i=0; i < npcs.size();i++) {
            if (!npcs.get(i).isLowHealh()) {
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

    /*boolean checkDangerousAndCurrentMap() {
        if (this.config.WORKING_MAP != this.hero.map.id && !main.mapManager.entities.portals.isEmpty()) {
            this.hero.runMode();
            repairing = true;
            jump = false;
            this.main.setModule(new MapModule()).setTargetAndBack(this.main.starManager.byId(this.main.config.WORKING_MAP));
            return false;
        }

        if (jump && escaping != null) {
            this.hero.runMode();
            if (escaping.locationInfo.distance(this.hero) < 250.0) hero.jumpPortal(escaping);
            else this.drive.move(escaping);
            return false;
        }

        boolean underAttack = this.isUnderAttack();
        boolean lowHp = this.hero.health.hpPercent() < this.config.GENERAL.SAFETY.REPAIR_HP ||
                (this.hero.health.hpPercent() < this.config.GENERAL.SAFETY.REPAIR_HP_NO_NPC &&
                        (this.target == null || this.target.removed || this.target.health.hp == 0 || this.target.health.hpPercent() > 0.8));

        if (lowHp || hasEnemies()) {
            escaping = this.main.starManager.next(this.hero.map, this.hero.locationInfo, this.hero.map);
            if (escaping == null) return true; // No place to run, don't even try.

            this.hero.runMode();
            if (underAttack && !repairing) Main.API.keyboardClick(config.GENERAL.SAFETY.SHIP_ABILITY);

            jump |= config.LOOT.SAFETY.JUMP_PORTALS && underAttack;
            repairing |= underAttack || lowHp || !this.config.LOOT.SAFETY.STOP_RUNNING_NO_SIGHT;

            if (escaping.locationInfo.distance(this.hero) > 250.0) this.drive.move(escaping);
            else if (lowHp && hero.health.hpDecreasedIn(100)) jump |= config.LOOT.SAFETY.JUMP_PORTALS;
            return false;
        }
        repairing &= this.hero.health.hpPercent() < this.config.GENERAL.SAFETY.REPAIR_TO_HP;
        if (repairing) return false;
        escaping = null;
        return true;
    }*/

}
