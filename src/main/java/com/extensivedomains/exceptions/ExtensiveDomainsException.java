package com.extensivedomains.exceptions;

public class ExtensiveDomainsException extends Exception {
    public ExtensiveDomainsException(String msg) {
        super(msg);
    }

    public ExtensiveDomainsException() {
        super("Unknown cause!");
    }
}
