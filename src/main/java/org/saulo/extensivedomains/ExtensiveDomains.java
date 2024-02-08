package org.saulo.extensivedomains;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.saulo.extensivedomains.commands.ExtensiveDomainsCommand;
import org.saulo.extensivedomains.listeners.BlockListener;
import org.saulo.extensivedomains.managers.ConfigManager;
import org.saulo.extensivedomains.managers.DomainManager;
import org.saulo.extensivedomains.managers.DomainTierManager;

public final class ExtensiveDomains extends JavaPlugin {
    public static ExtensiveDomains instance;

    public ConfigManager configManager;
    public DomainTierManager domainTierManager;
    public DomainManager domainManager;

    @Override
    public void onEnable() {
        registerEvents();
        registerCommands();
        loadManagers();

        instance = this;
    }

    @Override
    public void onDisable() {
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
    }
}
