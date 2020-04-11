package com.github.manolo8.darkbot.modules.helper;

import com.github.manolo8.darkbot.core.entities.Pet;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.manager.Core;
import com.github.manolo8.darkbot.core.manager.PetManager;
import com.github.manolo8.darkbot.core.objects.PetGear;
import com.github.manolo8.darkbot.core.utils.Clock;
import com.github.manolo8.darkbot.core.utils.module.ModuleHelper;

import java.util.List;

public class PetHelper
        implements ModuleHelper {

    private final Clock clock;

    private PetManager      petManager;
    private PetHelperConfig config;

    private List<Ship> ships;
    private Pet        pet;

    private int overrideGear;

    public PetHelper(PetHelperConfig config) {
        this.clock = new Clock();
        this.config = config;
    }

    @Override
    public void install(Core core) {
        this.petManager = core.getPetManager();
        this.ships = core.getEntityManager().ships;
        this.pet = core.getHeroManager().pet;
    }

    public boolean isGearReady() {
        PetGear gear = petManager.gearToSet();

        if (gear == null)
            return false;

        return gear.active;
    }

    public void overrideGear(int newGearId) {
        this.overrideGear = newGearId;
    }

    public void removeOverride() {
        this.overrideGear = 0;
    }

    public void check() {

        PetGear currentSet = petManager.gearToSet();

        if (petIsAttackingPlayer())
            clock.reset();

        int set;

        if (overrideGear != 0)
            set = overrideGear;
        else if (!clock.isBigger(120_000))
            set = 1;
        else
            set = config.gearId();

        if (currentSet == null || set != currentSet.id)
            petManager.setGear(petManager.gearById(set));
    }

    private boolean petIsAttackingPlayer() {
        return ships.stream().anyMatch(pet::isAttacking);
    }

    public interface PetHelperConfig {

        int gearId();

    }
}
