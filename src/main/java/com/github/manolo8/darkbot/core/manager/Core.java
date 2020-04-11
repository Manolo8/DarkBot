package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.backpage.BackPageManager;
import com.github.manolo8.darkbot.backpage.auth.AuthenticationResult;
import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.config.ConfigEntity;
import com.github.manolo8.darkbot.config.ConfigManager;
import com.github.manolo8.darkbot.core.DarkFlash;
import com.github.manolo8.darkbot.core.DarkFlashUtils;
import com.github.manolo8.darkbot.core.installer.BotInstaller;
import com.github.manolo8.darkbot.core.manager.step.StepToReconnect;
import com.github.manolo8.darkbot.core.manager.step.StepToRefresh;
import com.github.manolo8.darkbot.core.manager.step.StepToRevive;
import com.github.manolo8.darkbot.core.utils.Clock;
import com.github.manolo8.darkbot.core.utils.Observable;

public class Core
        implements Runnable {

    public static final Clock          WAIT = new Clock();
    public static       DarkFlash      API;
    public static       DarkFlashUtils APIU;
    private final       ConfigManager  configManager;

    private final BotInstaller botInstaller;

    private AuthenticationResult authentication;

    private final BackPageManager backPageManager;

    private final ItemManager      itemManager;
    private final MapManager       mapManager;
    private final StarManager      starManager;
    private final HeroManager      heroManager;
    private final PetManager       petManager;
    private final GuiManager       guiManager;
    private final StatsManager     statsManager;
    private final PingManager      pingManager;
    private final DriveManager     driveManager;
    private final EntityManager    entityManager;
    private final SchedulerManager schedulerManager;
    private final ModuleManager    moduleManager;

    private final CommonConfig commonConfig;

    private boolean running;

    private int tick;

    public final Observable<Boolean> status;

    public Core() {

        API = new DarkFlash();
        APIU = new DarkFlashUtils();

        authentication = new AuthenticationResult("", 0, "", "", "", "");

        botInstaller = new BotInstaller();
        status = botInstaller.status;

        backPageManager = new BackPageManager();

        configManager = new ConfigManager();
        commonConfig = configManager.getCommonConfig();
        pingManager = new PingManager();
        guiManager = new GuiManager();
        heroManager = new HeroManager();
        starManager = new StarManager(this);
        mapManager = new MapManager(this);
        statsManager = new StatsManager();
        entityManager = new EntityManager(this);
        schedulerManager = new SchedulerManager(this);
        driveManager = new DriveManager(this);
        moduleManager = new ModuleManager(this);
        petManager = new PetManager(this);
        itemManager = new ItemManager();

        botInstaller.addManager(mapManager);
        botInstaller.addManager(starManager);
        botInstaller.addManager(heroManager);
        botInstaller.addManager(petManager);
        botInstaller.addManager(guiManager);
        botInstaller.addManager(statsManager);
        botInstaller.addManager(pingManager);
        botInstaller.addManager(driveManager);
        botInstaller.addManager(entityManager);
        botInstaller.addManager(schedulerManager);
        botInstaller.addManager(moduleManager);
        botInstaller.addManager(itemManager);

        botInstaller.init();

        botInstaller.addStep(new StepToRevive(
                botInstaller.screenManagerAddress,
                botInstaller.guiManagerAddress,
                botInstaller.mainAddress,
                schedulerManager,
                statsManager,
                mapManager,
                commonConfig
        ));

        botInstaller.addStep(new StepToReconnect(
                botInstaller.mainApplicationAddress,
                schedulerManager,
                pingManager,
                guiManager
        ));

        botInstaller.addStep(new StepToRefresh(
                botInstaller.mainApplicationAddress,
                heroManager,
                moduleManager,
                commonConfig
        ));

        moduleManager.init(this);

        ConfigEntity.init(getCommonConfig());

        Runtime.getRuntime().addShutdownHook(new Thread(configManager::saveConfigs));
    }

    public void auth(AuthenticationResult result) {

        this.authentication = result;

        this.backPageManager.auth(result);

        API.setCookie(authentication.base, "dosid=" + authentication.sid);
        API.load(authentication.preloader, authentication.vars, authentication.base);

        Thread thread = new Thread(this);
        thread.start();
    }

    public ItemManager getItemManager() {
        if (itemManager == null)
            throw new NullPointerException();
        return itemManager;
    }

    public ConfigManager getConfigManager() {
        if (configManager == null)
            throw new NullPointerException();
        return configManager;
    }

    public MapManager getMapManager() {
        if (mapManager == null)
            throw new NullPointerException();
        return mapManager;
    }

    public StarManager getStarManager() {
        if (starManager == null)
            throw new NullPointerException();
        return starManager;
    }

    public HeroManager getHeroManager() {
        if (heroManager == null)
            throw new NullPointerException();
        return heroManager;
    }

    public PetManager getPetManager() {
        if (petManager == null)
            throw new NullPointerException();
        return petManager;
    }

    public GuiManager getGuiManager() {
        if (guiManager == null)
            throw new NullPointerException();
        return guiManager;
    }

    public StatsManager getStatsManager() {
        if (statsManager == null)
            throw new NullPointerException();
        return statsManager;
    }

    public PingManager getPingManager() {
        if (pingManager == null)
            throw new NullPointerException();
        return pingManager;
    }

    public CommonConfig getCommonConfig() {
        if (commonConfig == null)
            throw new NullPointerException();
        return commonConfig;
    }

    public DriveManager getDriveManager() {
        if (driveManager == null)
            throw new NullPointerException();
        return driveManager;
    }

    public EntityManager getEntityManager() {
        if (entityManager == null)
            throw new NullPointerException();
        return entityManager;
    }

    public SchedulerManager getSchedulerManager() {
        if (schedulerManager == null)
            throw new NullPointerException();
        return schedulerManager;
    }

    public ModuleManager getModuleManager() {
        if (moduleManager == null)
            throw new NullPointerException();
        return moduleManager;
    }

    public BotInstaller getInstaller() {
        if (botInstaller == null)
            throw new NullPointerException();
        return botInstaller;
    }

    public void setRunning(boolean running) {
        this.running = running;
        this.status.next(running);
    }

    public AuthenticationResult getAuthenticationResult() {
        return authentication;
    }

    public boolean isRunning() {
        return running;
    }

    public void tick() {
        if (WAIT.isBigger(0L)) {

            botInstaller.check();
            backPageManager.tick();

            if (canUpdate())
                tickValid();
            else
                waitNextTry();
        }
    }

    private boolean canUpdate() {
        return botInstaller.canUpdate();
    }

    private boolean canTickModule() {
        return botInstaller.canTickModule() && running;
    }

    private void waitNextTry() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    private void tickValid() {

        tickUpdate();

        if (canTickModule()) {
            if (tick % 2 == 0)
                tickRunning();
            if (tick % 8 == 0)
                driveManager.tick();
        }
    }

    private void tickUpdate() {
        mapManager.update();
        heroManager.update();
        guiManager.update();
        heroManager.update();
        entityManager.update();
        pingManager.update();
        petManager.update();
        itemManager.update();
    }

    private void tickRunning() {

        mapManager.tick();
        statsManager.tick();
        guiManager.tick();

        if (schedulerManager.isFree()) {
            petManager.tick();
            moduleManager.tick();
        }

        schedulerManager.tick();
    }

    @Override
    public void run() {
        long start;

        while (true) {
            start = System.currentTimeMillis();
            tick();
            tick++;
            sleep(start);
        }
    }

    private void sleep(long start) {
        long ms = System.currentTimeMillis() - start;
        if (25L > ms) {
            try {
                Thread.sleep(25L - ms);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
