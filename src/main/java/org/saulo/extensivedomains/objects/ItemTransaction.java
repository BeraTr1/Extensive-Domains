package org.saulo.extensivedomains.objects;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemTransaction {
    private VirtualInventory senderInventory;
    private VirtualInventory receiverInventory;

    private Map<ItemStack, Integer> items;

    public ItemTransaction(VirtualInventory senderInventory, VirtualInventory receiverInventory, Map<ItemStack, Integer> items) {
        this.senderInventory = senderInventory;
        this.receiverInventory = receiverInventory;
        this.items = items;
    }

    public VirtualInventory getSenderInventory() {
        return this.senderInventory;
    }

    public VirtualInventory getReceiverInventory() {
        return this.receiverInventory;
    }

    public Map<ItemStack, Integer> getItems() {
        return this.items;
    }
}
