package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.core.entities.Npc;
import com.github.manolo8.darkbot.core.entities.Portal;
import com.github.manolo8.darkbot.core.manager.Core;
import com.github.manolo8.darkbot.core.manager.DriveManager;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.core.manager.SchedulerManager;
import com.github.manolo8.darkbot.core.objects.Location;
import com.github.manolo8.darkbot.core.utils.module.Module;
import com.github.manolo8.darkbot.core.utils.module.ModuleConfig;
import com.github.manolo8.darkbot.core.utils.module.ModuleOptions;
import com.github.manolo8.darkbot.modules.helper.CircularDriveHelper;
import com.github.manolo8.darkbot.modules.helper.LootHelper;
import com.github.manolo8.darkbot.modules.helper.PetHelper;
import com.github.manolo8.darkbot.view.builder.element.component.ICharField;
import com.github.manolo8.darkbot.view.builder.element.component.ICheckBox;
import com.github.manolo8.darkbot.view.builder.element.component.ILabel;
import com.github.manolo8.darkbot.view.builder.element.component.IPetModules;

import java.util.Comparator;
import java.util.List;

@ModuleOptions("VortexModule")
public class VortexModule
        implements Module {

    private final LootHelper          loot;
    private final CircularDriveHelper circularDrive;
    private final PetHelper           pet;

    private CommonConfig commonConfig;

    private HeroManager      hero;
    private DriveManager     drive;
    private SchedulerManager schedulerManager;

    private boolean repairing;

    private List<Npc>    npcs;
    private List<Portal> portals;

    private boolean low;

    public VortexModule(Core core, InternalConfig config) {
        this.loot = new LootHelper(config, this::shouldStartAttack, this::shouldStopAttack);
        this.circularDrive = new CircularDriveHelper(this.loot.getTargetObservable());
        this.pet = new PetHelper(config);

        this.loot.install(core);
        this.circularDrive.install(core);
        this.pet.install(core);

        this.commonConfig = core.getCommonConfig();

        this.hero = core.getHeroManager();
        this.drive = core.getDriveManager();
        this.schedulerManager = core.getSchedulerManager();

        this.npcs = core.getEntityManager().npcs;
        this.portals = core.getEntityManager().portals;
    }

    @Override
    public void resume() {
    }

    @Override
    public boolean canRefresh() {
        return npcs.size() == 0;
    }

    @Override
    public void tick() {

        updateInfo();

        if (hasNpcs()) {
            if (checkHealth())
                doKillTick();
            else
                goSafe();
        } else
            goToNextMap();
    }

    private boolean hasNpcs() {
        return npcs.size() > 0;
    }

    private void doKillTick() {
        if (loot.findTarget()) {
            pet.check();

            circularDrive.moveToAnSafePosition();

            loot.doKillTick();
        }
    }

    private boolean checkHealth() {

        if (repairing && hero.health.hpPercent() < 0.9)
            return true;

        return repairing = hero.health.hpPercent() < 0.5;
    }

    private void goToNextMap() {

        if (portals.size() > 0) {
            Portal portal = portals.get(0);

            drive.move(portal);

            if (hero.distance(portal) < 100)
                schedulerManager.asyncKeyboardClick('j');
        }

    }

    private boolean shouldStartAttack(Npc npc) {

        if (low)
            return true;

        return npc.health.hpPercent() >= 0.25;
    }

    private boolean shouldStopAttack(Npc npc) {
        if (!low && npc.health.hpPercent() > 0.25) {

            Npc closest = closestNpc();

            if (closest != null) {
                Location current = hero.location;

                double d1 = current.distance(closest.location);
                double d2 = current.distance(npc.location);

                return d1 < d2 - 100;
            }
        }

        return !low && npc.health.hpPercent() <= 0.25;
    }

    private Npc closestNpc() {
        return npcs.stream()
                .min(Comparator.comparingDouble(value -> value.distance(hero.location))).orElse(null);
    }

    private void goSafe() {
        circularDrive.moveAway();
    }

    private void updateInfo() {
        low = npcs.stream().noneMatch(npc -> npc.health.hpPercent() >= 0.25);
    }

    private static class InternalConfig implements ModuleConfig,
            LootHelper.LootHelperConfig,
            PetHelper.PetHelperConfig {

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
        public int gearId() {
            return petGearId;
        }
    }
}
