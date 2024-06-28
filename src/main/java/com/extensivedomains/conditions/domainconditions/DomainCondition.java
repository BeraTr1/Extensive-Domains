package com.extensivedomains.conditions.domainconditions;

import com.extensivedomains.conditions.Condition;
import com.extensivedomains.objects.domain.Domain;

public interface DomainCondition extends Condition {
    boolean test(Domain domain);
}
