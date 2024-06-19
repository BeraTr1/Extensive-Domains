package org.saulo.extensivedomains.managers;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.saulo.extensivedomains.ExtensiveDomains;
import org.saulo.extensivedomains.domainactions.DomainAction;
import org.saulo.extensivedomains.playerconditions.Condition;
import org.saulo.extensivedomains.objects.*;

import java.util.List;
import java.util.UUID;

public class DomainManager {
    private ExtensiveDomains plugin;

    public DomainManager(ExtensiveDomains plugin) {
    private ClaimManager claimManager;
        this.plugin = plugin;
        this.claimManager = claimManager;
    }
    
    public void createDomain(Chunk chunk, Player player) {
        // todo check if chunk is claimed
        Citizen citizen = new Citizen(player);
        Domain domain = new Domain(citizen);
        Claim claim = new Claim(domain, chunk);
        domain.addClaim(claim);
        citizen.setDomain(domain);
        domain.addCitizen(citizen);

        UUID domainUUID = domain.getUUID();
        Mapper.addDomainWithUUID(domain, domainUUID);
        Mapper.addClaimWithChunk(claim, chunk);

        DomainTierManager domainTierManager = ExtensiveDomains.instance.domainTierManager;
        final int startingDomainTierLevel = 1;
        DomainTier domainTier = domainTierManager.getDomainTierFromLevel(startingDomainTierLevel);
        domain.setDomainTier(domainTier);
    }

    public void claimChunk(Domain domain, Chunk chunk) throws Exception {
        Claim claim = claimManager.createClaim(chunk);
        domain.addClaim(claim);
    }

    public void unclaimChunk(Domain domain, Chunk chunk) throws Exception {
        Claim claim = claimManager.getRegisteredClaim(chunk);

        if (!domainHasClaim(domain, claim)) {
            throw new Exception("Domain doesn't own this chunk!");
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
