package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.core.entities.Portal;
import com.github.manolo8.darkbot.core.itf.TempModule;
import com.github.manolo8.darkbot.core.manager.*;
import com.github.manolo8.darkbot.core.objects.Location;
import com.github.manolo8.darkbot.core.objects.Map;
import com.github.manolo8.darkbot.core.utils.Clock;
import com.github.manolo8.darkbot.core.utils.module.ModuleOptions;

@ModuleOptions(value = "MapModule",
               showInModules = false,
               alwaysNewInstance = true)
public class MapModule
        extends TempModule {

    private final HeroManager      hero;
    private final DriveManager     drive;
    private final StarManager      star;
    private final SchedulerManager scheduler;

    private final CommonConfig commonConfig;

    private final Clock   clock;
    private       Portal  current;
    private       Map     target;
    private       boolean configTarget;

    public MapModule(Core core) {
        super(core);

        this.hero = core.getHeroManager();
        this.drive = core.getDriveManager();
        this.star = core.getStarManager();
        this.scheduler = core.getSchedulerManager();
        this.commonConfig = core.getCommonConfig();

        this.clock = new Clock();
    }

    @Override
    public boolean canRefresh() {
        return false;
    }

    public void setTarget(Map target) {
        this.target = target;
        this.configTarget = false;
        current = star.next(target);
    }

    public void setTargetToWorkingMap() {
        setTarget(star.fromId(commonConfig.WORKING_MAP));
        configTarget = true;
    }

    @Override
    public void tick() {

        if (target.id == hero.map.id) {
            back();
        } else {

            checkCurrent();

            scheduler.asyncSetConfig(commonConfig.RUN_CONFIG);

            if (current != null)
                moveToPortal();
        }

    }

    private void checkCurrent() {
        if (configTarget && target.id != commonConfig.WORKING_MAP)
            target = star.fromId(commonConfig.WORKING_MAP);

        current = star.next(target);
    }

    private void moveToPortal() {

        Location loc      = current.location;
        double   distance = hero.distance(loc);

        if (distance < 300 && !drive.isMoving()) {

            if (clock.isBiggerThenReset(5000)) {
                scheduler.asyncKeyboardClick('j');
                drive.move(loc.x + Math.random() * 50, loc.y + Math.random() * 50);
            }

        } else if (current.location.valid) {
            drive.move(current);
        }
    }
}