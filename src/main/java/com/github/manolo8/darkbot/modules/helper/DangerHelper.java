package com.github.manolo8.darkbot.modules.helper;

import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.manager.Core;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.core.manager.ModuleManager;
import com.github.manolo8.darkbot.core.utils.module.ModuleHelper;
import com.github.manolo8.darkbot.modules.EscapeModule;
import com.github.manolo8.darkbot.modules.MapModule;

import java.util.List;

import static com.github.manolo8.darkbot.modules.EscapeModule.Mode.ESCAPE;
import static com.github.manolo8.darkbot.modules.EscapeModule.Mode.REPAIR;

public final class DangerHelper
        implements ModuleHelper {

    private final DangerHelpConfig config;

    private CommonConfig commonConfig;

    private ModuleManager moduleManager;
    private HeroManager   hero;

    private List<Ship> ships;

    public DangerHelper(DangerHelpConfig config) {
        this.config = config;
    }

    public void install(Core core) {
        this.moduleManager = core.getModuleManager();
        this.hero = core.getHeroManager();

        this.ships = core.getEntityManager().ships;
        this.commonConfig = core.getCommonConfig();
    }

    public boolean checkDangerousAndCurrentMap() {

        if (commonConfig.WORKING_MAP != hero.map.id) {
            moduleManager.setModule(MapModule.class).setTargetToWorkingMap();
        } else if (hero.health.hpPercent() < commonConfig.REPAIR_HP) {
            moduleManager.setModule(EscapeModule.class).setModeAndConfig(REPAIR, config);
        } else if (hasEnemies()) {
            moduleManager.setModule(EscapeModule.class).setModeAndConfig(ESCAPE, config);
        } else
            return true;

        return false;
    }

    public boolean hasEnemies() {

        if (config.runFromEnemies() || config.runFromEnemiesInSight()) {
            for (Ship ship : ships) {
                if (ship.playerInfo.isEnemy() && ship.distance(hero) < 1500) {

                    if (config.runFromEnemiesInSight())
                        return true;

                    if (ship.isAttacking(hero)) {
                        ship.setTimerTo(0, 400000);
                        return true;
                    } else if (ship.isInTimer(0)) {
                        return true;
                    }

                }
            }
        }

        return false;
    }

    public interface DangerHelpConfig {

        boolean runFromEnemies();

        boolean runFromEnemiesInSight();
    }
}