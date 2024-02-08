package org.saulo.extensivedomains.objects;

public class Currency {
    private Domain domain;
    private String name;
    private char symbol;
    private double purchasingPower = 0.0;

    public Currency(Domain domain, String name) {
        this.domain = domain;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public double getPurchasingPower() {
        return this.purchasingPower;
    }

    public void setPurchasingPower(double purchasingPower) {
        this.purchasingPower = purchasingPower;
    }
}
