package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.core.entities.Box;
import com.github.manolo8.darkbot.core.entities.Npc;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.manager.*;
import com.github.manolo8.darkbot.core.utils.module.Module;
import com.github.manolo8.darkbot.core.utils.module.ModuleConfig;
import com.github.manolo8.darkbot.core.utils.module.ModuleOptions;
import com.github.manolo8.darkbot.modules.helper.*;
import com.github.manolo8.darkbot.view.builder.element.component.ICharField;
import com.github.manolo8.darkbot.view.builder.element.component.ICheckBox;
import com.github.manolo8.darkbot.view.builder.element.component.ILabel;
import com.github.manolo8.darkbot.view.builder.element.component.IPetModules;

import java.util.Comparator;
import java.util.List;

@ModuleOptions("DefensiveCollectorModule")
public class DefensiveCollectorModule
        implements Module {

    private final DangerHelper        danger;
    private final LootHelper          loot;
    private final CollectorHelper     collector;
    private final CircularDriveHelper circularDrive;
    private final PetHelper           pet;

    private final InternalConfig config;

    private final CommonConfig commonConfig;

    private final HeroManager      hero;
    private final DriveManager     drive;
    private final SchedulerManager schedulerManager;
    private final StatsManager     statsManager;
    private final ModuleManager    moduleManager;

    private final List<Ship> ships;
    private final List<Box>  boxes;

    public DefensiveCollectorModule(Core core, InternalConfig config) {
        this.config = config;
        this.danger = new DangerHelper(config);
        this.loot = new LootHelper(config, this::shouldKill, this::shouldStopAttack);
        this.collector = new CollectorHelper(this::shouldCollect);
        this.circularDrive = new CircularDriveHelper(this.loot.getTargetObservable());
        this.pet = new PetHelper(config);

        this.collector.install(core);
        this.loot.install(core);
        this.danger.install(core);
        this.circularDrive.install(core);
        this.pet.install(core);

        this.hero = core.getHeroManager();
        this.drive = core.getDriveManager();
        this.schedulerManager = core.getSchedulerManager();
        this.commonConfig = core.getCommonConfig();
        this.ships = core.getEntityManager().ships;
        this.boxes = core.getEntityManager().boxes;
        this.statsManager = core.getStatsManager();
        this.moduleManager = core.getModuleManager();
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

            if (checkDeposit()) {
                doSafeTick();
            } else {
                tryToSellOrStop();
            }
        }
    }

    private boolean checkDeposit() {
        return statsManager.depositTotal == 0 || statsManager.deposit < statsManager.depositTotal;
    }

    private void tryToSellOrStop() {
        if (config.TRADE_PALLADIUM) {
            moduleManager.setModule(PalladiumTraderModule.class);
        } /*else {
            Well, not implemented '-'
        }*/
    }

    private void doSafeTick() {

        pet.check();

        if (loot.findTarget() && drive.isInWorkingArea()) {
            if (!collector.isCollecting()) {
                chooseBoxOrTarget();
                return;
            }
        }

        schedulerManager.asyncSetConfig(commonConfig.RUN_CONFIG);
        boxTick();
    }

    private void boxTick() {
        if (collector.findBox())
            collector.collectBox();
        else if (!drive.isMoving()) {
            drive.moveRandom();
        }
    }

    private void chooseBoxOrTarget() {

        loot.doKillTick();

        if (collector.findBox())
            collector.collectBox();
        else if (loot.isAttacking())
            circularDrive.moveToAnSafePosition();
    }

    private boolean shouldKill(Npc npc) {
        return npc.isAttacking(hero);
    }

    private boolean shouldStopAttack(Npc npc) {
        return !drive.canMove(npc.location);
    }

    private boolean shouldCollect(Box box) {
        if (config.SMART_IGNORE && someoneElseIsCollecting(box))
            return false;
        else
            return targetIsFarFromBox(box);
    }

    private boolean someoneElseIsCollecting(Box box) {

        if (box.isInTimer(1))
            return true;

        boolean ignore = false;

        //Don't collect if pet is close
        if (hero.pet.distance(box) > 400) {

            double heroDistance = hero.distance(box);

            for (Ship ship : ships) {

                double shipDistance = ship.distance(box);

                if (heroDistance < 500 && shipDistance < heroDistance + 50) {
                    if (ship.diffAngle(box) < 2) {
                        ignore = true;
                        break;
                    }

                    if (boxes.stream()
                            .filter(box1 -> box1.distance(ship) != 0)
                            .min(Comparator.comparingDouble(ship::distance))
                            .orElse(null) == box) {
                        ignore = true;
                        break;
                    }
                }

            }

            //No time for pet
            if (ignore)
                box.setTimerTo(1, 15_000);

        } else
            ignore = true;


        return ignore;
    }

    private boolean targetIsFarFromBox(Box box) {
        Npc target = loot.getTarget();

        if (target == null)
            return true;

        double heroDistance   = box.distance(hero);
        double targetDistance = box.distance(target);

        return heroDistance < targetDistance * 0.5;
    }

    private static class InternalConfig implements ModuleConfig,
            DangerHelper.DangerHelpConfig,
            LootHelper.LootHelperConfig,
            PetHelper.PetHelperConfig {


        @ILabel("Run from enemies")
        @ICheckBox
        public boolean RUN_FROM_ENEMIES;
        @ILabel("Trade palladium")
        @ICheckBox
        public boolean TRADE_PALLADIUM = true;
        @ILabel("Ammo key")
        @ICharField
        public char    AMMO_KEY        = '3';
        @ILabel("Use auto-sab")
        @ICheckBox
        public boolean AUTO_SAB        = false;
        @ILabel("Ammo sab key")
        @ICharField
        public char    AUTO_SAB_KEY    = '4';
        @ILabel("Smart ignore boxes")
        @ICheckBox
        public boolean SMART_IGNORE    = true;
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
            return false;
        }

        @Override
        public int gearId() {
            return petGearId;
        }
    }
}
