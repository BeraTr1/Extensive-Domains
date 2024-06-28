package com.extensivedomains;

import com.extensivedomains.conditions.domainconditions.PopulationCondition;
import com.extensivedomains.exceptions.ExtensiveDomainsException;
import com.extensivedomains.objects.domain.actions.RenameDomain;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.extensivedomains.commands.ExtensiveDomainsCommand;
import com.extensivedomains.data.Data;
import com.extensivedomains.data.SQLite;
import com.extensivedomains.listeners.BlockListener;
import com.extensivedomains.listeners.ChangeDayListener;
import com.extensivedomains.listeners.PlayerListener;
import com.extensivedomains.managers.*;
import com.extensivedomains.tasks.DetectDayChangeTask;

import java.io.File;

public class ExtensiveDomains extends JavaPlugin {
    public static ExtensiveDomains instance;

    public ConfigManager configManager;
    public DomainTierManager domainTierManager;
    public DomainManager domainManager;
    public DataManager dataManager;
    public DailyTaskManager dailyTaskManager;
    public CitizenManager citizenManager;
    public ClaimManager claimManager;
    public ConditionManager conditionManager;
    public DomainActionManager domainActionManager;

    @Override
    public void onEnable() {
        instance = this;

        loadManagers();
        registerCommands();
        registerEventListeners();
        loadConditions();
        loadDomainActions();
        loadDomainTiers();
        loadData();

        Runnable runnable = new DetectDayChangeTask();
        Bukkit.getScheduler().runTaskTimer(this, runnable, 0L, 20L);
    }

    @Override
    public void onDisable() {
        saveData();
    }

    public void loadData() {
        this.dataManager.loadData();
    }

    public void saveData() {
        this.dataManager.saveData();
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
        this.conditionManager = new ConditionManager();
        this.domainActionManager = new DomainActionManager();

        this.domainTierManager = new DomainTierManager(configManager, domainActionManager, conditionManager);
        this.citizenManager = new CitizenManager();
        this.claimManager = new ClaimManager();
        this.domainManager = new DomainManager(claimManager, domainTierManager);

        // todo remove below, pass it into "dataManager.loadData()" instead
        String databaseLocation = "database.db";
        Data data = new SQLite(this, databaseLocation, citizenManager, domainManager, domainTierManager);
        this.dataManager = new DataManager(this, data);
        this.dailyTaskManager = new DailyTaskManager(this);
    }

    private void loadConditions() {
        try {
            conditionManager.registerCondition("population", PopulationCondition.class);
        } catch (ExtensiveDomainsException e) {
            // todo add console message
            return;
        }
    }

    private void loadDomainActions() {
        try {
            domainActionManager.registerDomainAction("rename-domain", RenameDomain.class);
        } catch (ExtensiveDomainsException e) {
            // todo add console message
            return;
        }
    }

    private void loadDomainTiers() {
        final String DOMAIN_TIER_CONFIG_FILE_NAME = "domain-tiers.yml";
        File file = new File(this.getDataFolder().getAbsolutePath() + File.separator + DOMAIN_TIER_CONFIG_FILE_NAME);
        YamlConfiguration config = configManager.loadConfig(file);
        domainTierManager.loadDomainTiersFromConfig(config);
        domainTierManager.resolveAllowedActionsInheritance();
        System.out.println("Successfully loaded (" + domainTierManager.getRegisteredDomains().size() + ") domain tiers!");
    }
}
