package com.github.manolo8.darkbot.backpage.task;

import com.github.manolo8.darkbot.backpage.itf.Task;
import com.github.manolo8.darkbot.backpage.types.GalaxyGateHandler;
import com.github.manolo8.darkbot.backpage.utils.Client;
import com.github.manolo8.darkbot.core.utils.Clock;

public class GalaxyTask
        extends Task {

    private final Client            client;
    private final GalaxyGateHandler handler;
    private final Clock             lastUpdated;
    private       boolean           running;

    public GalaxyTask(Client client) {
        super(500);

        this.handler = new GalaxyGateHandler();
        this.lastUpdated = new Clock();
        this.client = client;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public boolean shouldExecute() {
        return running && super.shouldExecute();
    }

    @Override
    public void execute() {

        if (lastUpdated.isBiggerThenReset(120_000))
            client.getString(queryInit(), this.handler::load);

        if (running)
            client.getString(createQuerySpin(), this.handler::load);
    }

    private String createQuery() {
        return "flashinput/galaxyGates.php?userID=" + client.auth.id + "&sid=" + client.auth.sid;
    }

    private String queryInit() {
        return createQuery() + "&action=init";
    }

    private String createQuerySpin() {

        StringBuilder builder = new StringBuilder();
        builder
                .append(createQuery())
                .append("&action=multiEnergy&gateID=")
                .append(handler.currentGateId)
                .append('&')
                .append(handler.currentGate.name)
                .append("=1&sample=1&spinAmount=")
                .append(handler.selectedSpinAmount);

        if (handler.currentGate.multiplier != 0) {
            builder
                    .append("&multiplier=")
                    .append(handler.currentGate.multiplier);
        }

        return builder.toString();
    }
}
