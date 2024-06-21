package com.extensivedomains.objects.citizen;

import com.extensivedomains.objects.domain.actions.DomainAction;

import java.util.List;

public class CitizenTitle {
    private String name;
    private List<DomainAction> allowedDomainActions;

    public String getName() {
        return this.name;
    }
}
