package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.config.Config;
import com.github.manolo8.darkbot.core.itf.Module;
import com.github.manolo8.darkbot.core.entities.Npc;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.core.manager.MapManager;
import com.github.manolo8.darkbot.core.objects.LocationInfo;
import com.github.manolo8.darkbot.core.utils.Drive;

import java.util.List;

import static com.github.manolo8.darkbot.Main.API;
import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;


public class GateModule implements Module {

   private MapManager mapManager;
   private HeroManager hero;

   private List<Npc> npcs;

   private long clickDelay;
   private long laserTime;
   private long locker;
   private Config config;
   private Drive drive;
   private Npc current;
   private Main main;

   private int times;
   private boolean direction;
   private boolean repairing;
   private boolean locked;
   private boolean sab;

   private LocationInfo safe;

   public GateModule() {
       safe = new LocationInfo(8000, 5500);
   }

   @Override
   public void install(Main main) {
       this.main = main;
       drive = main.hero.drive;
       config = main.config;
       npcs = main.mapManager.entities.npcs;
       hero = main.hero;
       mapManager = main.mapManager;
   }

   @Override
   public boolean canRefresh() {
       return false;
   }

   @Override
   public void tick() {

       if (isGGMap()) {
           Npc closest = closest(hero.locationInfo);

           if (closest != null) {

               LocationInfo locationHero = hero.locationInfo;
               LocationInfo locationNpc = closest.locationInfo;

               if (hero.health.hpPercent() < this.config.GENERAL.SAFETY.REPAIR_HP || repairing) {

                   if (!repairing) repairing = true;
                   else repairing = (hero.health.hpPercent() < this.config.GENERAL.SAFETY.REPAIR_TO_HP);

                   hero.runMode();
                   move(safe, 500, 1000);

               } else {

                   hero.attackMode();

                   if (closest.health.hpPercent() < 0.25 && System.currentTimeMillis() - locker > 5000) {
                       direction = !direction;
                       locker = System.currentTimeMillis();
                   }

                   if (locationNpc.isMoving() || locationNpc.distance(locationHero) > 800) {
                       move(locationNpc, hero.shipInfo.speed * 0.625, closest.npcInfo.radius + locationNpc.speed * 0.625);
                   }

                   if (current == null
                           || current.removed
                           || locationHero.distance(current) - locationHero.distance(locationNpc) > 150) {
                       current = closest;
                   }

                   tickNormalMode();
               }

           } else {
               drive.move(9000, 6500);
           }
       } else {
           //this.main.setModule(new MapModule()).setTargetAndBack(this.main.starManager.fromId(this.main.config.WORKING_MAP));
       }
   }

   private boolean isGGMap() {
       int id = hero.map.id;
       return id == 51 || id == 52 || id == 53 || id == 75;
   }

   private void tickNormalMode() {
       if (main.mapManager.isTarget(current)) {
           if (checkIfIsAttackingAndCanContinue()) {
               checkSab();
           }
       } else {
           setTargetAndTryStartLaserAttack();
       }
   }

    private void setTargetAndTryStartLaserAttack() {
        if (hero.locationInfo.distance(current) < 800 && System.currentTimeMillis() - clickDelay > 1000) {

            hero.setTarget(current);

            setRadiusAndClick(1);
            API.keyboardClick(getAttackKey());
            clickDelay = System.currentTimeMillis();
            locked = true;
            times = 0;
        } else if (!locked) {
            current = null;
        }

    }

    private boolean checkIfIsAttackingAndCanContinue() {

        long laser = System.currentTimeMillis() - laserTime;

        boolean attacking = hero.isAttacking(current);
        boolean bugged = (!current.health.isDecreasedIn(1000) && laser > 1000);

        if ((!attacking || bugged) && hero.locationInfo.distance(current) < 800 && laser > 1500 + times * 10000) {
            setRadiusAndClick(2);
            times++;
            laserTime = System.currentTimeMillis();
        }

        return true;
    }

    private void checkSab() {
        if (config.AUTO_SAB && hero.health.shieldPercent() < config.LOOT.SAB_PERCENT
                && current.health.shield > config.LOOT.SAB_NPC_AMOUNT) {

            if (!sab) {
                API.keyboardClick(config.AUTO_SAB_KEY);
                sab = true;
            }

        } else if (sab) {
            API.keyboardClick(getAttackKey());
            sab = false;
        }
    }

    private void setRadiusAndClick(int times) {
        current.clickable.setRadius(800);

        drive.clickCenter(times);

        current.clickable.setRadius(0);
    }

   private void move(LocationInfo point, double distance, double radius) {

       LocationInfo current = hero.locationInfo;

       double angle = current.angle;

       LocationInfo target = new LocationInfo(
               point.now.x - cos(angle) * radius,
               point.now.y - sin(angle) * radius
       );

       if (distance - target.distance(current) > 0) {
           double move = distance / radius;
           angle += direction ? move : -move;
           target.now.x = point.now.x - cos(angle) * radius;
           target.now.y = point.now.y - sin(angle) * radius;
       }

       drive.move(target.now);
   }

//   private boolean hasNPC() {
//       return !npcs.hasLineOfSight();
//   }

   private Npc closest(LocationInfo point) {
       double distance = -1;
       Npc closest = null;

       for (Npc npc : npcs) {
           double distanceCurrent = point.distance(npc.locationInfo);
           if (distance == -1 || distanceCurrent < distance) {
               distance = distanceCurrent;
               closest = npc;
           }
       }

       return closest;
   }

    private char getAttackKey() {
        return this.current == null || this.current.npcInfo.attackKey == null ?
                this.config.AMMO_KEY : this.current.npcInfo.attackKey;
    }
}
