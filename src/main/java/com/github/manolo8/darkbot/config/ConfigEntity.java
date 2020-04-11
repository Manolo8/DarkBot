package com.github.manolo8.darkbot.config;

import com.github.manolo8.darkbot.core.manager.MapManager;

public class ConfigEntity {

    public static ConfigEntity INSTANCE;

    private final CommonConfig commonConfig;

    ConfigEntity(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    public static void init(CommonConfig commonConfig) {
        INSTANCE = new ConfigEntity(commonConfig);
    }

    public NpcInfo getOrCreateNpcInfo(String name) {

        int mapId = MapManager.id;
        name = nameTranslate(name);


        NpcInfo info = commonConfig.npcInfos.get(name);

        if (info == null) {
            info = new NpcInfo();

            info.name = name;
            info.circle = true;
            info.radius = 500;
            info.mapList.add(mapId);

            if (!name.equals("ERROR") && !name.isEmpty()) {

                commonConfig.npcInfos.put(name, info);
                commonConfig.addedNpc.next(info);
                commonConfig.changed = true;

            }

        } else if (info.mapList.add(mapId))
            commonConfig.changed = true;

        return info;
    }

    public BoxInfo getOrCreateBoxInfo(String name) {

        BoxInfo info = commonConfig.boxInfos.get(name);

        if (info == null) {

            info = new BoxInfo();

            info.name = name;

            if (!name.equals("ERROR") && !name.isEmpty()) {
                commonConfig.boxInfos.put(name, info);
                commonConfig.addedBox.next(info);
                commonConfig.changed = true;
            }
        }

        return info;
    }

    public static String nameTranslate(String original) {

        if (!original.isEmpty() && original.charAt(0) != original.charAt(original.length() - 1)) {

            int index;

            if ((index = original.lastIndexOf('α')) != -1) {
                return original.substring(0, index) + "ALPHA";
            } else if ((index = original.lastIndexOf('β')) != -1) {
                return original.substring(0, index) + "BETA";
            } else if ((index = original.lastIndexOf('γ')) != -1) {
                return original.substring(0, index) + "GAMMA";
            } else if ((index = original.lastIndexOf('δ')) != -1) {
                return original.substring(0, index) + "DELTA";
            } else if ((index = original.lastIndexOf('ε')) != -1) {
                return original.substring(0, index) + "EPSILON";
            } else if ((index = original.lastIndexOf('ζ')) != -1) {
                return original.substring(0, index) + "ZETA";
            } else if ((index = original.lastIndexOf('κ')) != -1) {
                return original.substring(0, index) + "KAPPA";
            } else if ((index = original.lastIndexOf('λ')) != -1) {
                return original.substring(0, index) + "LAMBDA";
            } else if ((index = original.lastIndexOf('৩')) != -1) {
                return original.substring(0, index) + "VORTEX";
            }

        }

        return original;
    }
}
