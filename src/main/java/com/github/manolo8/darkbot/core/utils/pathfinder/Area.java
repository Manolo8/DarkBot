package com.github.manolo8.darkbot.core.utils.pathfinder;

import com.github.manolo8.darkbot.core.objects.Location;

import static java.lang.Math.min;

public class Area {

    public double  maxX;
    public double  minX;
    public double  maxY;
    public double  minY;
    public boolean changed;
    public boolean cachedUsing;

    public Area(double maxX, double minX, double maxY, double minY) {
        this.maxX = maxX;
        this.minX = minX;
        this.maxY = maxY;
        this.minY = minY;
        this.changed = true;
    }

    public boolean hasLineOfSight(PathPoint current, PathPoint destination) {
        return !collisionPoint(current.x, current.y, destination.x, destination.y);
    }

    public boolean intersect(Area o) {
        return (o.maxX >= minX &&
                o.maxY >= minY &&
                maxX >= o.minX &&
                maxY >= o.minY);
    }

    private boolean collisionPoint(double x1, double y1, double x2, double y2) {
        if (x1 < x2 && lineCollisionLocation(x1, y1, x2, y2, minX, minY, minX, maxY))
            return true;
        else if (x1 > x2 && lineCollisionLocation(x1, y1, x2, y2, maxX, minY, maxX, maxY))
            return true;
        else if (y1 < y2 && lineCollisionLocation(x1, y1, x2, y2, minX, minY, maxX, minY))
            return true;
        else return y1 > y2 && lineCollisionLocation(x1, y1, x2, y2, minX, maxY, maxX, maxY);
    }

    private boolean lineCollisionLocation(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {

        double v  = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        double uA = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / v;
        double uB = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / v;

        return uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1;
    }

    public PathPoint toSide(PathPoint point) {

        int diffLeft  = point.x - (int) minX;
        int diffRight = (int) maxX - point.x;

        int diffTop    = point.y - (int) minY;
        int diffBottom = (int) maxY - point.y;

        int min = min(diffBottom, min(min(diffTop, diffLeft), diffRight));

        if (min == diffTop)
            return new PathPoint(point.x, (int) (minY - 2));
        else if (min == diffBottom)
            return new PathPoint(point.x, (int) (maxY + 2));
        else if (min == diffLeft)
            return new PathPoint((int) minX - 2, point.y);
        else
            return new PathPoint((int) maxX + 2, point.y);
    }

    public boolean inside(int x, int y) {
        return (minX < x &&
                x < maxX &&
                minY < y &&
                y < maxY);
    }

    public void set(Location o, int addX, int addY) {
        this.maxX = o.x + addX;
        this.minX = o.x - addX;
        this.maxY = o.y + addY;
        this.minY = o.y - addY;

        changed = true;
    }

    public boolean canGrowTo(Area other) {
        return Math.abs(maxX - other.minX) < 10 && Math.abs(minY - other.minY) < 10 && Math.abs(maxY - other.maxY) < 10 ||
                Math.abs(maxY - other.minY) < 10 && Math.abs(minX - other.minX) < 10 && Math.abs(maxX - other.maxX) < 10;
    }

    public Area grow(Area other) {
        return new Area(Math.max(maxX, other.maxX), Math.min(minX, other.minX), Math.max(maxY, other.maxY), Math.min(minY, other.minY));
    }
}
