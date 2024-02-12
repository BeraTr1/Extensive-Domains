package org.saulo.extensivedomains.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.saulo.extensivedomains.ExtensiveDomains;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConfigManager {
    private ExtensiveDomains plugin;
    private File pluginFolder;

    public ConfigManager(ExtensiveDomains plugin) {
        this.plugin = plugin;
        this.pluginFolder = plugin.getDataFolder();

        this.createDefaultConfigFiles();
    }

    private void createDefaultConfigFiles() {
        boolean pluginFolderExists = this.pluginFolder.exists();

        if (!pluginFolderExists) {
            this.pluginFolder.mkdir();
        }

        List<String> fileNames = new ArrayList<>();
        fileNames.add("domain-tiers.yml");

        for (String fileName : fileNames) {
            try {
                File file = new File(this.pluginFolder, fileName);
                boolean fileExists = file.exists();

                if (fileExists) {
                    continue;
                }

                InputStream inputStream = plugin.getClass().getResourceAsStream("/" + fileName);

                if (inputStream == null) {
//                    throw new Exception("Resource file " + fileName + " not found!");
                    System.out.println("Resource file " + fileName + " not found!");
                    return;
                }

                OutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;

                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.close();
                YamlConfiguration config = this.loadConfig(file);
                this.saveConfig(config, file);
                System.out.println("Created default file '" + fileName + "'");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public YamlConfiguration loadConfig(@NotNull File file) {
        boolean fileExists = file.exists();

        if (!fileExists) {
            return null;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        return config;
    }

    public YamlConfiguration loadConfig(String filePath) {
        File file = new File(pluginFolder, filePath);
        return loadConfig(file);
    }

    public void saveConfig(@NotNull YamlConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getConfigKeys(@NotNull YamlConfiguration config) {
        return config.getKeys(false);
    }

    public String getConfigString(@NotNull YamlConfiguration config, @NotNull String path) {
        return config.getString(path.toLowerCase());
    }

    public int getConfigInteger(@NotNull YamlConfiguration config, @NotNull String path) {
        return config.getInt(path.toLowerCase());
    }

    public List<String> getListString(@NotNull YamlConfiguration config, @NotNull String path) {
        return config.getStringList(path.toLowerCase());
    }
}
