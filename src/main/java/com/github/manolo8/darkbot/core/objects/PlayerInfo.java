package com.github.manolo8.darkbot.core.objects;

import com.github.manolo8.darkbot.core.itf.Updatable;

import static com.github.manolo8.darkbot.core.manager.Core.APIU;
import static com.github.manolo8.darkbot.core.manager.HeroManager.instance;

public class PlayerInfo
        extends Updatable {

    public int    clanId;
    public int    clanDiplomacy;
    public String clanTag  = "";
    public String username = "";
    public int    factionId;
    public int    rank;
    public int    gg;

    public boolean isEnemy() {
        return (clanDiplomacy != 0 || clanId != 0 || factionId != 0) && (factionId != instance.playerInfo.factionId && clanDiplomacy != 1 && clanDiplomacy != 2 || clanDiplomacy == 3);
    }

    @Override
    public void update() {

        if (address == 0)
            return;

        clanId = APIU.readIntFromIntHolder(address, 40);
        clanDiplomacy = APIU.readIntFromIntHolder(address, 48);
        factionId = APIU.readIntFromIntHolder(address, 72);
        rank = APIU.readIntFromIntHolder(address, 80);
        gg = APIU.readIntFromIntHolder(address, 88);

        //noinspection StringEquality
        if (username.isEmpty() || username == "ERROR") {
            clanTag = APIU.readStringFromStringHolder(address, 56);
            username = APIU.readStringFromStringHolder(address, 64);
        }

    }
}
