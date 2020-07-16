package com.github.manolo8.darkbot.core.objects.facades;

import com.github.manolo8.darkbot.core.itf.Updatable;
import com.github.manolo8.darkbot.core.utils.ByteUtils;

import static com.github.manolo8.darkbot.Main.API;

public class ChrominProxy extends Updatable {

    public double currAmt, maxAmt;
    public double earnedAmt;

    public ChrominProxy() { this.currAmt = -1.0D; }

    @Override
    public void update() {
        if (address == 0) return;

        long data = API.readMemoryLong(address + 48) & ByteUtils.FIX;

        this.maxAmt = API.readMemoryDouble(data + 48);
        updateChromin(API.readMemoryDouble(data + 40));

        //this.currAmt = API.readMemoryDouble(data + 40);
    }

    public void updateChromin(double amt) {
        double diff = amt - currAmt;

        if (this.currAmt >= 0.0D && diff > 0.0D)
            earnedAmt += diff;

        this.currAmt = amt;
    }
}
