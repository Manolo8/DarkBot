package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.config.Config;
import com.github.manolo8.darkbot.core.entities.Box;
import com.github.manolo8.darkbot.core.itf.Module;
import com.github.manolo8.darkbot.core.manager.HangarManager;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.core.manager.PetManager;
import com.github.manolo8.darkbot.core.manager.StatsManager;
import com.github.manolo8.darkbot.core.utils.Drive;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import static com.github.manolo8.darkbot.Main.API;

public class PaladiumModule implements Module {

    /**
     * Paladium Module Test v0.0.2
     * Made by @Dm94Dani
     */

    private LootModule lootModule;
    private CollectorModule collectorModule;

    private PetManager pet;
    private HeroManager hero;
    private Drive drive;
    private Config config;
    private StatsManager statsManager;

    private String hangarPalladium = ""; /* Put your hangar id */
    private String hangerBase = ""; /* Put your hangar id */
    private String hangarActive = "";
    private long lastCheckupHangar = 0;
    private Main main;
    private HangarManager hangarManager;

    public PaladiumModule() {
        this.lootModule = new LootModule();
        this.collectorModule = new CollectorModule();
    }

    @Override
    public void install(Main main) {
        lootModule = new LootModule();
        collectorModule = new CollectorModule();
        lootModule.install(main);
        collectorModule.install(main);

        this.hangarManager = new HangarManager(main);
        this.main = main;
        this.pet = main.guiManager.pet;
        this.hero = main.hero;
        this.drive = main.hero.drive;
        this.config = main.config;
        this.statsManager = main.statsManager;
    }

    @Override
    public String status() {
        return "Loot: " + lootModule.status() + " - Collect: " + collectorModule.status();
    }

    @Override
    public boolean canRefresh() {

        if(collectorModule.isNotWaiting()) {
            return lootModule.canRefresh();
        }

        return false;
    }

    @Override
    public void tick() {
        if (lastCheckupHangar <= System.currentTimeMillis() - 300000 && main.backpage.sidStatus().contains("OK")) {
            hangarActive = hangarManager.getActiveHangar();
            lastCheckupHangar = System.currentTimeMillis();
        }

        if (statsManager.deposit >= statsManager.depositTotal && statsManager.depositTotal != 0) {
            if (hangarActive == hangerBase) {

            } else {
                hangarManager.changeHangar(hangerBase);
            }

        } else if(hangarActive == hangarPalladium) {
            if (collectorModule.isNotWaiting() && lootModule.checkDangerousAndCurrentMap()) {
                pet.setEnabled(true);

                if (lootModule.findTarget()) {

                    collectorModule.findBox();

                    Box box = collectorModule.current;

                    if (box == null || box.locationInfo.distance(hero) > config.LOOT_COLLECT.RADIUS
                            || lootModule.attack.target.health.hpPercent() < 0.25) {
                        lootModule.moveToAnSafePosition();
                    } else {
                        collectorModule.tryCollectNearestBox();
                    }

                    lootModule.ignoreInvalidTarget();
                    lootModule.attack.doKillTargetTick();

                } else {
                    hero.roamMode();
                    collectorModule.findBox();

                    if (!collectorModule.tryCollectNearestBox() && (!drive.isMoving() || drive.isOutOfMap())) {
                        drive.moveRandom();
                }

                }
            }
        } else {
            hangarActive = hangarManager.getActiveHangar();
        }
    }

}
