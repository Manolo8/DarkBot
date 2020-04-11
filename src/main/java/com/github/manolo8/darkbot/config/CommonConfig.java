package com.github.manolo8.darkbot.config;

import com.github.manolo8.darkbot.core.utils.Observable;

import java.util.HashMap;

public class CommonConfig {

    public int WORKING_MAP = 1;

    public ShipConfig OFFENSIVE_CONFIG = new ShipConfig('8', 1);
    public ShipConfig RUN_CONFIG       = new ShipConfig('9', 2);

    public int REFRESH_TIME = 1024;

    public String CURRENT_MODULE = "";

    public boolean ENABLE_PET;

    public int MAX_DEATHS     = 10;
    public int MAX_PET_DEATHS = 1000;

    public int RESPAWN           = 0;
    public int REPAIR_LOCAL_TIME = 5;

    public double REPAIR_HP = 0.6;

    //ENTITIES
    public HashMap<String, BoxInfo> boxInfos = new HashMap<>();
    public HashMap<String, NpcInfo> npcInfos = new HashMap<>();
    //ENTITIES

    public transient boolean changed;

    public transient Observable<BoxInfo> addedBox = new Observable<>();
    public transient Observable<NpcInfo> addedNpc = new Observable<>();
}
