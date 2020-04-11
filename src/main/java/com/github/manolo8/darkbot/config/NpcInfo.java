package com.github.manolo8.darkbot.config;

import java.util.HashSet;

public class NpcInfo {

    public String name;

    public int priority;

    public HashSet<Integer> mapList = new HashSet<>();

    public boolean kill;
    public boolean circle;
    public double  radius;
    public char    ammo;
    public boolean kamikaze;
}
