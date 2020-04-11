package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.core.utils.module.Module;
import com.github.manolo8.darkbot.core.manager.Core;
import com.github.manolo8.darkbot.core.manager.DriveManager;
import com.github.manolo8.darkbot.core.manager.SchedulerManager;
import com.github.manolo8.darkbot.core.utils.module.ModuleConfig;
import com.github.manolo8.darkbot.core.utils.module.ModuleOptions;
import com.github.manolo8.darkbot.modules.helper.CircularDriveHelper;
import com.github.manolo8.darkbot.modules.helper.DangerHelper;
import com.github.manolo8.darkbot.modules.helper.LootHelper;
import com.github.manolo8.darkbot.modules.helper.PetHelper;
import com.github.manolo8.darkbot.view.builder.element.component.ICharField;
import com.github.manolo8.darkbot.view.builder.element.component.ICheckBox;
import com.github.manolo8.darkbot.view.builder.element.component.ILabel;
import com.github.manolo8.darkbot.view.builder.element.component.IPetModules;

@ModuleOptions("LootModule")
public class LootModule
        implements Module {

    private final LootHelper          loot;
    private final DangerHelper        danger;
    private final CircularDriveHelper circularDrive;
    private final PetHelper           pet;

    private final DriveManager     drive;
    private final SchedulerManager scheduler;

    private final CommonConfig config;

    public LootModule(Core core, InternalConfig config) {
        this.loot = new LootHelper(config);
        this.danger = new DangerHelper(config);
        this.circularDrive = new CircularDriveHelper(this.loot.getTargetObservable());
        this.pet = new PetHelper(config);

        this.loot.install(core);
        this.danger.install(core);
        this.circularDrive.install(core);
        this.pet.install(core);

        this.scheduler = core.getSchedulerManager();
        this.drive = core.getDriveManager();

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

            pet.check();
            scheduler.asyncSetConfig(config.OFFENSIVE_CONFIG);

            if (loot.findTarget()) {

                circularDrive.moveToAnSafePosition();
                loot.doKillTick();

            } else if (!drive.isMoving()) {
                drive.moveRandom();
            }

        }

    }

    private static class InternalConfig implements ModuleConfig,
            LootHelper.LootHelperConfig,
            DangerHelper.DangerHelpConfig,
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
