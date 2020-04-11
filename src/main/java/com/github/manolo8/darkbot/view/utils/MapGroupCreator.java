package com.github.manolo8.darkbot.view.utils;

import com.github.manolo8.darkbot.core.manager.StarManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MapGroupCreator {

    private final StarManager    starManager;
    private final List<MapGroup> groups;

    public MapGroupCreator(StarManager starManager) {
        this.starManager = starManager;
        this.groups = new ArrayList<>();
    }

    public void addGroup(String name, String regex, long columns) {

        Pattern pattern = Pattern.compile(regex);

        int[] arr = starManager.getMaps().stream().filter(entry -> pattern.matcher(entry.name).matches()).mapToInt(value -> value.id).toArray();

        groups.add(new MapGroup(name, arr, columns));
    }

    public List<MapGroup> getGroups() {
        return groups;
    }
}
