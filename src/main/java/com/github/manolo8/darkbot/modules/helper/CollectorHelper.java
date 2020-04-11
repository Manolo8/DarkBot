package com.github.manolo8.darkbot.modules.helper;

import com.github.manolo8.darkbot.core.entities.Box;
import com.github.manolo8.darkbot.core.manager.Core;
import com.github.manolo8.darkbot.core.manager.DriveManager;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.core.manager.SchedulerManager;
import com.github.manolo8.darkbot.core.utils.Clock;
import com.github.manolo8.darkbot.core.objects.Location;
import com.github.manolo8.darkbot.core.utils.module.ModuleHelper;
import com.github.manolo8.darkbot.modules.itf.Filter;

import java.util.Comparator;
import java.util.List;

public final class CollectorHelper
        implements ModuleHelper {

    private final Filter<Box> shouldCollect;

    private DriveManager     drive;
    private SchedulerManager scheduler;
    private HeroManager      hero;

    private List<Box> boxes;

    private Box   current;
    private int   stage;
    private Clock clock;

    public CollectorHelper(Filter<Box> shouldCollect) {
        this.shouldCollect = shouldCollect;
        this.clock = new Clock();
    }

    public CollectorHelper() {
        this(element -> true);
    }

    @Override
    public void install(Core core) {
        this.drive = core.getDriveManager();
        this.scheduler = core.getSchedulerManager();
        this.hero = core.getHeroManager();

        this.boxes = core.getEntityManager().boxes;
    }

    public boolean isCollecting() {
        return stage != 0;
    }

    public Box getCurrentBox() {
        return current;
    }

    public void collectBox() {

        Location location = hero.location;
        double   distance = location.distance(current.location);
        int      wait     = Math.max(current.boxInfo.waitTime, 100);


        if (distance != 0
                && !hero.isGoingTo(current.location)
                && clock.isBiggerThenReset(200)) {
            drive.stop(false);
            scheduler.asyncSelectTarget(current, false);
            stage = 1;
            clock.reset();
        } else if (distance == 0 && stage == 1) {
            stage++;
            clock.reset();
        } else if (stage == 2 && clock.isBiggerThenReset(wait)) {
            current.ignore = true;
        }

    }

    public boolean findBox() {

        Box closest = boxes.stream()
                .filter(this::shouldCollect)
                .min((Comparator.comparingDouble(hero::distance)))
                .orElse(null);

        if (current != null &&
                (current.ignore || current.isInvalid() || (current.distance(hero) != 0 && !shouldCollect.test(current)))) {

            current = null;
            stage = 0;
        }

        if (closest != null && (current == null || isBetter(closest))) {
            current = closest;
            stage = 0;
        }

        return current != null;
    }

    private boolean shouldCollect(Box box) {
        return box != current
                && box.boxInfo.collect
                && !box.ignore
                && box.distance(hero) < 2000
                && shouldCollect.test(box)
                && (drive.canMove(box.location));
    }

    public boolean isBetter(Box box) {

        double currentDistance = current.distance(hero);
        double newDistance     = box.distance(hero);

        return currentDistance > 600 && currentDistance - 150 > newDistance;
    }
}
