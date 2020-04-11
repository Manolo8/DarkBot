package com.github.manolo8.darkbot.core.utils.pathfinder;

import com.github.manolo8.darkbot.core.itf.Obstacle;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.core.objects.Location;

import java.util.*;

public class PathFinder {

    final        LinkedList<PathPoint> paths;
    public final Set<PathPoint>        points;
    final        List<Obstacle>        obstacles;
    final        HeroManager           hero;
    final        List<Area>            avoidAreas;

    private int  lastSize;
    private long lastMapChanged;

    public PathFinder(HeroManager hero, List<Obstacle> obstacles) {
        this.hero = hero;

        this.paths = new LinkedList<>();
        this.obstacles = obstacles;
        this.points = new HashSet<>();
        this.avoidAreas = new ArrayList<>();
    }

    public Location current() {
        PathPoint point = paths.getFirst();

        return new Location(point.x, point.y);
    }

    public void currentCompleted() {
        paths.removeFirst();
    }

    public boolean isEmpty() {
        return paths.isEmpty();
    }

    public void createRote(Location current, Location destination) {
        createRote(
                new PathPoint((int) current.x, (int) current.y),
                new PathPoint((int) destination.x, (int) destination.y)
        );
    }

    public List<PathPoint> path() {
        return paths;
    }

    public void clear() {
        paths.clear();
    }

    public boolean checkModification() {

        for (Obstacle obstacle : obstacles) {
            Area area = obstacle.getArea();

            if (area.changed || area.cachedUsing != obstacle.use()) {
                rebuild();
                return true;
            }
        }

        if (obstacles.size() != lastSize) {
            rebuild();
            return true;
        }

        if (hero != null && lastMapChanged != hero.map.mapInfo.lastChanged) {
            rebuild();
            return true;
        }

        if (!paths.isEmpty() && hero != null) {

            Location location = hero.location;

            PathPoint current     = new PathPoint((int) location.x, (int) location.y);
            PathPoint destination = paths.getFirst();

            return !hasLineOfSight(current, destination);
        }

        return false;
    }

    public boolean canMove(Location location) {

        PathPoint point        = new PathPoint((int) location.x, (int) location.y);
        Location  heroLocation = hero.location;
        PathPoint current      = new PathPoint((int) heroLocation.x, (int) heroLocation.y);

        return hasLineOfSight(current, point);
    }

    private void createRote(PathPoint current, PathPoint destination) {
        fixToClosest(current);
        fixToClosest(destination);

        if (hasLineOfSight(current, destination)) {

            paths.clear();
            paths.add(destination);

        } else {

            paths.clear();

            checkModification();

            current.setFinder(this);
            destination.setFinder(this);

            new PathFinderCalculator(
                    current,
                    destination
            ).fillGeneratedPathTo(paths);
        }
    }

    //Need some improvements '-'
    private void fixToClosest(PathPoint point) {

        Area area = areaTo(point);

        if (area == null)
            return;

        PathPoint other = area.toSide(point);

        if (areaTo(other) != null) {

            PathPoint closest = closest(point);

            point.x = closest.x;
            point.y = closest.y;


        } else {
            point.x = other.x;
            point.y = other.y;
        }
    }

    private PathPoint closest(PathPoint point) {

        double    distance = 0;
        PathPoint current  = null;

        for (PathPoint loop : points) {
            double cd = loop.distance(point);

            if (current == null || cd < distance) {
                current = loop;
                distance = cd;
            }

        }

        return current;
    }

    private void rebuild() {
        points.clear();
        lastSize = obstacles.size();
        if (hero != null)
            lastMapChanged = hero.map.mapInfo.lastChanged;
        avoidAreas.clear();

        rebuildPoints();
        setFinder();
    }

    private void rebuildPoints() {

        Set<Area> areas = new HashSet<>();

        for (Obstacle obstacle : obstacles) {

            Area a = obstacle.getArea();

            boolean use = obstacle.use();

            a.changed = false;
            a.cachedUsing = use;

            if (use)
                areas.add(a);
        }

        if (hero != null)
            areas.addAll(hero.map.mapInfo.getAvoidAreas());

        main:
        while (true) {
            for (Area area : areas) {
                for (Area area1 : areas) {
                    if (area1 != area) {
                        if (area.canGrowTo(area1)) {
                            areas.remove(area);
                            areas.remove(area1);
                            areas.add(area.grow(area1));
                            continue main;
                        }
                    }
                }
            }
            break;
        }

        avoidAreas.addAll(areas);

        for (Area area : areas)
            addArea(area);
    }

    private void addArea(Area a) {
        //LEFT AND TOP
        checkAndAddPoint(new PathPoint((int) a.minX - 1, (int) a.minY - 1));
        //LEFT AND BOTTOM
        checkAndAddPoint(new PathPoint((int) a.minX - 1, (int) a.maxY + 1));
        //RIGHT AND TOP
        checkAndAddPoint(new PathPoint((int) a.maxX + 1, (int) a.minY - 1));
        //RIGHT AND BOTTOM
        checkAndAddPoint(new PathPoint((int) a.maxX + 1, (int) a.maxY + 1));
    }

    private void checkAndAddPoint(PathPoint point) {
        if (collisionCount(point) == 0)
            points.add(point);
    }

    private int collisionCount(PathPoint point) {

        int count = 0;

        for (Area area : avoidAreas) {
            if (area.inside(point.x, point.y))
                count++;
        }

        return count;
    }

    private Area areaTo(PathPoint point) {

        for (Area area : avoidAreas) {
            if (area.inside(point.x, point.y)) {
                return area;
            }
        }

        return null;
    }

    private void setFinder() {
        for (PathPoint point : points) {
            point.setFinder(this);
        }
    }

    boolean hasLineOfSight(PathPoint point1, PathPoint point2) {

        Area container = new Area(
                Math.max(point1.x, point2.x),
                Math.min(point1.x, point2.x),
                Math.max(point1.y, point2.y),
                Math.min(point1.y, point2.y)
        );

        for (Area area : avoidAreas) {
            if (container.intersect(area)
                    && !area.hasLineOfSight(point1, point2)) {
                return false;
            }
        }

        return true;
    }
}
