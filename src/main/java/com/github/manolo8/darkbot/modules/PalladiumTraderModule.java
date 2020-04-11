package com.github.manolo8.darkbot.modules;

import com.github.manolo8.darkbot.core.entities.BasePiece;
import com.github.manolo8.darkbot.core.itf.TempModule;
import com.github.manolo8.darkbot.core.manager.*;
import com.github.manolo8.darkbot.core.objects.Gui;
import com.github.manolo8.darkbot.core.objects.Location;
import com.github.manolo8.darkbot.core.objects.Map;
import com.github.manolo8.darkbot.core.utils.Clock;
import com.github.manolo8.darkbot.core.utils.module.ModuleOptions;

import java.util.List;

@ModuleOptions(
        value = "PalladiumTradeModule",
        showInModules = false
)
public class PalladiumTraderModule
        extends TempModule {

    private final Map map;

    private final HeroManager      hero;
    private final ModuleManager    moduleManager;
    private final DriveManager     drive;
    private final SchedulerManager scheduler;

    private final List<BasePiece> basePieces;

    private final Gui trade;

    private final Clock clock;

    private boolean active;

    public PalladiumTraderModule(Core core) {
        super(core);

        this.map = core.getStarManager().fromName("5-2");
        this.hero = core.getHeroManager();
        this.moduleManager = core.getModuleManager();
        this.drive = core.getDriveManager();
        this.scheduler = core.getSchedulerManager();
        this.basePieces = core.getEntityManager().basePieces;
        this.trade = core.getGuiManager().fromName("ore_trade");
        this.active = true;
        this.clock = new Clock();
    }

    @Override
    public void resume() {
        super.resume();
        active = true;
    }

    @Override
    public boolean canRefresh() {
        return false;
    }

    @Override
    public void tick() {

        trade.update();

        if (active)
            tickActive();
        else if (trade.show(false))
            back();
    }

    private void tickActive() {
        if (isInTradeMap())
            tickInTradeMap();
        else
            goToTradeMap();
    }

    private void tickInTradeMap() {
        if (isInPlace())
            tickInPlace();
        else
            goToPlace();
    }

    private void tickInPlace() {
        if (basePieces.size() == 1) {
            if (trade.visible)
                tradeOres();
            else
                openTrade();
        }
    }

    private void tradeOres() {
        if (clock.isBiggerThenReset(1500)) {
            trade.click(600, 180);
            active = false;
        }
    }

    private void openTrade() {
        if (clock.isBiggerThenReset(1000)) {
            BasePiece piece = basePieces.get(0);
            scheduler.asyncSelectTarget(piece, false);
        }
    }

    private boolean isInPlace() {
        Location location = hero.location;

        return location.x == 10000 && location.y == 6500;
    }

    private void goToPlace() {
        drive.move(10000, 6500);
    }

    private boolean isInTradeMap() {
        return hero.map == map;
    }

    private void goToTradeMap() {
        moduleManager.setModule(MapModule.class).setTarget(map);
    }
}
