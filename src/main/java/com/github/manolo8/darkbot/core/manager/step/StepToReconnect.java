package com.github.manolo8.darkbot.core.manager.step;

import com.github.manolo8.darkbot.core.exception.StepException;
import com.github.manolo8.darkbot.core.installer.step.StepWithValidator;
import com.github.manolo8.darkbot.core.manager.GuiManager;
import com.github.manolo8.darkbot.core.manager.PingManager;
import com.github.manolo8.darkbot.core.manager.SchedulerManager;
import com.github.manolo8.darkbot.core.objects.Gui;
import com.github.manolo8.darkbot.core.utils.Observable;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StepToReconnect
        extends StepWithValidator {

    private final SchedulerManager schedulerManager;
    private final PingManager      pingManager;
    private final Gui              lostConnection;
    private final Gui              connection;

    private long    reconnectTime;
    private boolean restarting;

    public StepToReconnect(
            Observable<Long> applicationAddress,
            SchedulerManager schedulerManager,
            PingManager pingManager,
            GuiManager guiManager) {

        this.schedulerManager = schedulerManager;
        this.pingManager = pingManager;
        this.lostConnection = guiManager.fromName("lost_connection");
        this.connection = guiManager.fromName("connection");
        applicationAddress.subscribe(value -> restarting = false);
    }

    @Override
    public boolean isValid() {

        lostConnection.update();
        connection.update();

        return !lostConnection.visible && !connection.visible && pingManager.ping < 300_000;
    }

    @Override
    public void validate()
            throws StepException {

        if (restarting)
            throw new StepException("Restarting game...");

        if (lostConnection.visible) {
            tryReconnect();
            throw new StepException("Trying to reconnect");
        }

        if (connection.visible) {

            if (connection.lastUpdatedIn(60000)) {

                if (!restarting)
                    API.reload();

                restarting = true;

                throw new StepException("Waiting connection too long, restarting...");
            }

            throw new StepException("Connecting... (" + (connection.lastUpdate() / 1000) + "/60)");
        }

        if (pingManager.ping > 50_000) {

            if (!restarting)
                API.reload();

            pingManager.ping = 0;
            restarting = true;

            throw new StepException("Ping to high... Reloading");
        }

    }

    @Override
    public boolean requireRunning() {
        return false;
    }

    @Override
    public boolean blockTick() {
        return true;
    }

    private void tryReconnect() {
        if (System.currentTimeMillis() - reconnectTime > 5000) {
            reconnectTime = System.currentTimeMillis();

            schedulerManager.asyncClick(lostConnection.x + 46, lostConnection.y + 180);
        }
    }
}
