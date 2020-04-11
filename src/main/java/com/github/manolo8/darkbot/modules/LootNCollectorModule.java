package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.core.entities.Box;
import com.github.manolo8.darkbot.core.entities.Npc;
import com.github.manolo8.darkbot.core.manager.Core;
import com.github.manolo8.darkbot.core.manager.DriveManager;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.core.manager.SchedulerManager;
import com.github.manolo8.darkbot.core.utils.module.Module;
import com.github.manolo8.darkbot.core.utils.module.ModuleConfig;
import com.github.manolo8.darkbot.core.utils.module.ModuleOptions;
import com.github.manolo8.darkbot.modules.helper.*;
import com.github.manolo8.darkbot.view.builder.element.component.ICharField;
import com.github.manolo8.darkbot.view.builder.element.component.ICheckBox;
import com.github.manolo8.darkbot.view.builder.element.component.ILabel;
import com.github.manolo8.darkbot.view.builder.element.component.IPetModules;

@ModuleOptions("LootNCollectorModule")
public class LootNCollectorModule
        implements Module {

    private final DangerHelper        danger;
    private final LootHelper          loot;
    private final CollectorHelper     collector;
    private final CircularDriveHelper circularDrive;
    private final PetHelper           pet;

    private final HeroManager      hero;
    private final DriveManager     drive;
    private final SchedulerManager scheduler;

    private final CommonConfig config;


    public LootNCollectorModule(Core core, InternalConfig config) {
        this.loot = new LootHelper(config);
        this.collector = new CollectorHelper();
        this.danger = new DangerHelper(config);
        this.circularDrive = new CircularDriveHelper(this.loot.getTargetObservable());
        this.pet = new PetHelper(config);

        this.danger.install(core);
        this.loot.install(core);
        this.collector.install(core);
        this.circularDrive.install(core);
        this.pet.install(core);

        this.drive = core.getDriveManager();
        this.hero = core.getHeroManager();
        this.scheduler = core.getSchedulerManager();

        this.config = core.getCommonConfig();
    }


    @Override
    public void resume() {

    }

    @Override
    public boolean canRefresh() {
        return !loot.isAttacking();
    }

    @Override
    public void tick() {

        if (danger.checkDangerousAndCurrentMap()) {

            scheduler.asyncSetConfig(config.OFFENSIVE_CONFIG);
            pet.check();

            if (!collector.isCollecting() && loot.findTarget())
                chooseBoxOrTarget();
            else
                boxTick();
        }
    }

    private void chooseBoxOrTarget() {

        Npc target = loot.getTarget();

        if (target.health.hpPercent() > 0.25) {
            if (collector.findBox()) {

                Box box = collector.getCurrentBox();

                double heroDistance   = box.distance(hero);
                double targetDistance = box.distance(target);

                if (heroDistance < targetDistance * 0.85 && heroDistance < 600) {
                    loot.doKillTick();
                    collector.collectBox();
                    return;
                }
            }
        }

        circularDrive.moveToAnSafePosition();
        loot.doKillTick();
    }

    private void boxTick() {
        if (collector.findBox())
            collector.collectBox();
        else if (!drive.isMoving())
            drive.moveRandom();
    }

    private static class InternalConfig
            implements ModuleConfig,
            DangerHelper.DangerHelpConfig,
            LootHelper.LootHelperConfig,
            PetHelper.PetHelperConfig {

        @ILabel("Run from enemies")
        @ICheckBox
        public boolean RUN_FROM_ENEMIES;
        @ILabel("Run from enemies in sight")
        @ICheckBox
        public boolean RUN_FROM_ENEMIES_IN_SIGHT;
        @ILabel("Ammo key")
        @ICharField
        public char    AMMO_KEY     = '3';
        @ILabel("Use auto-sab")
        @ICheckBox
        public boolean AUTO_SAB     = false;
        @ILabel("Ammo sab key")
        @ICharField
        public char    AUTO_SAB_KEY = '4';
        @ILabel("Pet gear")
        @IPetModules
        public int     petGearId;

        @Override
        public char ammoKey() {
            return AMMO_KEY;
        }

        @Override
        public boolean autoSab() {
            return AUTO_SAB;
        }

        @Override
        public char autoSabKey() {
            return AUTO_SAB_KEY;
        }

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
