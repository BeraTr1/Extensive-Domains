package com.extensivedomains.commands;

import com.extensivedomains.*;
import com.extensivedomains.exceptions.ExtensiveDomainsException;
import com.extensivedomains.managers.CitizenManager;
import com.extensivedomains.managers.ClaimManager;
import com.extensivedomains.managers.DomainManager;
import com.extensivedomains.objects.domain.actions.RenameDomain;
import com.extensivedomains.playerconditions.HasNameCondition;
import com.extensivedomains.playerconditions.HasTitleCondition;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.extensivedomains.objects.citizen.Citizen;
import com.extensivedomains.objects.claim.Claim;
import com.extensivedomains.objects.claim.ClaimPermission;
import com.extensivedomains.objects.claim.ClaimProtection;
import com.extensivedomains.objects.domain.Domain;

import java.util.List;
import java.util.UUID;

public class ExtensiveDomainsCommand implements CommandExecutor {
    private CitizenManager citizenManager;
    private ClaimManager claimManager;

    public ExtensiveDomainsCommand(CitizenManager citizenManager, ClaimManager claimManager) {
        this.citizenManager = citizenManager;
        this.claimManager = claimManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            this.parseCommand(player, args);
        }

        return false;
    }

    private void parseCommand(Player player, String[] args) {
        if (args.length == 0) return;

        switch (args[0].toLowerCase()) {
            case "create":
                this.createDomain(player);
                break;
            case "delete":
                this.deleteDomain(player);
                break;
            case "claim":
                this.claimChunk(player);
                break;
            case "unclaim":
                this.unclaimChunk(player);
                break;
            case "claims":
                this.claims(player);
                break;
            case "print":
                this.print(player);
                break;
            case "isclaimed":
                this.isClaimed(player);
                break;
            case "addprotection":
                this.addClaimProtection(player, args[1]);
                break;
            case "removeprotection":
                this.removeProtection(player, args[1]);
                break;
            case "addpermission":
                this.addPermission(player, args[1], args[2], args[3]);
                break;
            case "removepermission":
                this.removePermission(player, args[1]);
                break;
            case "upgradedomain":
                this.upgradeDomain(player);
                break;
            case "downgradedomain":
                this.downgradeDomain(player);
                break;
            case "setpopulation":
                this.setPopulation(player, args[1]);
                break;
            case "rename":
                this.renameDomain(player, args[1]);
                break;
            default:
                this.unrecognizedCommand(player, args[0]);
                break;
        }
    }

    private void unrecognizedCommand(Player player, String commandName) {
        player.sendMessage("Unrecognized command '" + commandName + "'");
    }

    private Citizen getCitizenFromUUID(UUID uuid) {
        return citizenManager.getRegisteredCitizen(uuid);
    }

    private void renameDomain(Player player, String name) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();

        if (!ExtensiveDomains.instance.domainManager.domainCanPerformAction(domain, RenameDomain.class)) {
            player.sendMessage("Domain cannot be renamed!");
            return;
        }

        try {
            ExtensiveDomains.instance.domainManager.renameDomain(domain, name);
            player.sendMessage("Renamed domain to '" + name + "'!");
        } catch (ExtensiveDomainsException e) {
            player.sendMessage("There was an error while trying to rename the domain: " + e.getMessage());
        }
    }

    private void deleteDomain(Player player) {

    }

    private void setPopulation(Player player, String populationString) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();

        int population = Integer.parseInt(populationString);
        domain.setPopulation(population);
        player.sendMessage("Set population to " + populationString + "!");
    }

    private void upgradeDomain(Player player) {
        System.out.println("Upgrading domain...");
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();

        DomainManager domainManager = ExtensiveDomains.instance.domainManager;

        try {
            domainManager.upgradeDomain(domain);
            player.sendMessage("Upgraded domain to level " + domain.getDomainTier().getLevel() + "!");
        } catch (ExtensiveDomainsException e) {
            player.sendMessage("There was an error while trying to upgrade this domain: " + e.getMessage());
        }
    }

    private void downgradeDomain(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();

        DomainManager domainManager = ExtensiveDomains.instance.domainManager;

        try {
            domainManager.downgradeDomain(domain);
            player.sendMessage("Downgraded domain to level " + domain.getDomainTier().getLevel() + "!");
        } catch (ExtensiveDomainsException e) {
            player.sendMessage("There was an error while trying to downgrade this domain: " + e.getMessage());
        }
    }

    private void createDomain(Player player) {
        try {
            Citizen citizen = getCitizenFromUUID(player.getUniqueId());
            Chunk chunk = Utils.getChunkAtPlayerLocation(player);
            DomainManager domainManager = ExtensiveDomains.instance.domainManager;
            Domain domain = domainManager.createDomain(chunk);
            domainManager.addCitizen(domain, citizen);
            citizen.setDomain(domain);
            player.sendMessage("Created a domain!");
        } catch (Exception e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void claimChunk(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();
        Chunk chunk = Utils.getChunkAtPlayerLocation(player);

        try {
            DomainManager domainManager = ExtensiveDomains.instance.domainManager;
            domainManager.claimChunk(domain, chunk);
            player.sendMessage("Claimed chunk at your location");
        } catch (Exception e) {
            player.sendMessage(e.getMessage());
        }
    }

    private void unclaimChunk(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();
        Chunk chunk = Utils.getChunkAtPlayerLocation(player);

        try {
            DomainManager domainManager = ExtensiveDomains.instance.domainManager;
            domainManager.unclaimChunk(domain, chunk);
            player.sendMessage("Unclaimed chunk at your location");
        } catch (Exception e) {
            player.sendMessage(e.getMessage());
        }
    }

    @Deprecated
    private void claims(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();
        List<Claim> claims = domain.getClaims();
        player.sendMessage("Domain claims:");

        for (Claim claim : claims) {
            player.sendMessage("  Claim at chunk: " + claim.getChunk().getX() + " | " + claim.getChunk().getZ());
        }
    }

    @Deprecated
    private void print(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();
        player.sendMessage("-- Domain --");
        player.sendMessage("Name: " + domain.getName());
        player.sendMessage("Population: " + domain.getPopulation());
        player.sendMessage("Influence: " + domain.getInfluence());
        player.sendMessage("Tier Level: " + domain.getDomainTier().getLevel());
        player.sendMessage("");
        player.sendMessage("-- Citizen --");
//        player.sendMessage("Player: " + citizen.getPlayer().getName());
    }

    @Deprecated
    private void isClaimed(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();
        Chunk chunk = player.getLocation().getChunk();
        player.sendMessage("Chunk has been claimed: " + (domain.getClaimAtChunk(chunk) != null));
    }

    @Deprecated
    private void addClaimProtection(Player player, String protectionName) {
        Chunk chunk = player.getLocation().getChunk();
        Claim claim = getClaimFromChunk(chunk);
        ClaimProtection claimProtection = ClaimProtection.getProtectionFromName(protectionName);
        DomainManager domainManager = ExtensiveDomains.instance.domainManager;
        claimManager.addClaimProtection(claim, claimProtection);

        // todo remove below
        if (claim == null) {
            player.sendMessage("Chunk is not claimed!");
            return;
        }

        player.sendMessage("Added claim protection");
    }

    @Deprecated
    private void removeProtection (Player player, String protectionName) {

    }

    private void addPermission(Player player, String actionName, String conditionName, String conditionArg) {
        Chunk chunk = player.getLocation().getChunk();
        Claim claim = getClaimFromChunk(chunk);
        ClaimPermission.ClaimAction claimAction = ClaimPermission.ClaimAction.getClaimActionByName(actionName);
        DomainManager domainManager = ExtensiveDomains.instance.domainManager;

        switch (conditionName.toLowerCase()) {
            case "player-with-title":
                claimManager.addClaimPermission(claim, claimAction, new HasTitleCondition(conditionArg));
                break;
            case "player-with-name":
                claimManager.addClaimPermission(claim, claimAction, new HasNameCondition(conditionArg));
                break;
            case "player-belongs-to":
                break;
        }
    }

    private void removePermission(Player player, String actionName) {

    }

    private Claim getClaimFromChunk(Chunk chunk) {
        return this.claimManager.getRegisteredClaim(chunk);
    }
}
