package org.saulo.extensivedomains.managers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.saulo.extensivedomains.ExtensiveDomains;
import org.saulo.extensivedomains.objects.*;

import java.text.DecimalFormat;
import java.util.Map;

public class EconomyManager {
    ExtensiveDomains plugin;

    public EconomyManager(ExtensiveDomains plugin) {
        this.plugin = plugin;
    }

    public void createCurrency(Domain domain, String currencyName) {
        Currency currency = new Currency(domain, currencyName);
        domain.setPrimaryCurrencyAccount(currency);
        Mapper.addCurrencyWithName(currencyName, currency);
    }

    public void addBalance(CurrencyAccount currencyAccount, double amount) {
        double totalBalance = currencyAccount.getBalance() + amount;
        currencyAccount.setBalance(totalBalance);
    }

    public void removeBalance(CurrencyAccount currencyAccount, double amount) {
        double totalBalance = currencyAccount.getBalance() - amount;
        currencyAccount.setBalance(totalBalance);
    }

    public void addCurrencyAccount() {

    }

    public void removeCurrencyAccount() {

    }

    public void processCurrencyTransaction(CurrencyTransaction currencyTransaction) {
        System.out.println("Processing transaction...");
        double amountInSenderCurrency = currencyTransaction.getAmountInSenderCurrency();

        CurrencyAccount senderCurrencyAccount = currencyTransaction.getSenderAccount();
        Currency senderCurrency = senderCurrencyAccount.getCurrency();

        boolean senderCanAffordValue = currencyAccountCanAffordValue(senderCurrencyAccount, amountInSenderCurrency);

        if (!senderCanAffordValue) {
            System.out.println("\tSender cannot afford value!");
            return;
        }

        CurrencyAccount receiverCurrencyAccount = currencyTransaction.getReceiverAccount();
        Currency receiverCurrency = receiverCurrencyAccount.getCurrency();

        boolean currenciesAreEqual = senderCurrency.equals(receiverCurrency);
        double amountInReceiverCurrency = convertCurrencyValue(senderCurrency, receiverCurrency, amountInSenderCurrency);
        double receiverAmount = currenciesAreEqual ? amountInSenderCurrency : amountInReceiverCurrency;
        double senderAmount = amountInSenderCurrency;

        System.out.println("Setting new balances:");
        System.out.println("\tReceiver: " + receiverAmount + " + " + receiverCurrencyAccount.getBalance() + " = " + (receiverAmount + receiverCurrencyAccount.getBalance()) + " in " + receiverCurrency.getName());
        System.out.println("\tSender: " + senderAmount + " + " + senderCurrencyAccount.getBalance() + " = " + (senderAmount + senderCurrencyAccount.getBalance()) + " in " + senderCurrency.getName());

        receiverCurrencyAccount.addBalance(receiverAmount);
        senderCurrencyAccount.removeBalance(senderAmount);
    }

    public void createShop(Player player, String shopName) {
        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        Shop shop = new Shop(shopName, citizen);
        citizen.setShop(shop);
        Mapper.addShopWithCitizen(shop, citizen);
    }

    public void processItemTransaction(ItemTransaction itemTransaction) {
        VirtualInventory senderInventory = itemTransaction.getSenderInventory();
        VirtualInventory receiverInventory = itemTransaction.getReceiverInventory();

        for (Map.Entry<ItemStack, Integer> entry: itemTransaction.getItems().entrySet()) {
            ItemStack item = entry.getKey();
            int quantity = entry.getValue();
            senderInventory.removeItem(item, quantity);
            receiverInventory.addItem(item, quantity);
        }
    }

    public boolean currencyAccountCanAffordValue(CurrencyAccount currencyAccount, double value) {
        double balance = currencyAccount.getBalance();
        return balance >= value;
    }

    public double getExchangeRateOfCurrencies(Currency currencyA, Currency currencyB) {
        double currencyAPurchasingPower = currencyA.getPurchasingPower();
        double currencyBPurchasingPower = currencyB.getPurchasingPower();

        if (currencyAPurchasingPower == currencyBPurchasingPower) {
//            // if both currencies have no value, they technically have the same purchasing power
            return 1.0;
        }

        if (currencyAPurchasingPower == 0.0) {
            currencyAPurchasingPower = 0.01;
        }

        if (currencyBPurchasingPower == 0.0) {
            currencyAPurchasingPower = 0.01;
        }

        double exchangeRate = currencyAPurchasingPower / currencyBPurchasingPower;
        DecimalFormat decimalFormat = new DecimalFormat("#.####");
        double exchangeRateFormatted = Double.parseDouble(decimalFormat.format(exchangeRate));

        return exchangeRateFormatted;
    }

    public double convertCurrencyValue(Currency currencyA, Currency currencyB, double valueInCurrencyA) {
        double exchangeRate = this.getExchangeRateOfCurrencies(currencyA, currencyB);
        double valueInCurrencyB = valueInCurrencyA * exchangeRate;

        return valueInCurrencyB;
    }

    public void addShopStock(Shop shop, ItemStack item, int amount) {
        shop.addStock(item, amount);
    }
}
