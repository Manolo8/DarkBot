package com.github.manolo8.darkbot.core.manager.step;

import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.core.exception.StepException;
import com.github.manolo8.darkbot.core.installer.step.StepWithValidator;
import com.github.manolo8.darkbot.core.manager.MapManager;
import com.github.manolo8.darkbot.core.manager.SchedulerManager;
import com.github.manolo8.darkbot.core.manager.StatsManager;
import com.github.manolo8.darkbot.core.utils.ByteUtils;
import com.github.manolo8.darkbot.core.utils.Clock;
import com.github.manolo8.darkbot.core.utils.Observable;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StepToRevive
        extends StepWithValidator {

    private final SchedulerManager schedulerManager;
    private final StatsManager     statsManager;
    private final MapManager       mapManager;
    private final CommonConfig     config;
    private       long             screenManagerAddress;
    private       long             guiManagerAddress;
    private       long             repairAddress;
    private       long             mainAddress;

    private Clock reQuery;
    private Clock reload;

    private Clock revive;

    private boolean waiting;
    private boolean tried;

    public StepToRevive(
            Observable<Long> screenManagerAddress,
            Observable<Long> guiManagerAddress,
            Observable<Long> mainAddress,
            SchedulerManager schedulerManager,
            StatsManager statsManager,
            MapManager mapManager,
            CommonConfig commonConfig) {

        this.schedulerManager = schedulerManager;
        this.statsManager = statsManager;
        this.mapManager = mapManager;
        this.config = commonConfig;

        this.reQuery = new Clock();
        this.reload = new Clock();
        this.revive = new Clock();

        screenManagerAddress.subscribe(address -> {
            this.screenManagerAddress = address;
            this.repairAddress = 0;
            this.tried = false;
            this.waiting = false;
        });

        guiManagerAddress.subscribe(address -> this.guiManagerAddress = address);
        mainAddress.subscribe(address -> this.mainAddress = address);
    }

    @Override
    public boolean isValid() {
        if (!(config.MAX_DEATHS == -1 || statsManager.deaths < config.MAX_DEATHS))
            return false;

        if (isDead())
            return false;
        else {
            tried = false;
            waiting = false;
            return true;
        }
    }

    @Override
    public void validate()
            throws StepException {

        if (config.MAX_DEATHS != -1 && statsManager.deaths >= config.MAX_DEATHS)
            throw new StepException("Max deaths reached");

        if (isDead()) {

            if (!waiting) {
                waiting = true;
                revive.reset();
            }

            if (!revive.isBigger(10000))
                throw new StepException("Ship is dead, repairing in (" + (revive.elapsed() / 1000) + "/10)");

            if (tried) {

                if (reload.isBiggerThenReset(30000))
                    API.reload();

            } else {
                tryRevive();
                reload.reset();
                tried = true;
            }

            throw new StepException("Ship is dead... (" + (reload.elapsed() / 1000) + "/30)");
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

    private boolean isDead() {
        if (repairAddress != 0) {
            return API.readMemoryBoolean(repairAddress + 40);
        } else {
            if (isInvalidShip() && guiManagerAddress != 0 && mainAddress != 0 && reQuery.isBiggerThenReset(10_000)) {

                long[] values = API.queryMemory(ByteUtils.getBytes(guiManagerAddress, mainAddress), 1);

                if (values.length == 1) {
                    repairAddress = values[0] - 56;
                    return isDead();
                }
            }

            return false;
        }
    }

    private boolean isInvalidShip() {

        long temp = API.readMemoryLong(screenManagerAddress + 240);

        if (temp == 0)
            return true;

        return API.readMemoryInt(temp + 56) == 0;
    }

    private void tryRevive() {

        int method = config.RESPAWN;

        int midX = mapManager.clientWidth / 2;
        int midY = mapManager.clientHeight / 2;

        schedulerManager.asyncClick(midX + (150 * (method - 1)), midY);
        schedulerManager.asyncClick(midX, midY + 190);

        statsManager.deaths++;
    }

}
