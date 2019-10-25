package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.core.entities.Npc;
import com.github.manolo8.darkbot.core.entities.Pet;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.objects.Gui;

import java.util.List;

import static java.lang.Math.max;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class PetManager extends Gui {

    private static final int MAIN_BUTTON_X = 30, MODULES_X_MAX = 260, MODULE_Y = 120;

    private long togglePetTime, selectModuleTime;
    private long activeUntil;
    private int moduleStatus = -2; // -2 no module, -1 selecting module, >= 0 module selected
    private Main main;
    private List<Ship> ships;
    private Ship target;
    private Pet pet;
    private boolean enabled = false;

    PetManager(Main main) {
        this.main = main;
        this.ships = main.mapManager.entities.ships;
        this.pet = main.hero.pet;
    }

    public void tick() {
        if (!main.isRunning() || !main.config.PET.ENABLED || kamikazeTimer == -1 || repairModuleIsInUse()) return;
        if (active() != enabled) {
            if (show(true)) clickToggleStatus();
            return;
        }
        if (!enabled) {
            show(false);
            return;
        }
        updatePetTarget();
        int module = (target == null || target instanceof Npc || target.playerInfo.isEnemy()) ? main.config.PET.MODULE : 0;
        if (moduleStatus != module && show(true)) this.selectModule(module);
        else if (moduleSelected()) show(false);
    }

    private void updatePetTarget() {
        if (target == null || target.removed || !pet.isAttacking(target))
            target = ships.stream().filter(s -> pet.isAttacking(s)).findFirst().orElse(null);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    private boolean active() {
        if (!pet.removed) activeUntil = System.currentTimeMillis() + 1000;
        return System.currentTimeMillis() < activeUntil;
    }

    private boolean moduleSelected() {
        return System.currentTimeMillis() - this.selectModuleTime > 1000L;
    }

    private void clickToggleStatus() {
        if (System.currentTimeMillis() - this.togglePetTime > 5000L) {
            click(MAIN_BUTTON_X, MODULE_Y);
            this.moduleStatus = -2;
            this.togglePetTime = System.currentTimeMillis();
        }
    }

    private void selectModule(int module) {
        if (System.currentTimeMillis() - this.selectModuleTime > 1000L) {
            if (moduleStatus != -1) {
                click(MODULES_X_MAX - 5, MODULE_Y);
                this.moduleStatus = -1;
            } else {
                click(MODULES_X_MAX - 30, MODULE_Y + 40 + (20 * module));
                this.moduleStatus = module;
            }
            this.selectModuleTime = System.currentTimeMillis();
        }
    }

    private long kamikazeTimer;
    public long throwKamikaze(int module, int coolDown) {
        if (System.currentTimeMillis() < kamikazeTimer) return kamikazeTimer;
        kamikazeTimer = active() && pet.removed ? System.currentTimeMillis() + coolDown * 1000 : -1;

        if (!active()) {
            if (show(true)) clickToggleStatus();
            return kamikazeTimer;
        }
        if (moduleStatus != module && show(true)) selectModule(module);
        else if (moduleSelected()) show(false);

        return kamikazeTimer;
    }

    public long kamikazeCoolDown() {
        long coolDown = max(0, kamikazeTimer - System.currentTimeMillis());
        return MILLISECONDS.toSeconds(coolDown);
    }

    private long useRepairModuleUntil;
    public void useRepairModule(int module) {
        if (System.currentTimeMillis() < useRepairModuleUntil) return;
        useRepairModuleUntil = -1;

        if (!active()) {
            if (show(true)) clickToggleStatus();
            return;
        }
        if (moduleStatus != module && show(true)) selectModule(module);
        else if (moduleSelected()) {
            useRepairModuleUntil = System.currentTimeMillis() + 16_000;
            show(false);
        }
    }

    private boolean repairModuleIsInUse() {
        return useRepairModuleUntil == -1 || System.currentTimeMillis() < useRepairModuleUntil - 10_000;
    }
}
