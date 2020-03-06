package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.core.entities.BasePoint;
import com.github.manolo8.darkbot.core.entities.Box;
import com.github.manolo8.darkbot.core.itf.InstructionProvider;
import com.github.manolo8.darkbot.core.manager.StatsManager;
import com.github.manolo8.darkbot.core.objects.Map;
import com.github.manolo8.darkbot.core.objects.OreTradeGui;
import com.github.manolo8.darkbot.core.utils.Location;
import com.github.manolo8.darkbot.extensions.features.Feature;
import com.github.manolo8.darkbot.modules.utils.SafetyFinder;

import java.util.List;

import static com.github.manolo8.darkbot.Main.API;
import static com.github.manolo8.darkbot.utils.Time.sleep;

@Feature(
        name = "Easy Cash By Mitred Module",
        description = "Loot & collect in 5-2 map, but when full cargo is full to sell"
)
public class EasyCash extends LootNCollectorModule implements InstructionProvider {
    private Map SELL_MAP;
    private Main main;
    private StatsManager statsManager;
    private List<BasePoint> bases;
    private OreTradeGui oreTrade;
    private long sellClick;

    public EasyCash() {
    }

    public String instructions() {
        return "Work in Progress!";
    }

    public void install(Main main) {
        super.install(main);
        this.SELL_MAP = main.starManager.byName("5-2");
        this.main = main;
        this.statsManager = main.statsManager;
        this.bases = main.mapManager.entities.basePoints;
        this.oreTrade = main.guiManager.oreTrade;
    }

    public void tick() {
        if (this.statsManager.deposit >= this.statsManager.depositTotal && this.statsManager.depositTotal != 0) {
            checkInvisibility();
            this.sell();
        } else if (System.currentTimeMillis() - 500L > this.sellClick && this.oreTrade.showTrade(false, (BasePoint)null)) {
            super.tick();
        }

    }

    private boolean canCollect(Box box) {
        return box.boxInfo.collect
                && !box.isCollected()
                && drive.canMove(box.locationInfo.now)
                && (!box.type.equals("FROM_SHIP") || main.statsManager.deposit < main.statsManager.depositTotal);
    }
    private long invisibleTime;
    public void checkInvisibility() {
        if (config.COLLECT.AUTO_CLOACK
                && !hero.invisible
                && System.currentTimeMillis() - invisibleTime > 60000
        ) {
            invisibleTime = System.currentTimeMillis();
            API.keyboardClick(config.COLLECT.AUTO_CLOACK_KEY);
        }
    }

    private void sell() {
        this.pet.setEnabled(false);
        if (this.hero.map != this.SELL_MAP) {
            ((MapModule)this.main.setModule(new MapModule())).setTarget(this.SELL_MAP);
        } else {
            this.bases.stream().filter((b) -> {
                return b.locationInfo.isLoaded();
            }).findFirst().ifPresent((base) -> {
                if (this.drive.movingTo().distance(base.locationInfo.now) > 200.0D) {
                    double angle = base.locationInfo.now.angle(this.hero.locationInfo.now) + Math.random() * 0.2D - 0.1D;
                    this.drive.move(Location.of(base.locationInfo.now, angle, 100.0D + 100.0D * Math.random()));
                } else if (!this.hero.locationInfo.isMoving() && this.oreTrade.showTrade(true, base) && System.currentTimeMillis() - 6000L > this.sellClick) {
                    this.oreTrade.sellOre(OreTradeGui.Ore.PROMETIUM);
                    sleep(500);
                    this.oreTrade.sellOre(OreTradeGui.Ore.PROMERIUM);
                    sleep(500);
                    this.oreTrade.sellOre(OreTradeGui.Ore.ENDRIUM);
                    sleep(500);
                    this.oreTrade.sellOre(OreTradeGui.Ore.TERBIUM);
                    sleep(500);
                    this.oreTrade.sellOre(OreTradeGui.Ore.PROMETID);
                    sleep(500);
                    this.oreTrade.sellOre(OreTradeGui.Ore.DURANIUM);
                    this.sellClick = System.currentTimeMillis();
                    sleep(500);
                }

            });
        }

    }
}
