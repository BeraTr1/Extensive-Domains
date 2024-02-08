package org.saulo.extensivedomains.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.saulo.extensivedomains.ExtensiveDomains;
import org.yaml.snakeyaml.Yaml;

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

    public YamlConfiguration loadConfig(File file) {
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

    public void saveConfig(YamlConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void createConfigFile(File file, YamlConfiguration config) {
//        try {
//            config.save(file);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void createConfigFile(String path, YamlConfiguration config) {
//        File file = getFileFromPath(path);
//        createConfigFile(file, config);
//    }

//    public File getFileFromPath(String path) {
//        return new File(pluginFolder, path);
//    }

    public Set<String> getConfigKeys(YamlConfiguration config) {
        return config.getKeys(false);
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
