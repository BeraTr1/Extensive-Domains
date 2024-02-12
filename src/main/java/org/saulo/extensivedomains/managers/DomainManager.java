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
        this.plugin = plugin;
    }
    
    public void createDomain(Chunk chunk, Player player) {
        // todo check if chunk is claimed
        Citizen citizen = new Citizen(player);
        Domain domain = new Domain(citizen);
        Claim claim = new Claim(domain, chunk);
        domain.addClaim(claim);
        citizen.setDomain(domain);

        UUID domainUUID = domain.getUUID();
        Mapper.addDomainWithUUID(domain, domainUUID);
        Mapper.addClaimWithChunk(claim, chunk);

        DomainTierManager domainTierManager = ExtensiveDomains.instance.domainTierManager;
        final int startingDomainTierLevel = 1;
        DomainTier domainTier = domainTierManager.getDomainTierFromLevel(startingDomainTierLevel);
        domain.setDomainTier(domainTier);
    }

    public void claimChunk(Domain domain, Chunk chunk) throws Exception {
        if (chunkIsClaimed(chunk)) {
            throw new Exception("This chunk is already claimed");
        }

        Claim claim = new Claim(domain, chunk);
        domain.addClaim(claim);
        Mapper.addClaimWithChunk(claim, chunk);
        //todo create map for UUID -> Claim ?
    }

    public void unclaimChunk(Domain domain, Chunk chunk) throws Exception {
        if (!chunkIsClaimed(chunk)) {
            throw new Exception("This chunk is not claimed");
        }

        Claim claim = Mapper.getClaimFromChunk(chunk);
        Domain claimOwnerDomain = claim.getDomain();

        if (!claimOwnerDomain.equals(domain)) {
            throw new Exception("Domain does not own this chunk");
        }

        domain.removeClaim(claim);
        //todo remove claim from unclaimed chunk in Mapper
    }

    public boolean chunkIsClaimed(Chunk chunk) {
        Claim claim = Mapper.getClaimFromChunk(chunk);
        boolean chunkIsClaimed = claim != null;

        return chunkIsClaimed;
    }

    public void addClaimProtection(Claim claim, ClaimProtection claimProtection) {
        if (claim == null) {
            return;
        }

        claim.addProtection(claimProtection);
    }

    public void addClaimPermission(Claim claim, ClaimPermission.ClaimAction claimAction, Condition condition) {
        claim.getClaimPermission().addPermissionCondition(claimAction, condition);
    }

    public static void removeClaimPermission() {

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
