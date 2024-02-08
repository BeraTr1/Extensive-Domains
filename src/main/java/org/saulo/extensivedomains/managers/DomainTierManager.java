package org.saulo.extensivedomains.managers;

import org.bukkit.configuration.file.YamlConfiguration;
import org.saulo.extensivedomains.ExtensiveDomains;
import org.saulo.extensivedomains.domainactions.DomainAction;
import org.saulo.extensivedomains.objects.DomainTier;

import java.util.*;

public class DomainTierManager {
    private ExtensiveDomains plugin;
    private Map<Integer, DomainTier> domainTiersMap = new HashMap<>();
    private String domainTierConfigFilePath = "domain-tiers.yml";
    private Map<Integer, Set<DomainAction>> tierAllowedActionsMap = new HashMap<>();

    public DomainTierManager(ExtensiveDomains plugin) {
        this.plugin = plugin;

        this.loadDomainTiersFromConfig();
    }

    private void registerDomainTier(DomainTier domainTier) {
        boolean domainTierIsAlreadyRegistered = this.domainTiersMap.containsKey(domainTier.getLevel());

        if (domainTierIsAlreadyRegistered) {
            return;
        }

        domainTiersMap.put(domainTier.getLevel(), domainTier);
    }

    private void loadDomainTiersFromConfig() {
        ConfigManager configManager = plugin.configManager;
        YamlConfiguration domainTierConfig = configManager.loadConfig(domainTierConfigFilePath);
        Set<String> configKeys = configManager.getConfigKeys(domainTierConfig);

        for (String key : configKeys) {
            String domainTierName = configManager.getConfigString(domainTierConfig, key + ".name");
            int domainTierLevel = configManager.getConfigInteger(domainTierConfig, key + ".tier_level");
            int populationRequirement = configManager.getConfigInteger(domainTierConfig, key + ".population_req");
            List<String> allowedActionsString = configManager.getListString(domainTierConfig, key + ".allowed_actions");
            System.out.println("Converting list of string allowed actions to set of allowed actions for tier " + domainTierLevel + "...");
            Set<DomainAction> allowedActions = convertAllowedActionsToSet(allowedActionsString);
            this.tierAllowedActionsMap.put(domainTierLevel, allowedActions);

            DomainTier domainTier = new DomainTier(domainTierName, domainTierLevel, populationRequirement, allowedActions);
            this.registerDomainTier(domainTier);
        }

        this.resolveAllowedActionsInheritance();
    }

    private Set<DomainAction> convertAllowedActionsToSet(List<String> allowedActionsString) {
        Set<DomainAction> allowedActions = new HashSet<>();
        System.out.println("\tTotal allowed actions to convert: " + allowedActionsString.size());

        for (String allowedActionString : allowedActionsString) {
            DomainAction domainAction = DomainAction.getDomainActionFromLabel(allowedActionString);

            if (domainAction == null) {
                System.out.println("\t\tAction '" + allowedActionString + "' not found!");
                continue;
            }

            System.out.println("\t\tAction '\" + allowedActionString + \"' found, adding it to set...");

            allowedActions.add(domainAction);
        }

        System.out.println("\tFinished converting. Total allowed actions in set: " + allowedActions.size());
        System.out.println("");
        return allowedActions;
    }

    private void resolveAllowedActionsInheritance() {
        int registeredTiersAmount = this.tierAllowedActionsMap.size();

        System.out.println("Resolving inheritance of " + registeredTiersAmount + " domain tiers!");
        for (int i = 1; i <= registeredTiersAmount; i++) {
            System.out.println("\tCurrent tier: " + i);
            int previousTierLevel = i - 1;
            boolean previousTierExists = this.tierAllowedActionsMap.getOrDefault(previousTierLevel, null) != null;

            if (!previousTierExists) {
                System.out.println("\t\tSkipping tier... No tiers to inherit allowed actions from!");
                System.out.println("");
                continue;
            }

            System.out.println("\tPrevious tier: " + previousTierLevel);
            Set<DomainAction> previousTierAllowedActions = this.tierAllowedActionsMap.get(previousTierLevel);
            Set<DomainAction> currentTierAllowedActions = this.tierAllowedActionsMap.get(i);
            System.out.println("\tAllowed actions of CURRENT tier:");
            for (DomainAction action : currentTierAllowedActions) {
                System.out.println("\t\t- " + action.getLabel());
            }
            System.out.println("\tAllowed actions of PREVIOUS tier:");
            for (DomainAction action : previousTierAllowedActions) {
                System.out.println("\t\t- " + action.getLabel());
            }
            currentTierAllowedActions.addAll(previousTierAllowedActions);
            this.tierAllowedActionsMap.put(i, currentTierAllowedActions);
            System.out.println("\tUpdated allowed actions of current tier:");
            for (DomainAction action : this.tierAllowedActionsMap.get(i)) {
                System.out.println("\t\t- " + action.getLabel());
            }
            System.out.println("");
        }
    }

    public boolean domainTierAllowsAction(int domainTier, DomainAction action) {
        Set<DomainAction> allowedActions = this.tierAllowedActionsMap.get(domainTier);
        System.out.println("Domain tier level " + domainTier + " can perform " + action.getLabel() + ": " + allowedActions.contains(action));
        return allowedActions.contains(action);
    }

    public DomainTier getDomainTierFromLevel(int level) {
        return this.domainTiersMap.getOrDefault(level, null);
    }

    public DomainTier getNextDomainTier(int level) {
        int nextDomainTierLevel = level + 1;
        return this.domainTiersMap.getOrDefault(nextDomainTierLevel, null);
    }

    public DomainTier getPreviousDomainTier(int level) {
        int nextDomainTierLevel = level - 1;
        return this.domainTiersMap.getOrDefault(nextDomainTierLevel, null);
    }
}
