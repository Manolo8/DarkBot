package com.github.manolo8.darkbot.backpage.task;

import com.github.manolo8.darkbot.backpage.itf.Task;
import com.github.manolo8.darkbot.backpage.utils.Client;

public class KeepAliveTask
        extends Task {

    private final Client client;

    public boolean isAlive;

    public KeepAliveTask(Client client) {
        super(300000 + (long) (Math.random() * 300000));

        this.client = client;
    }

    @Override
    public void execute() {
        client.getString("indexInternal.es?action=internalSkylab", this::checkKeepAlive);
    }

    private void checkKeepAlive(String data) {
        isAlive = data.contains("dosid=");
    }
}
