package org.saulo.extensivedomains.objects;

import org.bukkit.inventory.ItemStack;
import org.saulo.extensivedomains.managers.EconomyManager;

import java.util.HashMap;
import java.util.Map;

public class Shop {
    // todo modify if needed
    public class ShopItem {
        private ItemStack item;
        private double price;
        private int limit;
    }

    // todo define owner
    private Domain domain;
    private CurrencyAccount currencyAccount;
    private Map<ItemStack, Double> sellingItems = new HashMap<>();
    private Map<ItemStack, Double> buyingItems = new HashMap<>();
    private VirtualInventory stockInventory = new VirtualInventory();
    private String name;

    public Shop(String name, Citizen citizen) {
        Domain domain = citizen.getDomain();
        this.domain = domain;
        this.name = name.isEmpty() ? "" : name;
    }

    public String getName() {
        return this.name;
    }

    public void addSellItem(ItemStack item, double price) {
        this.sellingItems.put(item, price);
    }

    public void removeSellItem(ItemStack item) {

    }

    public void addBuyItem(ItemStack item, double price) {
        this.buyingItems.put(item, price);
    }

    public void removeBuyItem(ItemStack item) {

    }

    public double getSellItemPrice(ItemStack item) {
        return this.sellingItems.getOrDefault(item, -1.0);
    }

