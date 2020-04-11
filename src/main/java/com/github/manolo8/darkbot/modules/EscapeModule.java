package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.core.entities.Portal;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.itf.TempModule;
import com.github.manolo8.darkbot.core.manager.*;
import com.github.manolo8.darkbot.core.objects.Health;
import com.github.manolo8.darkbot.core.utils.Clock;
import com.github.manolo8.darkbot.core.utils.module.ModuleConfig;
import com.github.manolo8.darkbot.core.utils.module.ModuleOptions;
import com.github.manolo8.darkbot.modules.helper.CircularDriveHelper;
import com.github.manolo8.darkbot.modules.helper.DangerHelper;
import com.github.manolo8.darkbot.view.builder.element.component.ICharField;
import com.github.manolo8.darkbot.view.builder.element.component.ICheckBox;
import com.github.manolo8.darkbot.view.builder.element.component.ILabel;

import java.util.List;

@ModuleOptions(
        value = "EscapeModule",
        showInModules = false,
        alwaysNewInstance = true
)
public class EscapeModule
        extends TempModule {

    private final CircularDriveHelper circularDriver;

    private final HeroManager      hero;
    private final SchedulerManager schedulerManager;
    private final StarManager      starManager;
    private final DriveManager     drive;

    private final Clock clock;

    private final CommonConfig commonConfig;

    private final List<Ship> ships;

    private DangerHelper.DangerHelpConfig dangerConfig;
    private InternalConfig                config;

    private int times;

    private Mode mode;

    public EscapeModule(Core core, InternalConfig config) {
        super(core);

        this.circularDriver = new CircularDriveHelper();
        this.config = config;
        this.commonConfig = core.getCommonConfig();

        this.hero = core.getHeroManager();
        this.schedulerManager = core.getSchedulerManager();
        this.drive = core.getDriveManager();
        this.starManager = core.getStarManager();

        this.clock = new Clock();

        this.ships = core.getEntityManager().ships;

        this.circularDriver.install(core);
    }

    @Override
    public boolean canRefresh() {
        return false;
    }

    public void setModeAndConfig(Mode mode, DangerHelper.DangerHelpConfig config) {
        this.mode = mode;
        this.dangerConfig = config;
    }

    @Override
    public void tick() {

        schedulerManager.asyncSetConfig(commonConfig.RUN_CONFIG);

        if (mode == Mode.REPAIR)
            tickRepair();
        else
            tickEscape();
    }

    private void tickRepair() {
        if (!isAttackedByOtherPlayer())
            safelyRepair();
        else
            mode = Mode.ESCAPE;
    }

    private void safelyRepair() {
        Health health = hero.health;

        if (health.hpPercent() < Math.max(0.8, commonConfig.REPAIR_HP))
            beAwayFromNpcAndWaitHealthIncrease();
        else
            back();
    }

    private void beAwayFromNpcAndWaitHealthIncrease() {

        Health health = hero.health;

        if (!health.isDecreasedIn(2_000 + (times * 25))) {
            circularDriver.moveAway();
        } else {
            Portal closest = starManager.next(hero.map);
            times++;
            drive.move(closest);
        }
    }

    private void tickEscape() {
        if (hasEnemies()) {

            Portal closest = starManager.next(hero.map);
            drive.move(closest);

            if (isAttackedByOtherPlayer()) {
                if (closest.distance(hero) < 300)
                    schedulerManager.asyncKeyboardClick('j');
                else if (config.USE_SLOW_MINE && clock.isBiggerThenReset(30_000))
                    schedulerManager.asyncKeyboardClick(config.SLOW_MINE_KEY);
            }
        } else {
            back();
        }
    }

    private boolean isAttackedByOtherPlayer() {
        for (Ship ship : ships)
            if (ship.isAttacking(hero))
                return true;

        return false;
    }

    private boolean hasEnemies() {

        if (dangerConfig.runFromEnemies() || dangerConfig.runFromEnemiesInSight()) {
            for (Ship ship : ships) {
                if (ship.playerInfo.isEnemy() && ship.distance(hero) < 3000) {

                    if (dangerConfig.runFromEnemiesInSight())
                        return true;

                    if (ship.isAttacking(hero)) {
                        ship.setTimerTo(0, 60_000);
                        return true;
                    } else if (ship.isInTimer(0)) {
                        return true;
                    }

                }
            }
        }

        return false;
    }

    public enum Mode {
        REPAIR, ESCAPE
    }

    private static class InternalConfig
            implements ModuleConfig {

        @ILabel("Use slow mine")
        @ICheckBox
        public boolean USE_SLOW_MINE;
        @ILabel("Slow mine key")
        @ICharField
        public char    SLOW_MINE_KEY;
    }
}
