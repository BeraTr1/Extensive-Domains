package org.saulo.extensivedomains.objects;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Citizen {
    private Player player;
    private UUID uuid;
    private Domain domain;
    private CurrencyAccount primaryCurrencyAccount;
    private List<CurrencyAccount> currencyAccounts;
    private CitizenTitle title; // if more are to be allowed, there needs to be a way to rank each title (i.e. check which rank is higher)
    private Shop shop;
    private VirtualInventory inventory = new VirtualInventory();

    public Citizen(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        Mapper.addCitizenWithUUID(this, uuid); //todo move this to event listener (onPlayerJoinEvent)
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Domain getDomain() {
        return this.domain;
    }

    public CitizenTitle getTitle() {
        return this.title;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean hasTitle(CitizenTitle citizenTitle) {
        return false;
    }

    public CurrencyAccount getPrimaryCurrencyAccount() {
        return this.primaryCurrencyAccount;
    }

    public void setPrimaryCurrencyAccount(CurrencyAccount currencyAccount) {
        this.primaryCurrencyAccount = currencyAccount;
    }

    public Shop getShop() {
        return this.shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public VirtualInventory getInventory() {
        return this.inventory;
    }
}
