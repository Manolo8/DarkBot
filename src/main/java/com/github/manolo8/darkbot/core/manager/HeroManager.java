package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.core.entities.Pet;
import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.installer.BotInstaller;
import com.github.manolo8.darkbot.core.itf.Installable;
import com.github.manolo8.darkbot.core.objects.Map;
import com.github.manolo8.darkbot.view.draw.GraphicDrawer;

import static com.github.manolo8.darkbot.core.manager.Core.API;
import static com.github.manolo8.darkbot.view.draw.Palette.OWNER;

public class HeroManager
        extends Ship
        implements Installable {

    public static HeroManager instance;

    private long staticAddress;
    private long settingsAddress;

    public final Pet pet;

    public Map map;

    public Ship target;

    public int configId;
    public int formationId;

    public HeroManager() {
        super(0);
        instance = this;

        this.pet = new Pet(0);
        this.map = new Map(-1, "Loading");
    }

    @Override
    public void install(BotInstaller botInstaller) {
        botInstaller.screenManagerAddress.subscribe(value -> staticAddress = value + 240);
        botInstaller.settingsAddress.subscribe(value -> settingsAddress = value);
    }

    @Override
    public void update() {

        long address = API.readMemoryLong(staticAddress);

        if (this.address != address)
            update(address);

        if (address == 0)
            return;

        super.update();

        pet.update();

        configId = API.readMemoryInt(settingsAddress + 52);
        formationId = API.readMemoryInt(API.readMemoryLong(API.readMemoryLong(address + 280) + 40) + 40);

        long petAddress = API.readMemoryLong(address + 176);

        if (petAddress != pet.address)
            pet.update(petAddress);
    }

    @Override
    public void update(long address) {

        if (address == 0) {
            this.address = 0;
            return;
        }

        super.update(address);

        pet.update(API.readMemoryLong(address + 176));
        clickable.setRadius(0);
        id = API.readMemoryInt(address + 56);
    }

    public void setTarget(Ship entity) {
        this.target = entity;
    }

    @Override
    public void draw(GraphicDrawer drawer) {
        drawer.setColor(OWNER);
        drawer.set(location.x, location.y);
        drawer.fillOvalCenter(7, 7);
    }
}