package org.saulo.extensivedomains;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.saulo.extensivedomains.commands.ExtensiveDomainsCommand;
import org.saulo.extensivedomains.data.Data;
import org.saulo.extensivedomains.data.SQLite;
import org.saulo.extensivedomains.listeners.BlockListener;
import org.saulo.extensivedomains.listeners.ChangeDayListener;
import org.saulo.extensivedomains.listeners.PlayerListener;
import org.saulo.extensivedomains.managers.*;
import org.saulo.extensivedomains.tasks.DetectDayChangeTask;

public class ExtensiveDomains extends JavaPlugin {
    public static ExtensiveDomains instance;

    public ConfigManager configManager;
    public DomainTierManager domainTierManager;
    public DomainManager domainManager;
    public DataManager dataManager;
    public DailyTaskManager dailyTaskManager;
    public CitizenManager citizenManager;
    public ClaimManager claimManager;

    @Override
    public void onEnable() {
        instance = this;

        loadManagers();
        registerCommands();
        registerEventListeners();
        loadData();

        Runnable runnable = new DetectDayChangeTask();
        Bukkit.getScheduler().runTaskTimer(this, runnable, 0L, 20L);
    }

    @Override
    public void onDisable() {
        saveData();
    }

    private void registerEventListeners() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new BlockListener(claimManager), this);
        pluginManager.registerEvents(new ChangeDayListener(), this);
        pluginManager.registerEvents(new PlayerListener(citizenManager), this);
    }

    private void registerCommands() {
        this.getCommand("extensivedomains").setExecutor(new ExtensiveDomainsCommand(citizenManager, claimManager));
    }

    private void loadManagers() {
        this.configManager = new ConfigManager(this);
        this.domainTierManager = new DomainTierManager(this);
        this.citizenManager = new CitizenManager();
        this.claimManager = new ClaimManager();
        this.domainManager = new DomainManager(this, claimManager, domainTierManager);
        // todo remove below, pass it into "dataManager.loadData()" instead
        String databaseLocation = "database.db";
        Data data = new SQLite(this, databaseLocation, citizenManager, domainManager);
        this.dataManager = new DataManager(this, data);
        this.dailyTaskManager = new DailyTaskManager(this);
    }

    public void loadData() {
        this.dataManager.loadData();
    }

    public void saveData() {
        this.dataManager.saveData();
    }
}
