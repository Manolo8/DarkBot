package com.github.manolo8.darkbot.core.utils.pathfinder;

import com.github.manolo8.darkbot.view.draw.Drawable;
import com.github.manolo8.darkbot.view.draw.GraphicDrawer;

import java.util.HashSet;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class PathPoint {

    public int x;
    public int y;

    public int f;
    public int g;
    public int s;

    private PathFinder         finder;
    private HashSet<PathPoint> lineOfSight;
    private int                hashCode;

    public PathPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double distance(PathPoint o) {
        return sqrt(pow(x - o.x, 2) + pow(y - o.y, 2));
    }

    public void setFinder(PathFinder finder) {
        this.lineOfSight = null;
        this.finder = finder;
    }

    public HashSet<PathPoint> getLineOfSight() {
        if (lineOfSight == null) {
            lineOfSight = new HashSet<>();

            if (finder != null) {
                for (PathPoint point : finder.points)
                    if (point != this)
                        if (finder.hasLineOfSight(point, this))
                            lineOfSight.add(point);
            }
        }
        return lineOfSight;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {

            int var1 = 1664525 * this.x + 1013904223;
            int var2 = 1664525 * (this.y ^ -559038737) + 1013904223;

            hashCode = var1 ^ var2;
        }

        return hashCode;
    }
}
