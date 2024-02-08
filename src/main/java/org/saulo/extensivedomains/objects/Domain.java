package org.saulo.extensivedomains.objects;

import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Domain {
    private String name = "";
    private List<Claim> claims = new ArrayList<>();
    private UUID uuid;

    private List<Citizen> citizens = new ArrayList<>();
    @Deprecated
    private Citizen founder; // either create a head of state field OR replace this with "headOfState"
    private Citizen headOfState;

    private double influence = 0.0;
    private int population = 0;
    private CurrencyAccount primaryCurrency;
    private List<CurrencyAccount> currencyAccounts; //todo remove, only 1 account per object will be used
    private DomainTier domainTier;
    private MarketCenter marketCenter = new MarketCenter();

    public Domain(Citizen headOfState) {
        this.headOfState = headOfState;
        this.uuid = UUID.randomUUID(); //todo Domain(UUID uuid, etc.), get uuid when trying to load data (and fails) or when creating new domain,
    }

    public void addClaim(Claim claim) {
        boolean foundClaim = false;

        for (Claim c : this.claims) {
            if (!c.getChunk().equals(claim.getChunk())) continue;

            foundClaim = true;
            break;
        }

        if (foundClaim) {
            return;
        }

        this.claims.add(claim);
    }

    public void removeClaim(Claim claim) {
        for (Claim c : this.claims) {
            if (c.getChunk().equals(claim.getChunk())) continue;

            this.claims.remove(c);
            break;
        }
    }

    public Claim getClaimAtChunk(Chunk chunk) {
        Claim claim = null;

        for (Claim c : this.claims) {
            if (!chunk.equals(c.getChunk())) continue;

            claim = c;
            break;
        }

        return claim;
    }

    public List<Claim> getClaims() {
        return this.claims;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public double getInfluence() {
        return this.influence;
    }

    public int getPopulation() {
        return this.population;
    }

    public String getName() {
        return this.name;
    }

    public CurrencyAccount getPrimaryCurrency() {
        return this.primaryCurrency;
    }

    public void setPrimaryCurrency(Currency currency) {
        this.primaryCurrency = new CurrencyAccount(currency);
    }

    @NotNull
    public DomainTier getDomainTier() {
        return this.domainTier;
    }

    public void setDomainTier(DomainTier domainTier) {
        this.domainTier = domainTier;
    }

    public void setPopulation(int population) {
        this.population = population;
    }
}
