package com.extensivedomains.conditions;

import com.extensivedomains.exceptions.ExtensiveDomainsException;

public interface Condition {
    void setArgs(String... args) throws ExtensiveDomainsException;
}
