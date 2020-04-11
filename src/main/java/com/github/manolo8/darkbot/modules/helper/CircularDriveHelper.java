package com.github.manolo8.darkbot.modules.helper;

import com.github.manolo8.darkbot.core.entities.Npc;
import com.github.manolo8.darkbot.core.manager.Core;
import com.github.manolo8.darkbot.core.manager.DriveManager;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.core.manager.MapManager;
import com.github.manolo8.darkbot.core.objects.Location;
import com.github.manolo8.darkbot.core.utils.Observable;
import com.github.manolo8.darkbot.core.utils.module.ModuleHelper;

import java.util.Comparator;
import java.util.List;

import static java.lang.Double.max;
import static java.lang.Double.min;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public final class CircularDriveHelper
        implements ModuleHelper {

    private MapManager   mapManager;
    private DriveManager drive;
    private HeroManager  hero;

    private List<Npc> npcs;

    private Npc     target;
    private boolean last;

    private double smoothRadius;

    public CircularDriveHelper() {
        //NoTarget
    }

    public CircularDriveHelper(Observable<Npc> targetObservable) {
        targetObservable.subscribe(npc -> this.target = npc);
    }

    @Override
    public void install(Core core) {
        this.mapManager = core.getMapManager();
        this.drive = core.getDriveManager();
        this.hero = core.getHeroManager();

        this.npcs = core.getEntityManager().npcs;
    }

    public void moveAway() {
        npcs.stream()
                .min(Comparator.comparingDouble(value -> value.distance(hero.location)))
                .ifPresent(closest -> stayAwayFromLocation(closest.location));
    }

    private void stayAwayFromLocation(Location awayLocation) {

        Location heroLocation = hero.location;

        double angle        = awayLocation.angle(heroLocation);
        double moveDistance = hero.shipInfo.speed;
        double distance     = 1600;

        Location target = new Location(
                awayLocation.x - cos(angle) * distance,
                awayLocation.y - StrictMath.sin(angle) * distance
        );

        moveDistance = moveDistance - target.distance(heroLocation);

        if (moveDistance > 0) {

            double move = moveDistance / 3000;

            angle += findBestDirection(heroLocation, angle, distance) ? +move : -move;

            target.x = awayLocation.x - cos(angle) * distance;
            target.y = awayLocation.y - StrictMath.sin(angle) * distance;
        }

        drive.move(target);
    }


    public void moveToAnSafePosition() {

        Location heroLocation   = hero.location;
        Location targetLocation = target.destinationInTime(200);
        double   npcRadius      = target.npcInfo.radius;
        double   hp             = target.health.hpPercent();
        double   distance       = heroLocation.distance(targetLocation);
        double   angle          = targetLocation.angle(heroLocation);

        if (hp == 1.0)
            npcRadius = npcRadius / 1.5;
        else if (target.isGoingAway(heroLocation))
            npcRadius *= 0.80;

        double radius = npcRadius;

        radius -= (distance - radius);

        double temp = radius;

        radius = (smoothRadius * 10D + radius) / 11D;
        radius = max(min(radius, npcRadius * 1.5), 1);
        smoothRadius = radius;

        double x = targetLocation.x - cos(angle) * radius;
        double y = targetLocation.y - sin(angle) * radius;

        double moveDistance = 50 + hero.shipInfo.speed * 0.625;

        if (!mapManager.isOutOfMap(heroLocation))
            moveDistance -= heroLocation.distance(x, y);

        if (target.npcInfo.circle && moveDistance > 0) {

            double add = moveDistance / radius;

            angle += findBestDirection(heroLocation, angle, radius) ? add : -add;

            x = targetLocation.x - cos(angle) * radius;
            y = targetLocation.y - sin(angle) * radius;
        }

        drive.move(x, y);
    }

    private boolean findBestDirection(Location lh, double angle, double radius) {

        double part = Math.PI / 5;
        double right = calcDirection(new Location(
                lh.x - cos(angle + part) * radius,
                lh.y - sin(angle + part) * radius
        ), true);
        double left = calcDirection(new Location(
                lh.x - cos(angle - part) * radius,
                lh.y - sin(angle - part) * radius
        ), false);

        return (last = (right == left) ? last : right > left);
    }

    private double calcDirection(Location loc, boolean direction) {
        double calc = 0;

        if (!drive.canMove(loc))
            calc -= 150;
        else
            calc += 150;

        calc -= mapManager.distanceOutOfMap(loc) * (last == direction ? 1 : 2);

        for (Npc npc : npcs) {

            if (npc == target)
                continue;
            if (npc.location.distance(hero.location) > 600)
                continue;

            double distance  = npc.location.distance(loc);
            double npcRadius = npc.npcInfo.radius;

            calc -= ((npcRadius / distance) * 3) + 0.2;
        }

        return calc;
    }
}