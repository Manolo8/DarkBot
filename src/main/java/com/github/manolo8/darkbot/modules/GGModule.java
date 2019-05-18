package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.config.Config;
import com.github.manolo8.darkbot.core.entities.Npc;
import com.github.manolo8.darkbot.core.itf.Module;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.core.utils.Drive;
import com.github.manolo8.darkbot.core.utils.Location;
import com.github.manolo8.darkbot.modules.utils.NpcAttacker;

import java.util.Comparator;
import java.util.List;

import static java.lang.Double.max;
import static java.lang.Double.min;

public class GGModule implements Module {
    //V1 BETA 6
    private static final double TAU = Math.PI * 2;

    private Main main;
    private Config config;
    private List<Npc> npcs;
    private HeroManager hero;
    private Drive drive;
    private Location direction;
    private int radiusFix;

    private boolean repairing;
    private int rangeNPCFix = 0;
    private long lastCheck = System.currentTimeMillis();
    private int lasNpcHealth = 0;
    private int lasPlayerHealth = 0;
    NpcAttacker attack;

    public GGModule() {
    }

    public void install(Main main) {
        this.main = main;
        this.config = main.config;
        this.attack = new NpcAttacker(main);

        this.hero = main.hero;
        this.drive = main.hero.drive;

        this.npcs = main.mapManager.entities.npcs;
    }

    @Override
    public boolean canRefresh() {
        return this.attack.target == null;
    }

    @Override
    public String status() {
        return this.repairing ? "Repairing" :
                this.attack.hasTarget() ? this.attack.status() : "Roaming" + " | NPCs: " + this.npcs.size();
    }

    @Override
    public void tick() {
        if (this.main.hero.map.gg) {
            if (findTarget()) {
                this.hero.attackMode();
                this.attack.doKillTargetTick();
                ignoreInvalidTarget();
                moveToAnSafePosition();
            } else if (!this.main.mapManager.entities.portals.isEmpty()) {
                this.hero.runMode();
                this.main.setModule(new MapModule()).setTarget(main.starManager.byId(main.mapManager.entities.portals.get(0).id));
            } else if (!this.drive.isMoving()) {
                this.drive.moveRandom();
                this.hero.runMode();
            }
        } else if (this.main.hero.map.name == "1-1" || this.main.hero.map.name == "2-1" || this.main.hero.map.name == "3-1") {
            this.hero.roamMode();
            for (int i=0; i < this.main.mapManager.entities.portals.size();i++){
                if (this.main.mapManager.entities.portals.get(i).target.gg){
                    this.main.setModule(new MapModule()).setTarget(this.main.starManager.byId(this.main.mapManager.entities.portals.get(i).id));
                    return;
                }
            }
        } else {
            this.hero.roamMode();
            this.main.setModule(new MapModule()).setTarget(this.main.starManager.byName("Home Map"));
        }
    }

    private boolean findTarget() {
        if (this.attack.target == null || this.attack.target.removed) {
            if (!npcs.isEmpty()) {
                if (!allLowLife()) {
                    this.attack.target = bestNpc();
                } else {
                    this.attack.target = closestNpc(this.hero.locationInfo.now);
                }
            } else {
                this.attack.target = null;
            }
        } else if (this.attack.target.health.hpPercent() < 0.25 && !allLowLife()) {
            this.attack.target = null;
        }
        return this.attack.target != null;
    }

    public boolean isLowHealh(Npc npc){
        return npc.health.hpPercent() < 0.25;
    }

    private boolean allLowLife(){
        int npcsLowLife = 0;

        for (int i=0; i < this.npcs.size();i++) {
            if (isLowHealh(this.npcs.get(i))) {
                npcsLowLife++;
            }
        }

        return npcsLowLife >= this.npcs.size();
    }

    private void moveToAnSafePosition() {
        Npc target = this.attack.target;

        if (!this.hero.drive.isMoving()) this.direction = null;

        Location heroLoc = this.hero.locationInfo.now;

        if (target == null || target.locationInfo == null) return;

        Location targetLoc = target.locationInfo.destinationInTime(400);
        double angle = targetLoc.angle(heroLoc), distance = heroLoc.distance(targetLoc), radius = target.npcInfo.radius;;

        dinamicNPCRange(distance);
        if (radius < 500) {
            radius = 550;
        }
        radius += this.rangeNPCFix;

        if (distance > radius) {
            this.radiusFix -= (distance - radius) / 2;
            this.radiusFix = (int) max(this.radiusFix, -target.npcInfo.radius / 2);
        } else {
            this.radiusFix += (radius - distance) / 6;
            this.radiusFix = (int) min(radiusFix, target.npcInfo.radius / 2);
        }
        distance = (radius += radiusFix);
        angle += Math.max((this.hero.shipInfo.speed * 0.625) + (min(200, target.locationInfo.speed) * 0.625)
                - heroLoc.distance(Location.of(targetLoc, angle, radius)), 0) / radius;

        this.direction = Location.of(targetLoc, angle, distance);
        while (!this.drive.canMove(this.direction) && distance < 10000)
            this.direction.toAngle(targetLoc, angle += 0.3, distance += 2);
        if (distance >= 10000) this.direction.toAngle(targetLoc, angle, 500);

        drive.move(direction);
    }

    private void dinamicNPCRange(double distance){
        if (this.hero.health.hpPercent() <= this.config.GENERAL.SAFETY.REPAIR_HP){
            this.rangeNPCFix = 1000;
            this.repairing = true;
        } else if  (this.hero.health.hpPercent() >= this.config.GENERAL.SAFETY.REPAIR_TO_HP){
            this.rangeNPCFix = 0;
            this.repairing = false;
        }

        if (this.lastCheck <= System.currentTimeMillis()-8000 && distance <= 1000) {
            if (this.lasPlayerHealth > this.hero.health.hp && this.rangeNPCFix < 500) {
                this.rangeNPCFix += 50;
            } else if (this.lasNpcHealth == this.attack.target.health.hp) {
                this.rangeNPCFix -= 50;
            }
            this.lasPlayerHealth =  this.hero.health.hp;
            this.lasNpcHealth = this.attack.target.health.hp;
            this.lastCheck = System.currentTimeMillis();
        }
    }

    private Npc closestNpc(Location location) {
        return this.npcs.stream()
                .min(Comparator.<Npc>comparingInt(n -> n.npcInfo.priority)
                        .thenComparing(n -> n.health.hpPercent())
                        .thenComparing(n -> n.locationInfo.now.distance(location))).orElse(null);
    }

    private Npc bestNpc() {
        return this.npcs.stream()
                .max(Comparator.<Npc>comparingDouble(n -> n.health.hpPercent())
                        .thenComparing(n -> (n.npcInfo.priority * -1))).orElse(null);
    }

    private void ignoreInvalidTarget() {
        if (!this.main.mapManager.isTarget(this.attack.target))
            return;

        if (this.main.mapManager.isTarget(this.attack.target) && (this.attack.target.health.hpPercent() < 0.25)) {
            if (!allLowLife()) {
                if(isLowHealh(this.attack.target)){
                    this.hero.setTarget(this.attack.target = null);
                    return;
                }
            }
        } else if ((this.attack.target.health.shIncreasedIn(1000) || this.attack.target.health.shieldPercent() > 0.99) && !this.repairing){
            this.attack.target.setTimerTo(5000);
            this.hero.setTarget(this.attack.target = null);
        }
    }

}

