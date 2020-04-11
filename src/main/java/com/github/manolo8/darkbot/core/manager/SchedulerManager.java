package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.config.ShipConfig;
import com.github.manolo8.darkbot.core.entities.Entity;
import com.github.manolo8.darkbot.core.itf.Manager;
import com.github.manolo8.darkbot.core.objects.Location;
import com.github.manolo8.darkbot.core.utils.Clock;

import java.util.LinkedList;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class SchedulerManager
        implements Manager {

    public static SchedulerManager INSTANCE;

    private final LinkedList<Runnable> queue;
    private final MapManager           mapManager;
    private final HeroManager          hero;

    private final Clock clock;

    private ShipConfig config;

    private int lastFormation;
    private int attempts;

    public SchedulerManager(Core core) {

        INSTANCE = this;

        this.hero = core.getHeroManager();
        this.mapManager = core.getMapManager();

        this.queue = new LinkedList<>();

        this.clock = new Clock();

        new Thread(this::run).start();
    }

    public void asyncKeyboardClick(char ch) {
        this.add(() -> API.keyPress(ch));
    }

    public void asyncSelectTarget(Entity target, boolean doubleClick) {
        this.add(() -> {
            target.clickable.setRadius(5000);

            mapManager.clickCenter();
            if (doubleClick)
                mapManager.clickCenter();

            target.clickable.setRadius(0);
        });
    }

    public void asyncSetConfig(ShipConfig shipConfig) {
        this.config = shipConfig;
    }

    public void asyncClick(int x, int y) {
        this.add(() -> API.mousePress(x, y));
    }

    void add(Runnable runnable) {
        synchronized (queue) {
            queue.add(runnable);
        }
    }

    public void asyncMove(Location location) {
        add(() -> mapManager.move(location));
    }

    private void run() {
        while (true) {

            Runnable runnable;

            synchronized (queue) {
                runnable = queue.poll();
            }

            if (runnable != null)
                runnable.run();
            else
                sleep();
        }
    }

    public void tick() {
        if (config != null && clock.isBiggerThenReset(6_000)) {

            if (hero.configId != config.configId) {

                asyncKeyboardClick('c');

                if (config.formationKey != '\0') {
                    lastFormation = hero.formationId;
                    asyncKeyboardClick(config.formationKey);
                    attempts = 0;
                }

            }

            if (config.formationKey != '\0'
                    && lastFormation == hero.formationId
                    && ++attempts < 5)
                asyncKeyboardClick(config.formationKey);
        }
    }

    public boolean isFree() {
        return queue.isEmpty();
    }

    private void sleep() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
