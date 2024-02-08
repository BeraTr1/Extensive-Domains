package org.saulo.extensivedomains.objects;

import org.bukkit.inventory.ItemStack;

import java.util.*;

public class VirtualInventory {
    private Map<ItemStack, Integer> items = new HashMap<>();
    private int limit = 0;

    public void addItem(ItemStack item, int quantity) {
        int currentQuantity = this.items.getOrDefault(item, 0);
        int totalQuantity = currentQuantity + quantity;
        this.items.put(item, totalQuantity);
    }

    public void removeItem(ItemStack item, int quantity) {
        int currentQuantity = this.items.getOrDefault(item, 0);

        if (quantity > currentQuantity) return;

        int totalQuantity = Math.max((currentQuantity - quantity), 0);
        this.items.put(item, totalQuantity);
    }

    public int getItemQuantity(ItemStack item) {
        return this.items.getOrDefault(item, 0);
    }

    public List<ItemStack> getItems() {
        Set<ItemStack> itemsSet = this.items.keySet();
        List<ItemStack> items = new ArrayList<>(itemsSet);
        return items;
    }

    public int getLimit() {
        return this.limit;
    }
}
