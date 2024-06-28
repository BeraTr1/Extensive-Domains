package com.extensivedomains.conditions.domainconditions;

import com.extensivedomains.exceptions.ExtensiveDomainsException;
import com.extensivedomains.objects.domain.Domain;

public class PopulationCondition implements DomainCondition {
    private int populationRequired;

    @Override
    public boolean test(Domain domain) {
        return domain.getPopulation() >= this.populationRequired;
    }

    @Override
    public void setArgs(String... args) throws ExtensiveDomainsException {
        if (args.length == 0) {
            return;
        }

        String firstArg = args[0];

        try {
            int populationReq = Integer.parseInt(firstArg);
            this.setPopulationRequired(populationReq);
        } catch (Exception e) {
            throw new ExtensiveDomainsException("Could not convert '" + firstArg + "' to an integer!");
        }
    }

    public void setPopulationRequired(int populationRequired) {
        this.populationRequired = populationRequired;
    }
}
