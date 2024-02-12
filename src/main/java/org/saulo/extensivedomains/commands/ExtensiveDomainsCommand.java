package org.saulo.extensivedomains.commands;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.saulo.extensivedomains.ExtensiveDomains;
import org.saulo.extensivedomains.Utils;
import org.saulo.extensivedomains.domainactions.DomainAction;
import org.saulo.extensivedomains.managers.EconomyManager;
import org.saulo.extensivedomains.playerconditions.HasNameCondition;
import org.saulo.extensivedomains.playerconditions.HasTitleCondition;
import org.saulo.extensivedomains.managers.DomainManager;
import org.saulo.extensivedomains.objects.*;

import java.util.List;
import java.util.UUID;

public class ExtensiveDomainsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            this.parseCommand(player, args);
        }

        return false;
    }

    private void parseCommand(Player player, String[] args) {
        if (args.length == 0) return;

        switch (args[0].toLowerCase()) {
            case "create":
                this.createDomain(player);
                break;
            case "claim":
                this.claimChunk(player);
                break;
            case "unclaim":
                this.unclaimChunk(player);
                break;
            case "claims":
                this.claims(player);
                break;
            case "print":
                this.print(player);
                break;
            case "isclaimed":
                this.isClaimed(player);
                break;
            case "addprotection":
                this.addClaimProtection(player, args[1]);
                break;
            case "removeprotection":
                this.removeProtection(player, args[1]);
                break;
            case "addpermission":
                this.addPermission(player, args[1], args[2], args[3]);
                break;
            case "removepermission":
                this.removePermission(player, args[1]);
                break;
            case "createshop":
                this.createShop(player, args[1]);
                break;
            case "createcurrency":
                this.createCurrency(player, args[1]);
                break;
            case "adoptcurrency":
                this.adoptCurrency(player, args[1]);
                break;
            case "addbalance":
                this.addBalance(player, args[1]);
                break;
            case "printbalance":
                this.printBalance(player);
                break;
            case "printinventory":
                this.printInventory(player);
                break;
            case "buyitem":
                this.buyItemFromShop(player, args[1], args[2], args[3]);
                break;
            case "sellitem":
                this.sellItemToShop(player, args[1], args[2], args[3]);
                break;
            case "bulktrade":
                this.bulkTrade(player, args[1], args[2], args[3]);
                break;
            case "addbuyitem":
                this.addBuyItem(player, args[1]);
                break;
            case "addsellitem":
                this.addSellItem(player, args[1]);
                break;
            case "addshopcurrency":
                this.addShopCurrency(player);
                break;
            case "deposititem":
                this.depositItem(player);
                break;
            case "stockshop":
                this.stockShop(player);
                break;
            case "printshopstock":
                this.printshopstock(player);
                break;
            case "setpurchasingpower":
                this.setPurchasingPower(player, args[1], args[2]);
                break;
            case "upgradedomain":
                this.upgradeDomain(player);
                break;
            case "downgradedomain":
                this.downgradeDomain(player);
                break;
            case "setpopulation":
                this.setPopulation(player, args[1]);
                break;
        }
    }

    private void setPopulation(Player player, String populationString) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = Mapper.getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();

        int population = Integer.parseInt(populationString);
        domain.setPopulation(population);
    }

    private void upgradeDomain(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = Mapper.getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();

        DomainManager domainManager = ExtensiveDomains.instance.domainManager;
        domainManager.upgradeDomain(domain);

        player.sendMessage("Upgraded domain to " + domain.getDomainTier().getName() + "!");
    }

    private void downgradeDomain(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = Mapper.getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();

        DomainManager domainManager = ExtensiveDomains.instance.domainManager;
        domainManager.downgradeDomain(domain);

        player.sendMessage("Downgraded domain to " + domain.getDomainTier().getName() + "!");
    }

    private void createDomain(Player player) {
        Chunk chunk = Utils.getChunkAtPlayerLocation(player);
        DomainManager domainManager = ExtensiveDomains.instance.domainManager;
        domainManager.createDomain(chunk, player);
        player.sendMessage("Created a domain");
    }

    private void claimChunk(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = Mapper.getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();
        Chunk chunk = Utils.getChunkAtPlayerLocation(player);

        try {
            DomainManager domainManager = ExtensiveDomains.instance.domainManager;
            domainManager.claimChunk(domain, chunk);
        } catch (Exception e) {
            player.sendMessage(e.getMessage());
            return;
        }

        player.sendMessage("Claimed chunk at your location");
    }

    private void unclaimChunk(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = Mapper.getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();
        Chunk chunk = Utils.getChunkAtPlayerLocation(player);

        try {
            DomainManager domainManager = ExtensiveDomains.instance.domainManager;
            domainManager.unclaimChunk(domain, chunk);
        } catch (Exception e) {
            player.sendMessage(e.getMessage());
            return;
        }

        player.sendMessage("Unclaimed chunk at your location");
    }

    @Deprecated
    private void claims(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = Mapper.getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();
        List<Claim> claims = domain.getClaims();
        player.sendMessage("Domain claims:");

        for (Claim claim : claims) {
            player.sendMessage("  Claim at chunk: " + claim.getChunk().getX() + " | " + claim.getChunk().getZ());
        }
    }

    @Deprecated
    private void print(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = Mapper.getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();
        player.sendMessage("-- Domain --");
        player.sendMessage("Name: " + domain.getName());
        player.sendMessage("Population: " + domain.getPopulation());
        player.sendMessage("Influence: " + domain.getInfluence());
        player.sendMessage("Domain Tier Name: " + (domain.getDomainTier() == null ? "none" : domain.getDomainTier().getName()));
        player.sendMessage("Domain Tier Level: " + (domain.getDomainTier() == null ? "none" : domain.getDomainTier().getLevel()));
        player.sendMessage("");
        player.sendMessage("-- Citizen --");
        player.sendMessage("Player: " + citizen.getPlayer().getName());
        player.sendMessage("Title: " + (citizen.getTitle() == null ? "none" : citizen.getTitle().getName()));
    }

    @Deprecated
    private void isClaimed(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = Mapper.getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();
        Chunk chunk = player.getLocation().getChunk();
        player.sendMessage("Chunk has been claimed: " + (domain.getClaimAtChunk(chunk) != null));
    }

    @Deprecated
    private void addClaimProtection(Player player, String protectionName) {
        Chunk chunk = player.getLocation().getChunk();
        Claim claim = Mapper.getClaimFromChunk(chunk);
        ClaimProtection claimProtection = ClaimProtection.getProtectionFromName(protectionName);
        DomainManager domainManager = ExtensiveDomains.instance.domainManager;
        domainManager.addClaimProtection(claim, claimProtection);

        // todo remove below
        if (claim == null) {
            player.sendMessage("Chunk is not claimed!");
            return;
        }

        player.sendMessage("Added claim protection");
    }

    @Deprecated
    private void removeProtection (Player player, String protectionName) {

    }

    private void addPermission(Player player, String actionName, String conditionName, String conditionArg) {
        Chunk chunk = player.getLocation().getChunk();
        Claim claim = Mapper.getClaimFromChunk(chunk);
        ClaimPermission.ClaimAction claimAction = ClaimPermission.ClaimAction.getClaimActionByName(actionName);
        DomainManager domainManager = ExtensiveDomains.instance.domainManager;

        switch (conditionName.toLowerCase()) {
            case "player-with-title":
                domainManager.addClaimPermission(claim, claimAction, new HasTitleCondition(conditionArg));
                break;
            case "player-with-name":
                domainManager.addClaimPermission(claim, claimAction, new HasNameCondition(conditionArg));
                break;
            case "player-belongs-to":
                break;
        }
    }

    private void removePermission(Player player, String actionName) {

    }

    private void createShop(Player player, String shopName) {
        EconomyManager economyManager = ExtensiveDomains.instance.economyManager;
        economyManager.createShop(player, shopName);
        player.sendMessage("Created new shop '" + shopName + "'!");
    }

    private void addSellItem(Player player, String priceString) {
        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        Shop shop = citizen.getShop();

        if (shop == null) {
            player.sendMessage("You do not have a shop!");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            player.sendMessage("Cannot sell air!");
            return;
        }

        double price = Double.parseDouble(priceString);
//        EconomyManager.addShopSellItem(shop, item, price);
        shop.addSellItem(item, price);
        String message = "Your shop is now selling " + item.getType().name() + " for " + price + (shop.getCurrencyAccount() == null ? "" : shop.getCurrencyAccount().getCurrency() == null ? "" : " " + shop.getCurrencyAccount().getCurrency().getName());
        player.sendMessage(message);
    }

    private void addBuyItem(Player player, String priceString) {
        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        Shop shop = citizen.getShop();

        if (shop == null) {
            player.sendMessage("You do not have a shop!");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            player.sendMessage("Cannot buy air!");
            return;
        }

        double price = Double.parseDouble(priceString);
        shop.addBuyItem(item, price);
        String message = "Your shop is now buying " + item.getType().name() + " for " + priceString + (shop.getCurrencyAccount() == null ? "" : shop.getCurrencyAccount().getCurrency() == null ? "" : " " + shop.getCurrencyAccount().getCurrency().getName());
        player.sendMessage(message);
    }

    private void createCurrency(Player player, String currencyName) {
        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        Domain domain = citizen.getDomain();

        if (domain == null) {
            player.sendMessage("You can't create a currency, as you are not part of a domain!");
            return;
        }

        if (!ExtensiveDomains.instance.domainManager.domainCanPerformAction(domain, DomainAction.CREATE_CURRENCY)) {
            player.sendMessage("Your domain cannot create a currency! You must upgrade it first.");
            return;
        }

        EconomyManager economyManager = ExtensiveDomains.instance.economyManager;
        economyManager.createCurrency(domain, currencyName);

        player.sendMessage("Created currency with name '" + currencyName + "'");
    }

    private void adoptCurrency(Player player, String currencyName) {
        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        Currency currency = Mapper.getCurrencyFromName(currencyName);
        CurrencyAccount currencyAccount = new CurrencyAccount(currency);
        citizen.setPrimaryCurrencyAccount(currencyAccount);

        player.sendMessage("Adopted currency '" + currency.getName() + "'");
    }

    private void printBalance(Player player) {
        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        CurrencyAccount currencyAccount = citizen.getPrimaryCurrencyAccount();

        if (currencyAccount == null) {
            player.sendMessage("You do not have an account to check your balance!");
            return;
        }

        double balance = currencyAccount.getBalance();

        player.sendMessage("Total balance: " + balance);
    }

    private void buyItemFromShop(Player player, String materialName, String quantityString, String shopPlayerName) {
        Player shopPlayer = Bukkit.getPlayer(shopPlayerName);

        if (shopPlayer == null) {
            player.sendMessage("Player '" + shopPlayerName + "' is not online!");
            return;
        }

        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        Citizen shopCitizen = Mapper.getCitizenFromUUID(shopPlayer.getUniqueId());
        Shop shop = Mapper.getShopFromCitizen(shopCitizen);

        if (shop == null) {
            player.sendMessage("Player does not own a shop!");
            return;
        }

        Material material = Material.getMaterial(materialName);

        if (material == null) {
            player.sendMessage("Material '" + materialName + "' is not valid!");
            return;
        }

        int quantity = Integer.parseInt(quantityString);
        ItemStack item = new ItemStack(material);
        ShopOrder shopOrder = new ShopOrder();
        shopOrder.addItemToBuy(item, quantity);
        VirtualInventory virtualInventory = citizen.getInventory();
        boolean shopTraded = shop.processOrder(shopOrder, virtualInventory, citizen.getPrimaryCurrencyAccount());

        if (!shopTraded) {
            player.sendMessage("There was an issue while trying to trade this item!");
            return;
        }

        player.sendMessage("Bought " + quantityString + " '" + materialName + "' from " + shopPlayerName + "'s shop!");
    }

    private void sellItemToShop(Player player, String materialName, String amountString, String shopPlayerName) {
        Player shopPlayer = Bukkit.getPlayer(shopPlayerName);

        if (shopPlayer == null) {
            player.sendMessage("Player '" + shopPlayerName + "' is not online!");
            return;
        }

        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        Citizen shopCitizen = Mapper.getCitizenFromUUID(shopPlayer.getUniqueId());
        Shop shop = Mapper.getShopFromCitizen(shopCitizen);

        if (shop == null) {
            player.sendMessage("Player does not own a shop!");
            return;
        }

        Material material = Material.getMaterial(materialName);

        if (material == null) {
            player.sendMessage("Material '" + materialName + "' is not valid!");
            return;
        }

        int amount = Integer.parseInt(amountString);
        ItemStack item = new ItemStack(material);
        ShopOrder shopOrder = new ShopOrder();
        shopOrder.addItemToSell(item, amount);
        VirtualInventory virtualInventory = citizen.getInventory();
        boolean shopTraded = shop.processOrder(shopOrder, virtualInventory, citizen.getPrimaryCurrencyAccount());

        if (!shopTraded) {
            player.sendMessage("There was an issue while trying to trade this item!");
            return;
        }

        player.sendMessage("Sold " + amountString + " '" + materialName + "' to " + shopPlayerName + "'s shop!");

    }

    private void bulkTrade(Player player, String buyingItems, String sellingItems, String shopPlayerName) {
        Player shopPlayer = Bukkit.getPlayer(shopPlayerName);

        if (shopPlayer == null) {
            player.sendMessage("Player '" + shopPlayerName + "' is not online!");
            return;
        }

        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        Citizen shopCitizen = Mapper.getCitizenFromUUID(shopPlayer.getUniqueId());
        Shop shop = Mapper.getShopFromCitizen(shopCitizen);

        if (shop == null) {
            player.sendMessage("Player does not own a shop!");
            return;
        }

        ShopOrder shopOrder = new ShopOrder();
        String[] buyingItemsArray = buyingItems.split(",");

        for (String materialName : buyingItemsArray) {
            Material material = Material.getMaterial(materialName);

            if (material == null) {
                player.sendMessage("Material '" + materialName + "' is not valid!");
                return;
            }

            ItemStack item = new ItemStack(material);
            shopOrder.addItemToBuy(item, 1);
        }

        String[] sellingItemsArray = sellingItems.split(",");

        for (String materialName : sellingItemsArray) {
            Material material = Material.getMaterial(materialName);

            if (material == null) {
                player.sendMessage("Material '" + materialName + "' is not valid!");
                return;
            }

            ItemStack item = new ItemStack(material);
            shopOrder.addItemToSell(item, 1);
        }

        VirtualInventory virtualInventory = citizen.getInventory();
        boolean shopTraded = shop.processOrder(shopOrder, virtualInventory, citizen.getPrimaryCurrencyAccount());

        if (!shopTraded) {
            player.sendMessage("There was an issue while trying to trade this item!");
            return;
        }

        player.sendMessage("Successfully traded with shop!");
    }

    private void printInventory(Player player) {
        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        VirtualInventory inventory = citizen.getInventory();

        player.sendMessage("Inventory:");
        for (ItemStack item : inventory.getItems()) {
            player.sendMessage("  - " + inventory.getItemQuantity(item) + " " + item.getType().name());
        }
    }

    private void addShopCurrency(Player player) {
        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        Shop shop = citizen.getShop();
        CurrencyAccount currencyAccount = citizen.getPrimaryCurrencyAccount();
        shop.setCurrencyAccount(currencyAccount);
        player.sendMessage("Shop '" + shop.getName() + "' is now using '" + currencyAccount.getCurrency().getName() + "'");
    }

    private void depositItem(Player player) {
        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        VirtualInventory inventory = citizen.getInventory();
        ItemStack item = player.getInventory().getItemInMainHand();
        inventory.addItem(item, 1);

        player.sendMessage("Added 1 " + item.getType().name() + " to virtual inventory");
    }

    private void withdrawItem(Player player, String materialName) {
    }

    private void addBalance(Player player, String amountString) {
        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        CurrencyAccount currencyAccount = citizen.getPrimaryCurrencyAccount();

        if (currencyAccount == null) {
            player.sendMessage("You do not have a currency to add funds to!");
            return;
        }

        double amount = Double.parseDouble(amountString);
        EconomyManager economyManager = ExtensiveDomains.instance.economyManager;
        economyManager.addBalance(currencyAccount, amount);
        player.sendMessage("Added " + amount + " to your account!");
    }

    private void stockShop(Player player) {
        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        Shop shop = citizen.getShop();

        if (shop == null) {
            player.sendMessage("You do not have a shop!");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        boolean shopIsSellingItem = shop.getSellItemPrice(item) != -1.0;

        if (!shopIsSellingItem) {
            player.sendMessage("Your shop isn't selling " + item.getType().name() + "!");
            return;
        }

        int amount = item.getAmount();
//        EconomyManager.addShopStock(shop, item, amount);
        shop.addStock(item, amount);
        player.getInventory().removeItem(item);

        player.sendMessage("Stocked " + amount + " " + item.getType().name() + " into your shop!");
    }

    private void printshopstock(Player player) {
        Citizen citizen = Mapper.getCitizenFromUUID(player.getUniqueId());
        Shop shop = citizen.getShop();

        if (shop == null) {
            player.sendMessage("You do not have a shop!");
            return;
        }

        VirtualInventory inventory = shop.getInventory();

        player.sendMessage("Inventory:");
        for (ItemStack item : inventory.getItems()) {
            player.sendMessage("  - " + inventory.getItemQuantity(item) + " " + item.getType().name());
        }
    }

    private void setPurchasingPower(Player player, String currencyName, String purchasingPowerString) {
        Currency currency = Mapper.getCurrencyFromName(currencyName);

        if (currency == null) {
            player.sendMessage("No currency with name '" + currencyName + "' found!");
            return;
        }

        double purchasingPower = Double.parseDouble(purchasingPowerString);
        currency.setPurchasingPower(purchasingPower);

        player.sendMessage("Set purchasing power of currency '" + currencyName + "' to " + purchasingPowerString);
    }
}
