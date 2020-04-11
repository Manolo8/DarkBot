package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.core.manager.Core;
import com.github.manolo8.darkbot.core.manager.DriveManager;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.core.manager.SchedulerManager;
import com.github.manolo8.darkbot.core.utils.Clock;
import com.github.manolo8.darkbot.core.utils.module.Module;
import com.github.manolo8.darkbot.core.utils.module.ModuleConfig;
import com.github.manolo8.darkbot.core.utils.module.ModuleOptions;
import com.github.manolo8.darkbot.modules.helper.CollectorHelper;
import com.github.manolo8.darkbot.modules.helper.DangerHelper;
import com.github.manolo8.darkbot.modules.helper.PetHelper;
import com.github.manolo8.darkbot.view.builder.element.component.ICharField;
import com.github.manolo8.darkbot.view.builder.element.component.ICheckBox;
import com.github.manolo8.darkbot.view.builder.element.component.ILabel;
import com.github.manolo8.darkbot.view.builder.element.component.IPetModules;

@ModuleOptions("CollectorModule")
public class CollectorModule
        implements Module {

    private final DangerHelper     danger;
    private final CollectorHelper  collector;
    private final PetHelper        pet;
    private final InternalConfig   config;
    private final CommonConfig     commonConfig;
    private final HeroManager      hero;
    private final DriveManager     drive;
    private final SchedulerManager scheduler;

    private final Clock clock;

    public CollectorModule(Core core, InternalConfig config) {

        this.danger = new DangerHelper(config);
        this.collector = new CollectorHelper();
        this.pet = new PetHelper(config);

        this.config = config;

        this.danger.install(core);
        this.collector.install(core);
        this.pet.install(core);

        this.hero = core.getHeroManager();
        this.drive = core.getDriveManager();
        this.scheduler = core.getSchedulerManager();

        this.commonConfig = core.getCommonConfig();

        this.clock = new Clock();
    }


    @Override
    public void resume() {
    }

    @Override
    public boolean canRefresh() {
        return !collector.isCollecting();
    }

    @Override
    public void tick() {

        if (danger.checkDangerousAndCurrentMap()) {

            pet.check();
            scheduler.asyncSetConfig(commonConfig.RUN_CONFIG);
            checkInvisibility();

            if (collector.findBox())
                collector.collectBox();
            else if (!drive.isMoving())
                drive.moveRandom();
        }
    }

    private void checkInvisibility() {
        if (config.AUTO_CLOAK
                && !hero.invisible
                && clock.isBiggerThenReset(60_000)
        ) {
            scheduler.asyncKeyboardClick(config.AUTO_CLOAK_KEY);
        }
    }

    private static class InternalConfig
            implements ModuleConfig,
            DangerHelper.DangerHelpConfig,
            PetHelper.PetHelperConfig {

        @ILabel("Run from enemies")
        @ICheckBox
        public boolean RUN_FROM_ENEMIES;
        @ILabel("Run from enemies in sight")
        @ICheckBox
        public boolean RUN_FROM_ENEMIES_IN_SIGHT;
        @ILabel("Auto cloak")
        @ICheckBox
        public boolean AUTO_CLOAK;
        @ILabel("Auto cloak key")
        @ICharField
        public char    AUTO_CLOAK_KEY;
        @ILabel("Pet gear")
        @IPetModules
        public int     petGearId;

        @Override
        public boolean runFromEnemies() {
            return RUN_FROM_ENEMIES;
        }

        @Override
        public boolean runFromEnemiesInSight() {
            return RUN_FROM_ENEMIES_IN_SIGHT;
        }

        @Override
        public int gearId() {
            return petGearId;
        }
    }

}
