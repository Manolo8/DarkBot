package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.core.entities.Entity;
import com.github.manolo8.darkbot.core.installer.BotInstaller;
import com.github.manolo8.darkbot.core.itf.Installable;
import com.github.manolo8.darkbot.core.objects.Location;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class MapManager implements Installable {

    public static MapManager INSTANCE;

    private final ThreadWorker worker;
    private final HeroManager  hero;
    private final StarManager  starManager;

    public static long address;
    private       long mapAddressStatic;
    private       long viewAddressStatic;
    private       long eventAddressStatic;
    private       long mapHandler;
    private       long viewAddress;

    public static int id;

    public int internalWidth  = 22000;
    public int internalHeight = 11000;

    public int clientWidth;
    public int clientHeight;

    public double boundX;
    public double boundY;
    public double boundMaxX;
    public double boundMaxY;

    public MapManager(Core core) {

        INSTANCE = this;

        this.hero = core.getHeroManager();
        this.starManager = core.getStarManager();

        this.worker = new ThreadWorker();
        this.worker.start();
    }

    @Override
    public void install(BotInstaller botInstaller) {

        botInstaller.screenManagerAddress.subscribe(value -> {
            mapAddressStatic = value + 256;
            viewAddressStatic = value + 216;
            eventAddressStatic = value + 200;
        });
    }

    void update() {

        long temp = API.readMemoryLong(mapAddressStatic);

        if (temp == 0)
            return;

        if (address != temp)
            update(temp);

        updateBounds();
    }

    private void update(long address) {

        MapManager.address = address;

        internalWidth = API.readMemoryInt(address + 68);
        internalHeight = API.readMemoryInt(address + 72) + 400;
        int tempId = API.readMemoryInt(address + 76);

        if (tempId != id) {
            id = tempId;
            hero.map = starManager.fromId(id);
        }
    }

    public void tick() {
        checkMirror();
    }

    void checkMirror() {
        long temp = API.readMemoryLong(eventAddressStatic) + 56;

        if (API.readMemoryBoolean(temp))
            API.writeMemoryInt(temp, 0);
    }

    void updateBounds() {

        long temp = API.readMemoryLong(viewAddressStatic);

        if (mapHandler != temp) {
            mapHandler = temp;
            viewAddress = API.readMemoryLong(mapHandler + 208);
        }

        if (viewAddress == 0)
            return;

        clientWidth = API.readMemoryInt(viewAddress + 168);
        clientHeight = API.readMemoryInt(viewAddress + 172);

        long boundsAddress = API.readMemoryLong(viewAddress + 280);
        boundsAddress = API.readMemoryLong(boundsAddress + 112);

        boundX = API.readMemoryDouble(boundsAddress + 80);
        boundY = API.readMemoryDouble(boundsAddress + 88);
        boundMaxX = API.readMemoryDouble(boundsAddress + 112);
        boundMaxY = API.readMemoryDouble(boundsAddress + 120);
    }

    public boolean isTarget(Entity entity) {
        return API.readMemoryLong(API.readMemoryLong(address + 120) + 40) == entity.address;
    }

    public double distanceOutOfMap(Location loc) {

        double x = loc.x;
        double y = loc.y;

        int width  = internalWidth;
        int height = internalHeight;

        if (x < 0 && y < 0) {
            return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        } else if (x > width && y > height) {
            return Math.sqrt(Math.pow(x - width, 2) + Math.pow(y - height, 2));
        } else if (x < 0 && y > height) {
            return Math.sqrt(Math.pow(x, 2) + Math.pow(y - height, 2));
        } else if (x > width && y < 0) {
            return Math.sqrt(Math.pow(x - width, 2) + Math.pow(y, 2));
        } else if (x > width) {
            return x - width;
        } else if (y > height) {
            return y - height;
        } else if (x < 0) {
            return -x;
        } else if (y < 0) {
            return -y;
        } else {
            return 0;
        }
    }

    public boolean isOutOfMap(Location loc) {
        return loc.x < 800 || loc.y < 800 || loc.x > internalWidth - 800 || loc.y > internalHeight - 800;
    }

    public boolean isInTop(Location loc) {

        double s = internalHeight * 0.75 * loc.x;
        double t = internalWidth * 0.75 * loc.y;

        return (s < 0) == (t < 0) && s >= 0 && s + t <= internalWidth * internalHeight;
    }

    public boolean isCurrentTargetOwned() {
        long temp = API.readMemoryLong(viewAddressStatic);

        if (temp == 0)
            return false;

        temp = API.readMemoryLong(temp + 216);
        temp = API.readMemoryLong(temp + 200);
        temp = API.readMemoryLong(temp + 48);

        return API.readMemoryInt(temp + 40) == 1;
    }

    public void move(Location location) {

        long eventAddress = API.readMemoryLong(eventAddressStatic);

        if (eventAddress == 0)
            return;

        location = location.copy();

        worker.address = API.readMemoryLong(eventAddress + 64);
        worker.x = location.x;
        worker.y = location.y;

        API.writeMemoryInt(eventAddress + 44, 0);

        worker.unpause();
        clickCenter();
        worker.pause();

        API.writeMemoryInt(eventAddress + 44, 0);
    }

    public void clickCenter() {
        API.mousePress(clientWidth / 2, clientHeight / 2);
    }

    private static class ThreadWorker extends Thread {

        private final    Object  mutex;
        private volatile boolean paused;
        private          long    address;
        private          double  x;
        private          double  y;

        public ThreadWorker() {
            this.mutex = new Object();
            this.paused = true;
        }

        public void unpause() {
            paused = false;

            synchronized (mutex) {
                mutex.notifyAll();
            }
        }

        public void pause() {
            paused = true;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (mutex) {

                    if (paused) {
                        try {
                            mutex.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    API.writeMemoryDouble(address + 32, x);
                    API.writeMemoryDouble(address + 40, y);
                }
            }
        }
    }


}
