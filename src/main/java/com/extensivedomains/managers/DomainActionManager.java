package com.extensivedomains.managers;

import com.extensivedomains.exceptions.ExtensiveDomainsException;
import com.extensivedomains.objects.domain.actions.DomainAction;

import java.util.HashMap;
import java.util.Map;

public class DomainActionManager {
    private Map<String, Class<? extends DomainAction>> registeredDomainActions = new HashMap<>();

    public Class<? extends DomainAction> getDomainAction(String domainActionName) {
        return this.registeredDomainActions.getOrDefault(domainActionName, null);
    }

    public DomainAction createDomainAction(Class <? extends DomainAction> domainActionClass) {
        // todo implement code to create a domain action
        return null;
    }

    public void registerDomainAction(String actionName, Class<? extends DomainAction> actionClass) throws ExtensiveDomainsException {
        if (this.registeredDomainActions.containsKey(actionName)) {
            throw new ExtensiveDomainsException("An action with the name '" + actionName + "' has already been registered!");
        }

        this.registeredDomainActions.putIfAbsent(actionName, actionClass);
    }
}