    public CurrencyAccount getCurrencyAccount() {
        return this.currencyAccount;
    }

//    public void processOrder(ShopOrder shopOrder, VirtualInventory traderInventory, CurrencyAccount buyerCurrencyAccount) {
    public boolean processOrder(ShopOrder shopOrder, VirtualInventory traderInventory, CurrencyAccount buyerCurrencyAccount) {
        Map<ItemStack, Integer> itemsBeingSoldToShop = shopOrder.getItemsToSellMap();
        Map<ItemStack, Integer> shopReceivingItemsTransaction = new HashMap<>();
        double totalIncomeForShop = 0;

        //todo debug
        System.out.println("Processing order...");
        System.out.println("Being sold to shop:");

        for (Map.Entry<ItemStack, Integer> entry : itemsBeingSoldToShop.entrySet()) {
            ItemStack itemBeingSoldToShop = entry.getKey();
            int quantity = entry.getValue();
            boolean traderHasEnoughOfItem = traderInventory.getItemQuantity(itemBeingSoldToShop) >= quantity;
            double itemPrice = this.buyingItems.getOrDefault(itemBeingSoldToShop, 0.0);
            boolean shopIsBuyingItem = this.buyingItems.getOrDefault(itemBeingSoldToShop, null) != null;
            boolean shopIsBuyingEnoughOfItem;

            //todo debug
            System.out.println("\tItem: " + itemBeingSoldToShop.getType().name());
            System.out.println("\tPlayer has enough items: " + traderHasEnoughOfItem);
            System.out.println("\t\tTotal in inventory: " + traderInventory.getItemQuantity(itemBeingSoldToShop));
            System.out.println("\t\tTotal being sold to shop: " + quantity);

            if (!shopIsBuyingItem) {
                //todo debug
                System.out.println("Shop is not buying this item");
                return false;
//                return;
            }

            if (!traderHasEnoughOfItem) {
                //todo debug
                System.out.println("Trader doesn't have enough of this item");
                return false;
//                return;
            }

            totalIncomeForShop -= itemPrice;
            shopReceivingItemsTransaction.put(itemBeingSoldToShop, quantity);
        }

        ItemTransaction shopReceivingTransaction = new ItemTransaction(traderInventory, this.stockInventory, shopReceivingItemsTransaction);

        Map<ItemStack, Integer> shopSendingItemsTransaction = new HashMap<>();
        Map<ItemStack, Integer> itemsBeingBought = shopOrder.getItemsToBuyMap();

        //todo debug
        System.out.println("Being bought by player:");

        for (Map.Entry<ItemStack, Integer> entry : itemsBeingBought.entrySet()) {
            ItemStack itemBeingBought = entry.getKey();
            int quantity = entry.getValue();
            double itemPrice = this.sellingItems.getOrDefault(itemBeingBought, 0.0);
            boolean shopHasEnoughOfItem = this.stockInventory.getItemQuantity(itemBeingBought) >= quantity;
            boolean shopIsSellingItem = this.sellingItems.getOrDefault(itemBeingBought, null) != null;
            boolean shopIsBuyingEnoughOfItem;

            //todo debug
            System.out.println("\tItem: " + itemBeingBought.getType().name());
            System.out.println("\tShop has enough items: " + shopHasEnoughOfItem);
            System.out.println("\tSellItemMap: " + this.sellingItems.get(itemBeingBought));
            System.out.println("\t\tTotal in inventory: " + this.stockInventory.getItemQuantity(itemBeingBought));
            System.out.println("\t\tTotal being sold to shop: " + quantity);

            if (!shopIsSellingItem) {
                //todo debug
                System.out.println("Shop is not selling this item");
                System.out.println("Available items to buy:");
                for (ItemStack item : this.sellingItems.keySet()) {
                    System.out.println("\t- " + item.getType().name());
                }
                return false;
//                return;
            }

            if (!shopHasEnoughOfItem) {
                //todo debug
                System.out.println("Shop doesn't have enough of this item");
                return false;
//                return;
            }

            totalIncomeForShop += itemPrice;
            shopSendingItemsTransaction.put(itemBeingBought, quantity);
        }

        ItemTransaction shopSendingTransaction = new ItemTransaction(this.stockInventory, traderInventory, shopSendingItemsTransaction);

        if (this.currencyAccount == null) {
            if (totalIncomeForShop > 0) {
                //todo debug
                System.out.println("Shop is using bartering system, and buyer isn't selling enough items!");
                return false;
                // return;
            }
        } else {
            if (totalIncomeForShop >= 0) {
                if (buyerCurrencyAccount == null || buyerCurrencyAccount.getCurrency() == null) {
                    System.out.println("Buyer doesn't have a currency!");
                    return false;
                } else {
                    System.out.println("Buyer has a currency");
                    System.out.println("\tAccount: " + buyerCurrencyAccount);
                    System.out.println("\tCurrency: " + buyerCurrencyAccount.getCurrency().getName());
                }

                double priceInBuyerCurrency = EconomyManager.convertCurrencyValue(this.currencyAccount.getCurrency(), buyerCurrencyAccount.getCurrency(), totalIncomeForShop);
                boolean buyerHasEnoughMoney = EconomyManager.currencyAccountCanAffordValue(buyerCurrencyAccount, Math.abs(priceInBuyerCurrency));

                System.out.println("Price in shop's currency: " + Math.abs(totalIncomeForShop));
                System.out.println("Price in buyer's currency: " + Math.abs(priceInBuyerCurrency));
                System.out.println("Buyer can afford price: " + buyerHasEnoughMoney);

                if (!buyerHasEnoughMoney) {
                    //todo debug
                    System.out.println("Buyer doesn't have enough money for this trade!");
                    return false;
                    // return;
                }

                CurrencyTransaction currencyTransaction = new CurrencyTransaction(buyerCurrencyAccount, Math.abs(priceInBuyerCurrency), this.currencyAccount);
                EconomyManager.processCurrencyTransaction(currencyTransaction);
            } else {
                boolean shopHasEnoughMoney = EconomyManager.currencyAccountCanAffordValue(this.currencyAccount, Math.abs(totalIncomeForShop));

                if (!shopHasEnoughMoney) {
                    //todo debug
                    System.out.println("Shop doesn't have enough money for this trade!");
                    return false;
                    // return;
                }

                CurrencyTransaction currencyTransaction = new CurrencyTransaction(this.currencyAccount, Math.abs(totalIncomeForShop), buyerCurrencyAccount);
                EconomyManager.processCurrencyTransaction(currencyTransaction);
            }

        }

        EconomyManager.processItemTransaction(shopReceivingTransaction);
        EconomyManager.processItemTransaction(shopSendingTransaction);

        return true;
    }

    public void setCurrencyAccount(CurrencyAccount currencyAccount) {
        this.currencyAccount = currencyAccount;
    }

    public VirtualInventory getInventory() {
        return this.stockInventory;
    }

    public void addStock(ItemStack item, int amount) {
        this.stockInventory.addItem(item, amount);
    }
}