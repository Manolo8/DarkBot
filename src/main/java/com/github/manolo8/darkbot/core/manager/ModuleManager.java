package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.core.itf.Manager;
import com.github.manolo8.darkbot.core.utils.module.Module;
import com.github.manolo8.darkbot.core.utils.module.ModuleAndInfo;
import com.github.manolo8.darkbot.core.utils.module.ModuleBootstrap;
import com.github.manolo8.darkbot.modules.*;

import java.util.List;

public class ModuleManager
        implements Manager {

    private final HeroManager hero;
    private final DriveManager driveManager;

    private final CommonConfig commonConfig;

    private List<ModuleAndInfo> modules;

    private ModuleAndInfo moduleInfo;
    private Module        module;

    public ModuleManager(Core core) {

        this.hero = core.getHeroManager();
        this.driveManager = core.getDriveManager();
        this.commonConfig = core.getCommonConfig();
    }

    public void init(Core core) {
        this.modules =
                new ModuleBootstrap(
                        core,
                        CollectorModule.class,
                        LootModule.class,
                        LootNCollectorModule.class,
                        DefensiveCollectorModule.class,
                        GateModule.class,
//                        VortexModule.class,
                        MapModule.class,
                        EscapeModule.class,
                        PalladiumTraderModule.class
                ).build();

        setModuleByName(commonConfig.CURRENT_MODULE);
    }

    public Module getCurrentModule() {
        return module;
    }

    public ModuleAndInfo getCurrentModuleInfo() {
        return moduleInfo;
    }

    public <E extends Module> E setModule(Class<E> clazz) {

        driveManager.stop(true);

        for (ModuleAndInfo info : modules)
            if (info.getModuleClass() == clazz) {
                //noinspection unchecked
                return (E) setModuleByModuleAndInfo(info);
            }

        throw new Error("The module is not registered!");
    }

    private Module setModuleByModuleAndInfo(ModuleAndInfo info) {
        Module module = info.getOrCreateModule();

        module.resume();

        this.module = module;
        this.moduleInfo = info;

        return module;
    }

    public void setModuleByName(String name) {
        setModuleByModuleAndInfo(getModuleAndInfo(name));
    }

    public List<ModuleAndInfo> getModulesAndInfo() {
        return modules;
    }

    public ModuleAndInfo getModuleAndInfo(String name) {
        for (ModuleAndInfo info : modules)
            if (info.getName().equals(name))
                return info;

        return modules.get(0);
    }

    void tick() {
        if (module != null && hero.map.id != -1)
            module.tick();
    }
}
