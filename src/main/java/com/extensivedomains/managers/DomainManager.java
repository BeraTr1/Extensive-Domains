package com.extensivedomains.managers;

import com.extensivedomains.ExtensiveDomains;
import com.extensivedomains.exceptions.ExtensiveDomainsException;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import com.extensivedomains.objects.claim.Claim;
import com.extensivedomains.objects.claim.ClaimPermission;
import com.extensivedomains.objects.domain.Domain;
import com.extensivedomains.objects.domain.actions.DomainAction;
import com.extensivedomains.exceptions.ExtensiveDomainsException;
import com.extensivedomains.objects.domain.tier.DomainTier;

import java.util.*;

public class DomainManager {
    private ExtensiveDomains plugin;

    private ClaimManager claimManager;
    private DomainTierManager domainTierManager;

    private Map<UUID, Domain> registeredDomains = new HashMap<>();

    public DomainManager(ExtensiveDomains plugin, ClaimManager claimManager, DomainTierManager domainTierManager) {
        this.plugin = plugin;
        this.claimManager = claimManager;
        this.domainTierManager = domainTierManager;
    }

    public void registerDomain(UUID uuid, Domain domain) throws ExtensiveDomainsException {
        if (uuid == null || domain == null) {
            throw new ExtensiveDomainsException("Domain or UUID doesn't exist!");
        }

        this.registeredDomains.put(uuid, domain);
    }

    public void unregisterDomain(Domain domain) throws ExtensiveDomainsException {
        if (domain == null) {
            throw new ExtensiveDomainsException("Domain doesn't exist!");
        }

        if (this.domainIsRegistered(domain)) {
            throw new ExtensiveDomainsException("Domain is already registered!");
        }

        UUID uuid = domain.getUUID();
        this.registeredDomains.remove(uuid);
    }

    public boolean domainIsRegistered(Domain domain) {
        UUID uuid = domain.getUUID();

        return this.registeredDomains.containsKey(uuid);
    }

    public List<Domain> getRegisteredDomains() {
        return new ArrayList<>(this.registeredDomains.values());
    }

    public Domain getRegisteredDomain(UUID uuid) {
        return this.registeredDomains.getOrDefault(uuid, null);
    }

    public Domain createDomain(Chunk chunk) throws ExtensiveDomainsException {
        if (chunk == null) {
            throw new ExtensiveDomainsException("Chunk doesn't exist!");
        }

        if (claimManager.chunkIsClaimed(chunk)) {
            throw new ExtensiveDomainsException("Chunk is already claimed!");
        }

        UUID uuid = UUID.randomUUID();

        while (this.registeredDomains.containsKey(uuid)) {
            uuid = UUID.randomUUID();
        }

        Domain domain = new Domain(uuid);
        Claim claim = claimManager.createClaim(domain, chunk);
        domain.addClaim(claim);
        this.registerDomain(uuid, domain);

        return domain;
    }

    public void deleteDomain(Domain domain) throws ExtensiveDomainsException {
        if (domain == null) {
            throw new ExtensiveDomainsException("Domain doesn't exist!");
        }

        UUID uuid = domain.getUUID();
        this.registeredDomains.remove(uuid);
    }

    public void claimChunk(Domain domain, Chunk chunk) throws ExtensiveDomainsException {
        if (domain == null || chunk == null) {
            throw new ExtensiveDomainsException("Domain or chunk doesn't exist!");
        }

        Claim claim = claimManager.createClaim(domain, chunk);
        domain.addClaim(claim);
    }

    public void unclaimChunk(Domain domain, Chunk chunk) throws ExtensiveDomainsException {
        Claim claim = claimManager.getRegisteredClaim(chunk);

        if (!domainHasClaim(domain, claim)) {
            throw new ExtensiveDomainsException("Domain doesn't own this chunk!");
        }

        claimManager.unregisterClaim(claim);
        domain.removeClaim(claim);
    }

    public boolean domainHasClaim(Domain domain, Claim claim) {
        if (claim == null) {
            return false;
        }

        return claim.getDomain().equals(domain);
    }

    public boolean domainHasClaim(Domain domain, Chunk chunk) {
        Claim claim = claimManager.getRegisteredClaim(chunk);
        return this.domainHasClaim(domain, claim);
    }

    public boolean playerHasPermission(Player player, Claim claim, ClaimPermission.ClaimAction claimAction) {
        return claim.getClaimPermission().playerHasPermission(player, claimAction);
    }

    public void upgradeDomain(Domain domain) {
        System.out.println("Upgrading domain...");
        DomainTier currentDomainTier = domain.getDomainTier();
        int currentDomainTierLevel = currentDomainTier.getLevel();
        DomainTierManager domainTierManager = this.plugin.domainTierManager;
        DomainTier nextDomainTier = domainTierManager.getNextDomainTier(currentDomainTierLevel);

        if (nextDomainTier == null) {
            System.out.println("\tDomain is already at the highest tier!");
            return;
        }

        boolean domainCanUpgrade = domainCanUpgradeTier(domain, nextDomainTier.getLevel());

        if (!domainCanUpgrade) {
            System.out.println("\tDomain doesn't meet the upgrade requirements!");
            return;
        }

        System.out.println("\tUpgraded domain from level " + currentDomainTier.getLevel() + " to level " + nextDomainTier.getLevel() + ". The Domain is now a " + nextDomainTier.getName());

        domain.setDomainTier(nextDomainTier);
    }

    public boolean domainCanUpgradeTier(Domain domain, int domainTierLevel) {
        int populationRequiredToUpgrade = 0; //todo finish
        int domainPopulation = domain.getPopulation();

        return domainPopulation >= populationRequiredToUpgrade;
    }

    public void downgradeDomain(Domain domain) {
        System.out.println("Downgrading domain...");
        DomainTier currentDomainTier = domain.getDomainTier();
        int currentDomainTierLevel = currentDomainTier.getLevel();
        DomainTierManager domainTierManager = this.plugin.domainTierManager;
        DomainTier previousDomainTier = domainTierManager.getPreviousDomainTier(currentDomainTierLevel);

        if (previousDomainTier == null) {
            System.out.println("\tDomain is already at the lowest tier!");
            return;
        }

        System.out.println("\tDowngraded domain from level " + currentDomainTier.getLevel() + " to level " + previousDomainTier.getLevel() + ". The Domain is now a " + previousDomainTier.getName());

        domain.setDomainTier(previousDomainTier);
    }

    public boolean domainCanPerformAction(Domain domain, DomainAction action) {
        DomainTierManager domainTierManager = this.plugin.domainTierManager;
        int domainTier = domain.getDomainTier().getLevel();
        boolean domainCanPerformAction = domainTierManager.domainTierAllowsAction(domainTier, action);

        return domainCanPerformAction;
    }
}
