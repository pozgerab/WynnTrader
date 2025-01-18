package com.gertoxq.wynntrader.config;

import com.gertoxq.wynntrader.custom.Cast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Manager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config/wynntrader.json");
    private ConfigType config;

    public void loadConfig() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                config = new ConfigType();
                saveConfig();
            } else {
                BufferedReader reader = Files.newBufferedReader(CONFIG_PATH);
                config = GSON.fromJson(reader, ConfigType.class);
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH.getParent());
            }
            BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH);
            GSON.toJson(config, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigType getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    public void setConfig(ConfigType config) {
        this.config = config;
        saveConfig();
    }
}
