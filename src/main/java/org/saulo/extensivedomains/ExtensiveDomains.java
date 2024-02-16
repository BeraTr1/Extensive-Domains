package org.saulo.extensivedomains;

import org.bukkit.Chunk;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.saulo.extensivedomains.commands.ExtensiveDomainsCommand;
import org.saulo.extensivedomains.data.Data;
import org.saulo.extensivedomains.data.SQLite;
import org.saulo.extensivedomains.listeners.BlockListener;
import org.saulo.extensivedomains.managers.*;

public final class ExtensiveDomains extends JavaPlugin {
    public static ExtensiveDomains instance;

    public ConfigManager configManager;
    public DomainTierManager domainTierManager;
    public DomainManager domainManager;
    public EconomyManager economyManager;
    public DataManager dataManager;

    @Override
    public void onEnable() {
        instance = this;

        registerEvents();
        registerCommands();
        loadManagers();
        loadData();
    }

    @Override
    public void onDisable() {
        saveData();
    }

    private void registerEvents() {
        PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new BlockListener(), this);
    }

    private void registerCommands() {
        this.getCommand("extensivedomains").setExecutor(new ExtensiveDomainsCommand());
    }

    private void loadManagers() {
        this.configManager = new ConfigManager(this);
        this.domainTierManager = new DomainTierManager(this);
        this.domainManager = new DomainManager(this);
        this.economyManager = new EconomyManager(this);
        String databaseLocation = "database.db";
        Data data = new SQLite(databaseLocation);
        this.dataManager = new DataManager(this, data);
    }

    public void loadData() {
        this.dataManager.loadData();
    }

    public void saveData() {
        this.dataManager.saveData();
    }
}
