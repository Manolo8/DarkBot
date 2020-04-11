package com.github.manolo8.darkbot.backpage.types;

public class NpcRank {

    public String name;

    public int pointsPerDestruction;

    public int totalDestruction;
    public int sessionDestruction;

    public void update(int destruction) {

        if (totalDestruction != 0)
            sessionDestruction += destruction - totalDestruction;

        totalDestruction = destruction;
    }

}
