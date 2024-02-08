package org.saulo.extensivedomains.objects;

public class CurrencyAccount {
    private Currency currency;
    private double balance = 0.0;
    // todo owner of account (i.e. who holds this account)

    public CurrencyAccount(Currency currency) {
        this.currency = currency;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public void addBalance(double amount) {
        this.balance += amount;
    }

    public void removeBalance(double amount) {
        this.balance -= amount;
    }
}
