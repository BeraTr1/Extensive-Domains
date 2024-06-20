package org.saulo.extensivedomains.exceptions;

import org.saulo.extensivedomains.ExtensiveDomains;

public class ExtensiveDomainsException extends Exception {
    public ExtensiveDomainsException(String msg) {
        super(msg);
    }

    public ExtensiveDomainsException() {
        super("Unknown cause!");
    }
}
