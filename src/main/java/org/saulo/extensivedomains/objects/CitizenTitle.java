package org.saulo.extensivedomains.objects;

import org.saulo.extensivedomains.domainactions.DomainAction;

import java.util.List;

public class CitizenTitle {
    private String name;
    private List<DomainAction> allowedDomainActions;

    public String getName() {
        return this.name;
    }
}
