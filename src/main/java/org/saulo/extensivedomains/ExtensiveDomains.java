package org.saulo.extensivedomains;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.saulo.extensivedomains.commands.ExtensiveDomainsCommand;
import org.saulo.extensivedomains.data.Data;
import org.saulo.extensivedomains.data.SQLite;
import org.saulo.extensivedomains.listeners.BlockListener;
import org.saulo.extensivedomains.listeners.ChangeDayListener;
import org.saulo.extensivedomains.managers.*;
import org.saulo.extensivedomains.tasks.DetectDayChangeTask;

public final class ExtensiveDomains extends JavaPlugin {
    public static ExtensiveDomains instance;

    public ConfigManager configManager;
    public DomainTierManager domainTierManager;
    public DomainManager domainManager;
    public EconomyManager economyManager;
    public DataManager dataManager;
    public DailyTaskManager dailyTaskManager;

    @Override
    public void onEnable() {
        instance = this;

        registerEventListeners();
        registerCommands();
        loadManagers();
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

        pluginManager.registerEvents(new BlockListener(), this);
        pluginManager.registerEvents(new ChangeDayListener(), this);
    }

    private void registerCommands() {
        this.getCommand("extensivedomains").setExecutor(new ExtensiveDomainsCommand());
    }

    private void loadManagers() {
        this.configManager = new ConfigManager(this);
        this.domainTierManager = new DomainTierManager(this);
        this.domainManager = new DomainManager(this);
        this.economyManager = new EconomyManager(this);
        // todo remove below, pass it into "dataManager.loadData()" instead
        String databaseLocation = "database.db";
        Data data = new SQLite(databaseLocation);
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
