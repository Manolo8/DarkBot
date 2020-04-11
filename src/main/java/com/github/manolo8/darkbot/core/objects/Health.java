package com.github.manolo8.darkbot.core.objects;

import com.github.manolo8.darkbot.core.itf.Updatable;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class Health
        extends Updatable {

    public int hp;
    public int maxHp;
    public int shield;
    public int maxShield;

    public long lastIncreased;
    public long lastDecreased;

    @Override
    public void update() {

        if (address == 0)
            return;

        int hpLast = hp;

        hp = API.readMemoryInt(API.readMemoryLong(address + 48) + 40);
        maxHp = API.readMemoryInt(API.readMemoryLong(address + 56) + 40);
        shield = API.readMemoryInt(API.readMemoryLong(address + 80) + 40);
        maxShield = API.readMemoryInt(API.readMemoryLong(address + 88) + 40);

        if (hpLast > hp)
            lastDecreased = System.currentTimeMillis();
        else if (hpLast < hp)
            lastIncreased = System.currentTimeMillis();
    }

    public void reset() {
        lastIncreased = System.currentTimeMillis();
        lastDecreased = System.currentTimeMillis();
    }

    public double hpPercent() {
        return maxHp == 0 ? 1 : ((double) hp / (double) maxHp);
    }

    public double shieldPercent() {
        return maxShield == 0 ? 1 : ((double) shield / (double) maxShield);
    }

    public boolean isDecreasedIn(int time) {
        return System.currentTimeMillis() - lastDecreased < time;
    }

    public boolean isIncreasedIn(int time) {
        return System.currentTimeMillis() - lastIncreased < time;
    }

    public int healthToFull() {
        return maxHp - hp;
    }

    public int shieldToFull() {
        return maxShield - shield;
    }
}
