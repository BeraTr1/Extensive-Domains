package org.saulo.extensivedomains.objects;

import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Claim {
    private Domain domain;
    private Citizen owner;
    private Chunk chunk;
    private UUID uuid;
    private List<ClaimProtection> claimProtections = new ArrayList<>();
    private ClaimPermission claimPermission;

    public Claim(Chunk chunk) {
        this.chunk = chunk;
        this.claimPermission = new ClaimPermission();
    }

    public Chunk getChunk() {
        return this.chunk;
    }

    public Domain getDomain() {
        return this.domain;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void addProtection(ClaimProtection claimProtection) {
        if (this.claimProtections.contains(claimProtection)) return;

        this.claimProtections.add(claimProtection);
    }

    public void removeProtection(ClaimProtection claimProtection) {
        if (!this.claimProtections.contains(claimProtection)) return;

        this.claimProtections.remove(claimProtection);
    }

    public boolean hasProtectionAgainst(ClaimProtection claimProtection) {
        return this.claimProtections.contains(claimProtection);
    }

    public ClaimPermission getClaimPermission() {
        return this.claimPermission;
    }

    public Citizen getOwner() {
        return this.owner;
    }
}
