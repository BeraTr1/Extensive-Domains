package com.extensivedomains.objects.domain.tier;

import com.extensivedomains.conditions.domainconditions.DomainCondition;
import com.extensivedomains.objects.domain.actions.DomainAction;

import java.util.List;

public class DomainTier {
    private final int level;
    private final List<DomainCondition> conditions;
    private final List<Class<? extends DomainAction>> allowedActions;

    public DomainTier(int level, List<DomainCondition> requirements, List<Class<? extends DomainAction>> allowedActions) {
        this.level = level;
        this.conditions = requirements;
        this.allowedActions = allowedActions;
    }

    public int getLevel() {
        return this.level;
    }

    public List<Class<? extends DomainAction>> getAllowedActions() {
        return this.allowedActions;
    }

    public List<DomainCondition> getConditions() {
        return this.conditions;
    }

    public void addAllowedAction(Class<? extends DomainAction> actionClass) {
        if (this.allowedActions.contains(actionClass)) {
            return;
        }

        this.allowedActions.add(actionClass);
    }

    public void addAllowedActions(List<Class<? extends DomainAction>> allowedActions) {
        for (Class<? extends DomainAction> actionClass : allowedActions) {
            this.addAllowedAction(actionClass);
        }
    }
}
