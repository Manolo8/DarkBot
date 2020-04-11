package com.github.manolo8.darkbot.core.utils.module;

public class ModuleAndInfo {

    final Class<? extends Module> clazz;
    final ModuleOptions           info;
    final ModuleConfig            config;

    Module        module;
    ModuleCreator creator;

    public ModuleAndInfo(Class<? extends Module> clazz, ModuleOptions info, ModuleConfig config) {
        this.clazz = clazz;
        this.info = info;
        this.config = config;
    }

    public String getName() {
        return info.value();
    }

    public <E extends Module> E getOrCreateModule() {
        //noinspection unchecked
        return (E) (info.alwaysNewInstance() ? creator.newInstance() : module);
    }

    public Class<? extends Module> getModuleClass() {
        return clazz;
    }

    public ModuleConfig getConfig() {
        return config;
    }

    public boolean showInModules() {
        return info.showInModules();
    }
}
