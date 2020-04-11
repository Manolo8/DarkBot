package com.github.manolo8.darkbot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final Gson                    gson;
    private final HashMap<String, Object> configs;
    private final CommonConfig            commonConfig;

    public ConfigManager() {
        this.configs = new HashMap<>();
        this.gson = new GsonBuilder().create();
        this.commonConfig = loadOrCreate(CommonConfig.class, "config/config.json");
    }

    public <C> C loadOrCreate(Class<C> clazz, String folder) {

        C config = load(clazz, folder);

        if (config == null)
            config = create(clazz, folder);

        configs.put(folder, config);

        return config;
    }

    public void unload(String folder) {
        Object config = configs.remove(folder);

        if (config != null)
            save(config, folder);
    }

    public CommonConfig getCommonConfig() {
        return commonConfig;
    }

    private <C> C load(Class<C> clazz, String folder) {
        File file = new File(folder);

        C config;

        if (file.exists()) {
            FileReader reader = null;
            try {
                reader = new FileReader(file);

                config = gson.fromJson(reader, clazz);

                reader.close();

                return config;
            } catch (IOException ignored) {
                throw new Error("Can't load config from class " + clazz.getName());
            }
        }

        return null;
    }

    private <C> C create(Class<C> clazz, String folder) {

        try {
            //noinspection unchecked
            Constructor<C> constructor = (Constructor<C>) clazz.getDeclaredConstructors()[0];

            constructor.setAccessible(true);

            C config = constructor.newInstance();

            save(config, folder);

            return config;

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new Error("Can't create config from class " + clazz.getName());
        }
    }

    private void save(Object object, String folder) {
        File file = new File(folder);

        try {

            createFolderIfNotExists(folder);

            FileWriter writer = new FileWriter(file);

            gson.toJson(object, writer);

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFolderIfNotExists(String folder) {
        folder = folder.substring(0, folder.lastIndexOf('/'));
        File file = new File(folder);
        file.mkdirs();
    }

    public void saveConfigs() {
        for (Map.Entry<String, Object> configs : configs.entrySet())
            save(configs.getValue(), configs.getKey());
    }
}
