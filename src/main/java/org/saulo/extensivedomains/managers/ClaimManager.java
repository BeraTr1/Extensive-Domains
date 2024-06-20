package org.saulo.extensivedomains.managers;

import org.bukkit.Chunk;
import org.saulo.extensivedomains.exceptions.ExtensiveDomainsException;
import org.saulo.extensivedomains.objects.Claim;
import org.saulo.extensivedomains.objects.ClaimPermission;
import org.saulo.extensivedomains.objects.ClaimProtection;
import org.saulo.extensivedomains.objects.Domain;
import org.saulo.extensivedomains.playerconditions.Condition;

import java.util.HashMap;
import java.util.Map;

public class ClaimManager {
    private Map<Chunk, Claim> registeredClaims = new HashMap<>();

    public void registerClaim(Chunk chunk, Claim claim) {
        this.registeredClaims.put(chunk, claim);
    }

    public void unregisterClaim(Chunk chunk) {
        if (!this.claimIsRegistered(chunk)) {
            return;
        }

        this.registeredClaims.remove(chunk);
    }

    public void unregisterClaim(Claim claim) {
        Chunk chunk = claim.getChunk();
        this.unregisterClaim(chunk);
    }

    public boolean claimIsRegistered(Chunk chunk) {
        return this.registeredClaims.containsKey(chunk);
    }

    public boolean claimIsRegistered(Claim claim) {
        Chunk chunk = claim.getChunk();

        return this.claimIsRegistered(chunk);
    }

    public Claim createClaim(Domain domain, Chunk chunk) throws ExtensiveDomainsException {
        if (chunkIsClaimed(chunk)) {
            throw new ExtensiveDomainsException("Chunk is already claimed!");
        }

        Claim claim = new Claim(domain, chunk);
        registerClaim(chunk, claim);

        return claim;
    }

    public boolean chunkIsClaimed(Chunk chunk) {
        Claim claim = this.registeredClaims.getOrDefault(chunk, null);

        return claim != null;
    }

    public Claim getRegisteredClaim(Chunk chunk) {
        return this.registeredClaims.getOrDefault(chunk, null);
    }

    public void addClaimProtection(Claim claim, ClaimProtection claimProtection) {
        claim.addProtection(claimProtection);
    }

    public void removeClaimProtection(Claim claim, ClaimProtection claimProtection) {
        claim.removeProtection(claimProtection);
    }

    public void addClaimPermission(Claim claim, ClaimPermission.ClaimAction claimAction, Condition condition) {
        claim.getClaimPermission().addPermissionCondition(claimAction, condition);
    }

    public static void removeClaimPermission(Claim claim, ClaimPermission.ClaimAction claimAction, Condition condition) {
        claim.getClaimPermission().removePermissionCondition(claimAction, condition);
    }
}
