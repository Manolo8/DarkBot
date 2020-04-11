package com.github.manolo8.darkbot.core.entities;

import com.github.manolo8.darkbot.core.objects.Health;
import com.github.manolo8.darkbot.core.objects.PlayerInfo;
import com.github.manolo8.darkbot.core.objects.ShipInfo;
import com.github.manolo8.darkbot.core.objects.Location;

import static com.github.manolo8.darkbot.core.manager.Core.API;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public abstract class Ship
        extends Entity {

    public Health     health;
    public PlayerInfo playerInfo;
    public ShipInfo   shipInfo;
    public boolean    invisible;

    public Ship(int id) {
        super(id);

        this.health = new Health();
        this.playerInfo = new PlayerInfo();
        this.shipInfo = new ShipInfo();
    }

    @Override
    public void update() {

        if (address == 0)
            return;

        super.update();

        health.update();
        shipInfo.update();
        playerInfo.update();

        invisible = API.readMemoryBoolean(API.readMemoryLong(address + 160) + 32);
    }

    @Override
    public void update(long address) {

        super.update(address);

        if (address == 0)
            return;

        playerInfo.update(API.readMemoryLong(address + 248));
        health.update(API.readMemoryLong(address + 184));
        shipInfo.update(API.readMemoryLong(address + 232));
    }

    public boolean isAttacking(Ship other) {
        return shipInfo.target == other.address;
    }

    public int diffAngle(Entity other) {

        int angleOne = shipInfo.angle;
        int angleTwo = (int) (location.angle(other.location) * 57.2957795130823);

        if (angleTwo < 0)
            angleTwo += 360;

        return Math.abs(angleOne - angleTwo);
    }

    public Location destinationInTime(long time) {

        if (!shipInfo.destination.valid)
            return location;

        double maxDistance  = shipInfo.destination.distance(location);
        double timeDistance = (time * shipInfo.speed) / 1000;

        double distance = Math.min(maxDistance, timeDistance);

        double angle = location.angle(shipInfo.destination);

        return new Location(
                location.x - cos(angle) * distance,
                location.y - sin(angle) * distance
        );
    }

    public boolean isMoving() {
        return shipInfo.destination.valid;
    }

    public boolean isGoingTo(Location location) {
        return this.location.equals(location) || shipInfo.destination.equals(location);
    }

    public boolean isGoingAway(Location location) {
        return shipInfo.destination.valid && shipInfo.destination.distance(location) > this.location.distance(location);
    }
}
