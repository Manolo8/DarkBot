package com.github.manolo8.darkbot.core.manager.step;

import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.core.exception.StepException;
import com.github.manolo8.darkbot.core.installer.step.StepWithValidator;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.core.manager.ModuleManager;
import com.github.manolo8.darkbot.core.utils.Clock;
import com.github.manolo8.darkbot.core.utils.Observable;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StepToRefresh
        extends StepWithValidator {

    private final HeroManager   hero;
    private final ModuleManager moduleManager;
    private final CommonConfig  commonConfig;
    private final Clock         refreshClock;
    private final Clock         connectBugClock;
    private       boolean       connectBug;

    public StepToRefresh(Observable<Long> mainApplicationAddress,
                         HeroManager hero,
                         ModuleManager moduleManager,
                         CommonConfig commonConfig) {

        this.hero = hero;
        this.moduleManager = moduleManager;
        this.commonConfig = commonConfig;
        this.refreshClock = new Clock();
        this.connectBugClock = new Clock();
        this.refreshClock.value = System.currentTimeMillis();

        mainApplicationAddress.subscribe(value -> {
            refreshClock.value = System.currentTimeMillis();
            connectBug = false;
        });
    }

    @Override
    public boolean isValid() {
        return !(refreshClock.isBigger(commonConfig.REFRESH_TIME * 60000) && moduleManager.getCurrentModule().canRefresh()) && !(hero.map.id == -1 || connectBug);
    }

    @Override
    public void validate()
            throws StepException {

        if (refreshClock.isBigger(commonConfig.REFRESH_TIME * 60000)) {

            if (moduleManager.getCurrentModule().canRefresh()) {
                refreshClock.reset();
                API.reload();
            }

        }

        if (hero.map.id == -1) {

            if (connectBug) {
                if (connectBugClock.isBiggerThenReset(120000))
                    API.reload();
            } else {
                connectBug = true;
                connectBugClock.value = System.currentTimeMillis();
            }

            throw new StepException("Loading... (" + (connectBugClock.elapsed() / 1000) + "/120)");

        } else {
            connectBug = false;
        }

    }

    @Override
    public boolean requireRunning() {
        return true;
    }

    @Override
    public boolean blockTick() {
        return true;
    }
}
