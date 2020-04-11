package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.config.MapInfo;
import com.github.manolo8.darkbot.core.entities.Entity;
import com.github.manolo8.darkbot.core.itf.Manager;
import com.github.manolo8.darkbot.core.objects.Location;
import com.github.manolo8.darkbot.core.utils.Clock;
import com.github.manolo8.darkbot.core.utils.pathfinder.Area;
import com.github.manolo8.darkbot.core.utils.pathfinder.PathFinder;

import java.util.List;
import java.util.Random;

import static java.lang.Math.random;

public class DriveManager
        implements Manager {

    private final Random random;

    private final MapManager       mapManager;
    private final HeroManager      hero;
    private final SchedulerManager schedulerManager;

    private final Location heroLocation;

    public PathFinder pathFinder;

    private Location destination;
    private Location last;
    private boolean  changedDestination;

    private Clock clock;
    private int   blameBPTimes;

    public DriveManager(Core core) {
        this.random = new Random();
        this.clock = new Clock();
        this.mapManager = core.getMapManager();
        this.schedulerManager = core.getSchedulerManager();
        this.hero = core.getHeroManager();
        this.heroLocation = hero.location;
        this.pathFinder = new PathFinder(hero, core.getEntityManager().obstacles);
        this.destination = new Location(0, 0);
    }

    void tick() {

        checkDestination();

        if (shouldMove())
            makeMovement();
    }

    private void makeMovement() {

        Location currentDestination = pathFinder.current();

        if (!currentDestination.equals(last) || !hero.isMoving() || !isMovingRight()) {

            last = currentDestination;
            double distance = heroLocation.distance(currentDestination);

            if (distance == 0) {
                pathFinder.currentCompleted();
                resetBlame();
                if (!pathFinder.isEmpty())
                    makeMovement();
            } else
                schedulerManager.asyncMove(currentDestination);
        }

    }

    private boolean isMovingRight() {
        if (!hero.isGoingTo(pathFinder.current())) {
            return false;
        } else if (clock.isBiggerThenReset(1000 + (1000 * blameBPTimes))) {
            //Sometimes the Drive dsync with BP server, so, if is moving for more than 3 seconds re-send movement
            blameBPTimes++;
            return false;
        }

        return true;
    }

    private void checkDestination() {
        if (pathFinder.checkModification() || changedDestination || (!heroLocation.equals(destination) && pathFinder.isEmpty())) {
            pathFinder.createRote(heroLocation, destination);
            changedDestination = false;
        }
    }

    private boolean shouldMove() {
        return !pathFinder.isEmpty();
    }

    public boolean canMove(Location location) {
        return pathFinder.canMove(location);
    }

    public void stop(boolean at) {

        if (!pathFinder.isEmpty()) {

            if (at)
                schedulerManager.asyncMove(heroLocation);

            if (!pathFinder.isEmpty())
                pathFinder.clear();

            destination = heroLocation;
        }
    }

    public void move(Entity entity) {
        move(entity.location);
    }

    public void move(Location location) {
        move(location.x, location.y);
    }

    public void move(double x, double y) {

        if (destination.x == x && destination.y == y)
            return;

        destination = new Location(x, y);
        changedDestination = true;
        resetBlame();
    }

    private void resetBlame() {
        blameBPTimes = 0;
        clock.reset();
    }

    public void moveRandom() {

        MapInfo info = hero.map.mapInfo;

        List<Area> work = info.getWorkAreas();

        if (work.size() == 0)
            move(random() * mapManager.internalWidth, random() * mapManager.internalHeight);
        else {
            Area area = work.get(random.nextInt(work.size()));
            move(area.maxX, area.maxY);
        }
    }

    public boolean isInWorkingArea() {
        MapInfo info = hero.map.mapInfo;

        int x = (int) heroLocation.x;
        int y = (int) heroLocation.y;

        return info.getWorkAreas().stream().anyMatch(area -> area.inside(x, y));
    }

    public boolean isMoving() {
        return !pathFinder.isEmpty();
    }

    public boolean isOutOfMap() {
        return mapManager.isOutOfMap(heroLocation);
    }
}
