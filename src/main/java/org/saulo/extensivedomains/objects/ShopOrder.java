package org.saulo.extensivedomains.objects;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopOrder {
    private Map<ItemStack, Integer> itemsToBuyMap = new HashMap<>();
    private Map<ItemStack, Integer> itemsToSellMap = new HashMap<>();

    public void addItemToBuy(ItemStack item, int quantity) {
        int currentQuantity = this.itemsToBuyMap.getOrDefault(item, 0);
        int totalQuantity = currentQuantity + quantity;
        this.itemsToBuyMap.putIfAbsent(item, totalQuantity);
    }

    public void removeItemToBuy(ItemStack item, int quantity) {
        int currentQuantity = this.itemsToBuyMap.get(item);

        if (quantity >= currentQuantity) {
            this.itemsToSellMap.remove(item);
            return;
        }

        int totalQuantity = currentQuantity - quantity;
        this.itemsToBuyMap.put(item, quantity);
    }

    public Map<ItemStack, Integer> getItemsToBuyMap() {
        return this.itemsToBuyMap;
    }

    public void addItemToSell(ItemStack item, int quantity) {
        int currentQuantity = this.itemsToSellMap.getOrDefault(item, 0);
        int totalQuantity = currentQuantity + quantity;
        this.itemsToSellMap.putIfAbsent(item, totalQuantity);
    }

    public void removeItemToSell(ItemStack item, int quantity) {
        int currentQuantity = this.itemsToSellMap.get(item);

        if (quantity >= currentQuantity) {
            this.itemsToSellMap.remove(item);
            return;
        }

        int totalQuantity = currentQuantity - quantity;
        this.itemsToSellMap.put(item, totalQuantity);
    }

    public Map<ItemStack, Integer> getItemsToSellMap() {
        return this.itemsToSellMap;
    }
}
