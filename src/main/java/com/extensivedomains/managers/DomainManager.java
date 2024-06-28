package com.extensivedomains.managers;

import com.extensivedomains.conditions.domainconditions.DomainCondition;
import com.extensivedomains.exceptions.ExtensiveDomainsException;
import com.extensivedomains.objects.citizen.Citizen;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import com.extensivedomains.objects.claim.Claim;
import com.extensivedomains.objects.claim.ClaimPermission;
import com.extensivedomains.objects.domain.Domain;
import com.extensivedomains.objects.domain.actions.DomainAction;
import com.extensivedomains.objects.domain.tier.DomainTier;

import java.util.*;

public class DomainManager {
    private final ClaimManager claimManager;
    private final DomainTierManager domainTierManager;

    private final Map<UUID, Domain> registeredDomains = new HashMap<>();

    public DomainManager(ClaimManager claimManager, DomainTierManager domainTierManager) {
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

        DomainTier domainTier = domainTierManager.getLowestDomainTier();
        Domain domain = new Domain(uuid, domainTier);
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

    public void renameDomain(Domain domain, String name) throws ExtensiveDomainsException {
        domain.setName(name);
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

    // todo remove
    public boolean playerHasPermission(Player player, Claim claim, ClaimPermission.ClaimAction claimAction) {
        return claim.getClaimPermission().playerHasPermission(player, claimAction);
    }

    public void addCitizen(Domain domain, Citizen citizen) {
        if (citizen == null) {
            return;
        }

        // todo check if citizen is already a member in the domain
        domain.addCitizen(citizen);
    }

    public void upgradeDomain(Domain domain) throws ExtensiveDomainsException {
        DomainTier domainTier = domain.getDomainTier();
        DomainTier highestDomainTier = domainTierManager.getHighestDomainTier();
        boolean isHighestDomainTier = domainTier.getLevel() == highestDomainTier.getLevel();

        if (isHighestDomainTier) {
            throw new ExtensiveDomainsException("Domain is already at the highest tier!");
        }

        int domainTierLevel = domainTier.getLevel();
        int nextDomainTierLevel = domainTierLevel + 1;
        DomainTier nextDomainTier = domainTierManager.getRegisteredDomainTier(nextDomainTierLevel);

        if (nextDomainTier == null) {
            throw new ExtensiveDomainsException("An internal exception occurred!");
            // todo display error message in console "Domain tier with level " + nextDomainTierLevel + " not found!"
        }

        boolean domainCanUpgrade = this.domainCanUpgradeTier(domain, nextDomainTier);

        if (!domainCanUpgrade) {
            throw new ExtensiveDomainsException("Domain doesn't meet all required conditions to upgrade!");
        }

        domain.setDomainTier(nextDomainTier);
    }

    public boolean domainCanUpgradeTier(Domain domain, DomainTier domainTier) {
        List<DomainCondition> conditions = domainTier.getConditions();

        for (DomainCondition condition : conditions) {
            if (!condition.test(domain)) {
                return false;
            }
        }

        return true;
    }

    public void downgradeDomain(Domain domain) throws ExtensiveDomainsException {
        DomainTier domainTier = domain.getDomainTier();
        DomainTier lowestDomainTier = domainTierManager.getLowestDomainTier();
        boolean isLowestDomainTier = domainTier.getLevel() == lowestDomainTier.getLevel();

        if (isLowestDomainTier) {
            throw new ExtensiveDomainsException("Domain is already at the lowest tier!");
        }

        int domainTierLevel = domainTier.getLevel();
        int previousDomainTierLevel = domainTierLevel - 1;
        DomainTier previousDomainTier = domainTierManager.getRegisteredDomainTier(previousDomainTierLevel);

        if (previousDomainTier == null) {
            throw new ExtensiveDomainsException("An internal exception occurred!");
            // todo display error message in console "Domain tier with level " + previousDomainTierLevel + " not found!"
        }

        domain.setDomainTier(previousDomainTier);
    }

    public boolean domainCanPerformAction(Domain domain, Class<? extends DomainAction> actionClass) {
        DomainTier domainTier = domain.getDomainTier();
        List<Class<? extends DomainAction>> domainTierAllowedActions = domainTier.getAllowedActions();

        return domainTierAllowedActions.contains(actionClass);
    }
}
