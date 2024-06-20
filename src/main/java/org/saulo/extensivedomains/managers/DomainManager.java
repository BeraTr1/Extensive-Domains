package org.saulo.extensivedomains.managers;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.saulo.extensivedomains.ExtensiveDomains;
import org.saulo.extensivedomains.domainactions.DomainAction;
import org.saulo.extensivedomains.exceptions.ExtensiveDomainsException;
import org.saulo.extensivedomains.objects.*;

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

    public void registerDomain(UUID uuid, Domain domain) {
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
        domain.addClaim(claim);
        this.registerDomain(uuid, domain);

        return domain;
    }

    public void deleteDomain(Domain domain) {
        // todo code to delete domain
    }

    public void claimChunk(Domain domain, Chunk chunk) throws Exception {
        Claim claim = claimManager.createClaim(chunk);
    public void claimChunk(Domain domain, Chunk chunk) throws ExtensiveDomainsException {
        if (domain == null || chunk == null) {
            throw new ExtensiveDomainsException("Domain or chunk doesn't exist!");
        }

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
        return claim.getDomain().equals(domain);
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
