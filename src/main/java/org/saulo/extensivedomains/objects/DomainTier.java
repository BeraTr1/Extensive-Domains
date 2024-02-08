package org.saulo.extensivedomains.objects;

import org.jetbrains.annotations.NotNull;
import org.saulo.extensivedomains.domainactions.DomainAction;

import java.util.List;
import java.util.Set;

public class DomainTier {
    private String name;
    private int level;
    private int populationRequirement;
    private Set<DomainAction> allowedActions;

    public DomainTier(String name, int level, int populationRequirement, Set<DomainAction> allowedActions) {
        this.name = name;
        this.level = level;
        this.populationRequirement = populationRequirement;
        this.allowedActions = allowedActions;
    }

    public String getName() {
        return this.name;
    }

    @NotNull
    public Integer getLevel() {
        return this.level;
    }

    public Set<DomainAction> getAllowedActions() {
        return this.allowedActions;
    }

    public int getPopulationRequirement() {
        return this.populationRequirement;
    }
}
