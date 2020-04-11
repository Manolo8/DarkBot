package com.github.manolo8.darkbot.core.utils.pathfinder;

import java.util.*;

public class PathFinderCalculator {

    private final PathPoint come;
    private final PathPoint destination;

    private final Set<PathPoint> closedList;
    private final Set<PathPoint> openList;

    private final List<PathPoint> fragmentedPath;

    private boolean invalid;

    public PathFinderCalculator(PathPoint come,
                                PathPoint destination) {

        this.come = come;
        this.destination = destination;

        this.fragmentedPath = new ArrayList<>();
        this.closedList = new HashSet<>();
        this.openList = new HashSet<>();
    }

    public void fillGeneratedPathTo(LinkedList<PathPoint> target) {

        addDefs();

        build();

        if (!invalid)
            unfragment(target);

        remDefs();

    }

    private void addDefs() {
        for (PathPoint temp : destination.getLineOfSight())
            temp.getLineOfSight().add(destination);

        for (PathPoint temp : come.getLineOfSight())
            temp.getLineOfSight().add(come);
    }

    private void remDefs() {
        for (PathPoint temp : destination.getLineOfSight())
            temp.getLineOfSight().remove(destination);

        for (PathPoint temp : come.getLineOfSight())
            temp.getLineOfSight().remove(come);
    }

    private void build() {

        PathPoint current = come;

        current.f = (int) destination.distance(come);
        current.g = 0;
        current.s = 0;

        openList.add(current);

        fragmentedPath.add(come);

        do {

            if (current == null) {
                invalid = true;
                break;
            }

            openList.remove(current);
            closedList.add(current);

            update(current);
        }
        while ((current = pickupOne()) != destination);
    }

    private void update(PathPoint current) {
        for (PathPoint neighbor : current.getLineOfSight()) {

            if (closedList.contains(neighbor))
                continue;

            int g = current.g + (int) current.distance(neighbor);

            if (!openList.add(neighbor) && g >= neighbor.g)
                continue;

            neighbor.g = g;
            neighbor.s = current.s + 1;
            neighbor.f = g + (int) destination.distance(neighbor);

            fragmentedPath.add(neighbor);
        }
    }

    private void unfragment(LinkedList<PathPoint> target) {
        PathPoint current = destination;

        do {
            target.addFirst(current);
        } while ((current = next(current)) != come);
    }

    private PathPoint next(PathPoint current) {

        PathPoint closest = null;
        int       sum     = 0;

        for (PathPoint loop : fragmentedPath) {

            if (!current.getLineOfSight().contains(loop)) continue;

            int csum = loop.g + (int) loop.distance(current);

            if (current.s == loop.s + 1 && (closest == null || csum < sum)) {
                closest = loop;
                sum = csum;
            }

        }

        return closest;
    }

    private PathPoint pickupOne() {
        PathPoint better = null;

        for (PathPoint loop : openList) {
            if (better == null || loop.f < better.f) {
                better = loop;
            }
        }

        return better;
    }
}
