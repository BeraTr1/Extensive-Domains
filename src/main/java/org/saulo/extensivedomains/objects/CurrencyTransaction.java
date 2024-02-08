package org.saulo.extensivedomains.objects;

import org.jetbrains.annotations.NotNull;

public class CurrencyTransaction {
    private CurrencyAccount senderAccount;
    private CurrencyAccount receiverAccount;
    private double amountInSenderCurrency;

    public CurrencyTransaction(CurrencyAccount senderAccount, double amountInSenderCurrency, CurrencyAccount receiverAccount) {
        this.senderAccount = senderAccount;
        this.amountInSenderCurrency = amountInSenderCurrency;
        this.receiverAccount = receiverAccount;
    }

    @NotNull
    public CurrencyAccount getSenderAccount() {
        return this.senderAccount;
    }

    @NotNull
    public CurrencyAccount getReceiverAccount() {
        return this.receiverAccount;
    }

    public double getAmountInSenderCurrency() {
        return this.amountInSenderCurrency;
    }
}
