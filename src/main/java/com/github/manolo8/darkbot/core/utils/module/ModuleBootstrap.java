package com.github.manolo8.darkbot.core.utils.module;

import com.github.manolo8.darkbot.config.ConfigManager;
import com.github.manolo8.darkbot.core.manager.Core;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public class ModuleBootstrap {

    private final Core                      core;
    private final ConfigManager             configManager;
    private final Class<? extends Module>[] modules;

    @SafeVarargs
    public ModuleBootstrap(Core core, Class<? extends Module>... modules) {
        this.core = core;
        this.configManager = core.getConfigManager();
        this.modules = modules;
    }

    public List<ModuleAndInfo> build() {

        List<ModuleAndInfo> modules = new ArrayList<>();

        for (Class<? extends Module> clazz : this.modules)
            modules.add(createModuleInfo(clazz));

        return modules;
    }

    private ModuleAndInfo createModuleInfo(Class<? extends Module> clazz) {

        Constructor   constructor = clazz.getConstructors()[0];
        ModuleOptions options     = clazz.getAnnotation(ModuleOptions.class);

        ModuleConfig config = createModuleConfig(constructor, options);

        return createModuleInfo(constructor, clazz, options, config);
    }

    private ModuleConfig createModuleConfig(Constructor constructor, ModuleOptions options) {

        Class configClass = searchConfigClass(constructor.getParameterTypes());

        if (configClass == null)
            return null;
        else
            return createConfigInstance(configClass, options.value());
    }

    private Class searchConfigClass(Class<?>[] parameters) {
        return Arrays.stream(parameters).filter(ModuleConfig.class::isAssignableFrom).findFirst().orElse(null);
    }

    private ModuleConfig createConfigInstance(Class configClass, String name) {
        return (ModuleConfig) configManager.loadOrCreate(configClass, "config/modules/" + name + ".json");
    }

    private ModuleAndInfo createModuleInfo(Constructor constructor, Class clazz, ModuleOptions options, ModuleConfig config) {

        ModuleAndInfo info = new ModuleAndInfo(clazz, options, config);

        info.module = createModule(constructor, config);

        if (options.alwaysNewInstance())
            info.creator = () -> createModule(constructor, config);

        return info;
    }

    private Module createModule(Constructor constructor, ModuleConfig config) {

        Class[] parameters = constructor.getParameterTypes();

        try {

            if (parameters.length == 0)
                return (Module) constructor.newInstance();
            else
                return (Module) constructor.newInstance(createParameters(parameters, core, config));

        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private Object[] createParameters(Class[] parameters, Object... objects) {

        int      length = parameters.length;
        Object[] build  = new Object[length];

        main:
        for (int i = 0; i < length; i++) {

            Class expected = parameters[i];

            for (Object object : objects) {
                if (expected.isAssignableFrom(object.getClass())) {
                    build[i] = object;
                    continue main;
                }
            }

            throw new Error(expected.getName() + " not found in offered classes!");
        }

        return build;
    }
}
