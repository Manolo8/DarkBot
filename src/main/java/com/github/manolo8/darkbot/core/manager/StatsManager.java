package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.core.installer.BotInstaller;
import com.github.manolo8.darkbot.core.itf.Installable;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class StatsManager
        implements Installable {

    private long address;

    public double credits;
    public double uridium;
    public double experience;
    public double honor;
    public int    deposit;
    public int    depositTotal;
    public int    deaths;
    public int    petDeaths;

    private long    started;
    private long    runningTime;
    private boolean lastStatus;

    public double earnedCredits;
    public double earnedUridium;
    public double earnedExperience;
    public double earnedHonor;

    private StringBuilder builder;

    public StatsManager() {
        builder = new StringBuilder();
    }

    @Override
    public void install(BotInstaller botInstaller) {

        botInstaller.userDataAddress.subscribe(value -> address = value);
        botInstaller.status.subscribe(this::toggle);
    }


    void tick() {

        if (address == 0)
            return;

        updateCredits(API.readMemoryDouble(address + 288));
        updateUridium(API.readMemoryDouble(address + 296));
        updateExperience(API.readMemoryDouble(address + 312));
        updateHonor(API.readMemoryDouble(address + 320));

        deposit = API.readMemoryInt(API.readMemoryLong(address + 240) + 40);
        depositTotal = API.readMemoryInt(API.readMemoryLong(address + 248) + 40);
    }


    private void toggle(boolean running) {
        lastStatus = running;

        if (running)
            started = System.currentTimeMillis();
         else if (started != 0)
            runningTime += System.currentTimeMillis() - started;
    }

    private void updateCredits(double credits) {
        double diff = credits - this.credits;

        if (this.credits != 0 && diff > 0)
            earnedCredits += diff;

        this.credits = credits;
    }

    private void updateUridium(double uridium) {
        double diff = uridium - this.uridium;

        if (this.uridium != 0 && diff > 0)
            earnedUridium += diff;

        this.uridium = uridium;
    }

    private void updateExperience(double experience) {

        if (this.experience != 0)
            earnedExperience += experience - this.experience;

        this.experience = experience;
    }

    private void updateHonor(double honor) {

        if (this.honor != 0)
            earnedHonor += honor - this.honor;

        this.honor = honor;
    }

    public long runningTime() {
        return runningTime + (lastStatus ? (System.currentTimeMillis() - started) : 0);
    }

    public String runningTimeStr() {
        builder.setLength(0);

        int seconds = (int) (runningTime() / 1000);
        int hours   = seconds / 3600;

        int minutes = (seconds / 60) % 60;

        seconds = seconds % 60;

        if (hours > 0) {

            if (hours < 10)
                builder.append('0');

            builder.append(hours).append(':');
        }

        if (minutes > 0) {

            if (minutes < 10)
                builder.append('0');

            builder.append(minutes).append(':');
        }

        if (seconds < 10)
            builder.append('0');

        builder.append(seconds);

        return builder.toString();
    }

    public double earnedCredits() {
        return earnedCredits == 0 ? 0 : earnedCredits / ((double) runningTime() / 3600000);
    }

    public double earnedUridium() {
        return earnedUridium == 0 ? 0 : earnedUridium / ((double) runningTime() / 3600000);
    }

    public double earnedExperience() {
        return earnedExperience == 0 ? 0 : earnedExperience / ((double) runningTime() / 3600000);
    }

    public double earnedHonor() {
        return earnedHonor == 0 ? 0 : earnedHonor / ((double) runningTime() / 3600000);
    }
}
