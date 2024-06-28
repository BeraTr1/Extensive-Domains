package com.extensivedomains.objects.domain;

import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;
import com.extensivedomains.ExtensiveDomains;
import com.extensivedomains.managers.DomainTierManager;
import com.extensivedomains.objects.citizen.Citizen;
import com.extensivedomains.objects.claim.Claim;
import com.extensivedomains.objects.domain.tier.DomainTier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Domain {
    private String name = "";
    private UUID uuid;

    private List<Claim> claims = new ArrayList<>();
    private List<Citizen> citizens = new ArrayList<>();
    private double influence = 0.0;
    private int population = 0;
    private DomainTier domainTier;

    public Domain(UUID uuid, DomainTier domainTier) {
        this.uuid = uuid;
        this.domainTier = domainTier;
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

    public String getClaimsAsString() {
        StringBuilder claimsString = new StringBuilder();

        for (Claim claim : this.claims) {
            String chunkString = claim.getChunk().getX() + "," + claim.getChunk().getZ() + "," + claim.getChunk().getWorld().getName();

            if (claimsString.length() > 0) {
                claimsString.append("|").append(chunkString);
            } else {
                claimsString.append(chunkString);
            }
        }

        return claimsString.toString();
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
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

    public void setName(String name) {
        this.name = name;
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

    public String getCitizensAsString() {
        StringBuilder citizensString = new StringBuilder();

        for (Citizen citizen : this.citizens) {
            if (citizensString.length() > 0) {
                citizensString.append("|").append(citizen.getUUID().toString());
            } else {
                citizensString.append(citizen.getUUID().toString());
            }
        }

        return citizensString.toString();
    }

    public void addCitizen(Citizen citizen) {
        this.citizens.add(citizen);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Domain)) {
            return false;
        }

        UUID uuid = ((Domain) obj).uuid;

        return this.uuid == uuid;
    }
}
