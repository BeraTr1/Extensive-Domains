package com.extensivedomains.listeners;

import com.extensivedomains.managers.ClaimManager;
import com.extensivedomains.managers.DomainManager;
import com.extensivedomains.ExtensiveDomains;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import com.extensivedomains.managers.ClaimManager;
import com.extensivedomains.managers.DomainManager;
import com.extensivedomains.objects.citizen.Citizen;
import com.extensivedomains.objects.claim.Claim;
import com.extensivedomains.objects.claim.ClaimPermission;
import com.extensivedomains.objects.claim.ClaimProtection;

public class BlockListener implements Listener {
    private ClaimManager claimManager;

    public BlockListener(ClaimManager claimManager) {
        this.claimManager = claimManager;
    }

    @EventHandler
    public void onBlockFromToEvent(BlockFromToEvent event) {
//        System.out.println("Block moving from one location to another (liquid block)");

        Block sourceBlock = event.getBlock();
        Location sourceLocation = sourceBlock.getLocation();
        Chunk sourceChunk = sourceBlock.getChunk();
        Claim sourceClaim = claimManager.getRegisteredClaim(sourceChunk);
        boolean sourceChunkIsClaimed = sourceClaim != null;

        Block toBlock = event.getToBlock();
        Location toLocation = toBlock.getLocation();
        Chunk toChunk = toBlock.getChunk();
        Claim toClaim = claimManager.getRegisteredClaim(toChunk);
        boolean toChunkIsClaimed = toClaim != null;

        boolean bothClaimsBelongToSameDomain = sourceChunkIsClaimed && toChunkIsClaimed && (sourceClaim.getDomain().equals(toClaim.getDomain()));
        boolean targetClaimHasProtectionAgainstLiquidFlow = toChunkIsClaimed && toClaim.hasProtectionAgainst(ClaimProtection.LIQUID_FLOW);
        boolean preventLiquidFlow = !bothClaimsBelongToSameDomain && targetClaimHasProtectionAgainstLiquidFlow;

        String sourceLoc = sourceLocation.getBlockX() + " | " + sourceLocation.getBlockY() + " | " + sourceLocation.getBlockZ();
        String toLoc = toLocation.getBlockX() + " | " + toLocation.getBlockY() + " | " + toLocation.getBlockZ();

//        System.out.println("Block at (" + sourceLoc + ") is moving to (" + toLoc + ")");
//        System.out.println("\tSource block chunk is claimed: " + sourceChunkIsClaimed);
//        System.out.println("\tTarget block chunk is claimed: " + sourceChunkIsClaimed);
//        if (sourceChunkIsClaimed && toChunkIsClaimed) {
//            System.out.println("\t\tBoth chunks belong to same domain: " + bothClaimsBelongToSameDomain);
//        }
//        System.out.println("\t\tTarget chunk has protection against liquid flow: " + targetClaimHasProtectionAgainstLiquidFlow);
//        System.out.println("\tEvent is being cancelled: " + preventLiquidFlow);

        if (!preventLiquidFlow) return;

        event.setCancelled(true);

        //todo protect from dragon egg teleport (always, i.e. not toggleable by players)!
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        Claim claim = claimManager.getRegisteredClaim(chunk);

        if (claim == null) {
            return;
        }

        Citizen claimOwner = claim.getOwner();

        boolean playerOwnsClaim = claimOwner != null && claimOwner.getPlayer().equals(player);

        // todo check if player can destroy blocks through manager (manager.playerCanDestroyBlocks(claim))

        if (playerOwnsClaim) return;

        DomainManager domainManager = ExtensiveDomains.instance.domainManager;
        boolean playerCanDestroyInClaim = domainManager.playerHasPermission(player, claim, ClaimPermission.ClaimAction.DESTROY);

        if (playerCanDestroyInClaim) return;

        event.setCancelled(true);
        player.sendMessage("You don't have permission to destroy blocks in this chunk!");
    }
}
