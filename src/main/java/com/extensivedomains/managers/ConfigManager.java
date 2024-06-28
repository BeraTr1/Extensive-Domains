package com.extensivedomains.managers;

import com.extensivedomains.ExtensiveDomains;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private ExtensiveDomains plugin;
    private File pluginFolder;

    public ConfigManager(ExtensiveDomains plugin) {
        this.plugin = plugin;
        this.pluginFolder = plugin.getDataFolder();
    }

    public YamlConfiguration loadConfig(File file) {
        boolean fileExists = file != null && file.exists();

        if (!fileExists) {
            return null;
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public void saveConfig(YamlConfiguration config, File file) throws IOException {
        if (config == null || file == null) {
            return;
        }

        config.save(file);
    }

    public List<String> getConfigKeys(YamlConfiguration config) {
        return new ArrayList<>(config.getKeys(false));
    }

    public String getConfigString(YamlConfiguration config, String path) {
        return config.getString(path.toLowerCase());
    }

    public int getConfigInteger(YamlConfiguration config, String path) {
        return config.getInt(path.toLowerCase());
    }

    public List<String> getListString(YamlConfiguration config, String path) {
        return config.getStringList(path.toLowerCase());
    }
}
