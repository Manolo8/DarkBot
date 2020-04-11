package com.github.manolo8.darkbot.backpage;

import com.github.manolo8.darkbot.backpage.auth.AuthenticationResult;
import com.github.manolo8.darkbot.backpage.itf.Task;
import com.github.manolo8.darkbot.backpage.task.KeepAliveTask;
import com.github.manolo8.darkbot.backpage.utils.Client;

import java.util.ArrayList;
import java.util.List;

public class BackPageManager {

    private final Client     client;
    private final List<Task> tasks;

    //    public final StatsTask     statsTask;
//    public final GalaxyTask    galaxyTask;
    public final KeepAliveTask keepAliveTask;

    public BackPageManager() {

        this.client = new Client();
        this.tasks = new ArrayList<>();

//        this.tasks.add(this.statsTask = new StatsTask(client));
//        this.tasks.add(this.galaxyTask = new GalaxyTask(client));
        this.tasks.add(this.keepAliveTask = new KeepAliveTask(client));
    }

    public void auth(AuthenticationResult result) {
        this.client.auth(result);
    }

    public void tick() {

        if (client.auth != null)
            for (Task task : tasks)
                if (task.shouldExecute())
                    task.execute();
    }
}
