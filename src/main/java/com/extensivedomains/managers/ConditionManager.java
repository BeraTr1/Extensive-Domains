package com.extensivedomains.managers;

import com.extensivedomains.conditions.Condition;
import com.extensivedomains.exceptions.ExtensiveDomainsException;

import java.util.HashMap;
import java.util.Map;

public class ConditionManager {
    private Map<String, Class<? extends Condition>> registeredConditions = new HashMap<>();

    public void registerCondition(String conditionName, Class<? extends Condition> conditionClass) throws ExtensiveDomainsException {
        if (this.registeredConditions.containsKey(conditionName)) {
            throw new ExtensiveDomainsException("A condition with the name '" + conditionName + "' has already been registered");
        }

        this.registeredConditions.put(conditionName, conditionClass);
    }

    public Condition getRegisteredCondition(String conditionName) {
        try {
            Class<? extends Condition> conditionClass = this.registeredConditions.getOrDefault(conditionName, null);
            return this.createCondition(conditionClass);
        } catch (ExtensiveDomainsException e) {
            return null;
        }
    }

    public Condition createCondition(Class<? extends Condition> conditionClass) throws ExtensiveDomainsException {
        try {
            return conditionClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ExtensiveDomainsException();
        }
    }
}
