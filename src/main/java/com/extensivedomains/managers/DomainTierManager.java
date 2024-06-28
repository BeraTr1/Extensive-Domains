package com.extensivedomains.managers;

import com.extensivedomains.conditions.domainconditions.DomainCondition;
import com.extensivedomains.exceptions.ExtensiveDomainsException;
import org.bukkit.configuration.file.YamlConfiguration;
import com.extensivedomains.objects.domain.actions.DomainAction;
import com.extensivedomains.objects.domain.tier.DomainTier;

import java.util.*;

public class DomainTierManager {
    private final Map<Integer, DomainTier> registeredDomainTiers = new HashMap<>();
    private DomainTier highestDomainTier;
    private DomainTier lowestDomainTier;

    private final ConfigManager configManager;
    private final DomainActionManager domainActionManager;
    private final ConditionManager conditionManager;

    public DomainTierManager(ConfigManager configManager, DomainActionManager domainActionManager, ConditionManager conditionManager) {
        this.configManager = configManager;
        this.domainActionManager = domainActionManager;
        this.conditionManager = conditionManager;
    }

    public DomainTier getRegisteredDomainTier(int level) {
        return this.registeredDomainTiers.getOrDefault(level, null);
    }

    public List<DomainTier> getRegisteredDomains() {
        return new ArrayList<>(this.registeredDomainTiers.values());
    }

    public void registerAllDomainTiers(DomainTier... domainTiers) throws ExtensiveDomainsException {
        for (DomainTier domainTier : domainTiers) {
            this.registerDomainTier(domainTier);
        }
    }

    public void registerDomainTier(DomainTier domainTier) throws ExtensiveDomainsException {
        boolean domainTierIsAlreadyRegistered = this.registeredDomainTiers.containsKey(domainTier.getLevel());

        if (domainTierIsAlreadyRegistered) {
            throw new ExtensiveDomainsException("A tier with level " + domainTier.getLevel() + " has already been registered!");
        }

        registeredDomainTiers.put(domainTier.getLevel(), domainTier);
    }

    public void loadDomainTiersFromConfig(YamlConfiguration config) {
        if (config == null) {
            return;
        }

        List<String> tierLevelKeys = configManager.getConfigKeys(config);

        for (String tierLevelKey : tierLevelKeys) {
            List<String> requirementsString = configManager.getListString(config, tierLevelKey + ".requirements");
            List<String> allowedActionsString = configManager.getListString(config, tierLevelKey + ".allowed-actions");
            int domainTierLevel;

            try {
                domainTierLevel = Integer.parseInt(tierLevelKey);
            } catch (Exception e) {
                // todo display error message in console
                continue;
            }

            boolean domainTierLevelIsDuplicate = this.registeredDomainTiers.getOrDefault(domainTierLevel, null) != null;

            if (domainTierLevelIsDuplicate) {
                // todo display error message in console
                continue;
            }

            List<DomainCondition> requirements = new ArrayList<>();

            for (String domainConditionConfig : requirementsString) {
                // todo ignore multiple whitespaces (e.g. "<condition name>    <condition arg>" == "<condition name> <condition arg>")
                String[] domainConditionConfigSplit = domainConditionConfig.split(" ");

                if (domainConditionConfigSplit.length == 0) {
                    continue;
                }

                String conditionName = domainConditionConfigSplit[0];
                // todo allow for multiple condition arguments (String.split(regex, numOfElements in array))
                String domainConditionArg = domainConditionConfigSplit.length > 1 ? domainConditionConfigSplit[1] : null;

                try {
                    DomainCondition domainCondition = (DomainCondition) conditionManager.getRegisteredCondition(conditionName);
                    domainCondition.setArgs(domainConditionArg);
                    requirements.add(domainCondition);
                } catch (ExtensiveDomainsException e) {
                    // todo display error message in console
                    continue;
                }
            }

            List<Class<? extends DomainAction>> allowedActions = new ArrayList<>();

            for (String allowedActionName : allowedActionsString) {
                Class<? extends DomainAction> domainActionClass = domainActionManager.getDomainAction(allowedActionName);
                allowedActions.add(domainActionClass);
            }

            try {
                this.createDomainTier(domainTierLevel, requirements, allowedActions);
            } catch (Exception e) {
                // todo display error message in console
            }
        }
    }

    public DomainTier getHighestDomainTier() {
        return this.highestDomainTier;
    }

    public DomainTier getLowestDomainTier() {
        return this.lowestDomainTier;
    }

    public DomainTier createDomainTier(int level, List<DomainCondition> conditions, List<Class<? extends DomainAction>> allowedActions) throws ExtensiveDomainsException {
        DomainTier domainTier = new DomainTier(level, conditions, allowedActions);
        this.registerDomainTier(domainTier);
        boolean domainTierIsHighestLevel = highestDomainTier == null || highestDomainTier.getLevel() < domainTier.getLevel();

        if (domainTierIsHighestLevel) {
            this.highestDomainTier = domainTier;
        }

        boolean domainTierIsLowestLevel = lowestDomainTier == null || lowestDomainTier.getLevel() > domainTier.getLevel();

        if (domainTierIsLowestLevel) {
            this.lowestDomainTier = domainTier;
        }

        return domainTier;
    }

    public void resolveAllowedActionsInheritance() {
        // todo use lowest/highestDomainTier
        int totalRegisteredDomainTiers = this.registeredDomainTiers.size();

        for (int i = 1; i <= totalRegisteredDomainTiers; i++) {
            int previousDomainTierLevel = Math.max(i - 1, 0);
            DomainTier previousDomainTier = this.getRegisteredDomainTier(previousDomainTierLevel);

            if (previousDomainTier == null) {
                continue;
            }

            DomainTier domainTier = this.getRegisteredDomainTier(i);

            if (domainTier == null) {
                return;
            }

            List<Class<? extends DomainAction>> previousDomainTierActions = previousDomainTier.getAllowedActions();
            domainTier.addAllowedActions(previousDomainTierActions);
        }
    }
}
