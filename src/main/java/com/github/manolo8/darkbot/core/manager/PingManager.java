package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.core.installer.BotInstaller;
import com.github.manolo8.darkbot.core.itf.Installable;
import com.github.manolo8.darkbot.core.objects.swf.SwfVectorInt;

public class PingManager
        implements Installable {

    private SwfVectorInt lastPings;

    public int ping;

    private int  currentIndex;
    private long lastCheck;

    public PingManager() {
        this.lastPings = new SwfVectorInt(0);
    }

    @Override
    public void install(BotInstaller botInstaller) {
        botInstaller.pingManagerAddress.subscribe(lastPings::update);
    }

    public void update() {
        if (System.currentTimeMillis() - lastCheck >= 1000) {

            lastPings.update();

            int current = 0;

            if (currentIndex == lastPings.size)
                current = 1000 + ping;
            else if (lastPings.size > 0)
                current = lastPings.elements[lastPings.size - 1];


            this.ping = (ping + current) / 2;

            currentIndex = lastPings.size;

            lastCheck = System.currentTimeMillis();
        }
    }
}
