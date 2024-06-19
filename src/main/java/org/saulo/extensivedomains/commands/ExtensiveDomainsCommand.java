package org.saulo.extensivedomains.commands;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.saulo.extensivedomains.ExtensiveDomains;
import org.saulo.extensivedomains.Utils;
import org.saulo.extensivedomains.domainactions.DomainAction;
import org.saulo.extensivedomains.managers.CitizenManager;
import org.saulo.extensivedomains.playerconditions.HasNameCondition;
import org.saulo.extensivedomains.playerconditions.HasTitleCondition;
import org.saulo.extensivedomains.managers.DomainManager;
import org.saulo.extensivedomains.objects.*;

import java.util.List;
import java.util.UUID;

public class ExtensiveDomainsCommand implements CommandExecutor {
    private CitizenManager citizenManager;

    public ExtensiveDomainsCommand(CitizenManager citizenManager) {
        this.citizenManager = citizenManager;
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
        }
    }

    private Citizen getCitizenFromUUID(UUID uuid) {
        return citizenManager.getRegisteredCitizen(uuid);
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
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();

        DomainManager domainManager = ExtensiveDomains.instance.domainManager;
        domainManager.upgradeDomain(domain);

        player.sendMessage("Upgraded domain to " + domain.getDomainTier().getName() + "!");
    }

    private void downgradeDomain(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();

        DomainManager domainManager = ExtensiveDomains.instance.domainManager;
        domainManager.downgradeDomain(domain);

        player.sendMessage("Downgraded domain to " + domain.getDomainTier().getName() + "!");
    }

    private void createDomain(Player player) {
        Chunk chunk = Utils.getChunkAtPlayerLocation(player);
        DomainManager domainManager = ExtensiveDomains.instance.domainManager;
        domainManager.createDomain(chunk, player);
        player.sendMessage("Created a domain");
    }

    private void claimChunk(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();
        Chunk chunk = Utils.getChunkAtPlayerLocation(player);

        try {
            DomainManager domainManager = ExtensiveDomains.instance.domainManager;
            domainManager.claimChunk(domain, chunk);
        } catch (Exception e) {
            player.sendMessage(e.getMessage());
            return;
        }

        player.sendMessage("Claimed chunk at your location");
    }

    private void unclaimChunk(Player player) {
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = getCitizenFromUUID(playerUUID);
        Domain domain = citizen.getDomain();
        Chunk chunk = Utils.getChunkAtPlayerLocation(player);

        try {
            DomainManager domainManager = ExtensiveDomains.instance.domainManager;
            domainManager.unclaimChunk(domain, chunk);
        } catch (Exception e) {
            player.sendMessage(e.getMessage());
            return;
        }

        player.sendMessage("Unclaimed chunk at your location");
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
        player.sendMessage("Domain Tier Name: " + (domain.getDomainTier() == null ? "none" : domain.getDomainTier().getName()));
        player.sendMessage("Domain Tier Level: " + (domain.getDomainTier() == null ? "none" : domain.getDomainTier().getLevel()));
        player.sendMessage("");
        player.sendMessage("-- Citizen --");
//        player.sendMessage("Player: " + citizen.getPlayer().getName());
        player.sendMessage("Title: " + (citizen.getTitle() == null ? "none" : citizen.getTitle().getName()));
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
        Claim claim = Mapper.getClaimFromChunk(chunk);
        ClaimProtection claimProtection = ClaimProtection.getProtectionFromName(protectionName);
        DomainManager domainManager = ExtensiveDomains.instance.domainManager;
        domainManager.addClaimProtection(claim, claimProtection);

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
        Claim claim = Mapper.getClaimFromChunk(chunk);
        ClaimPermission.ClaimAction claimAction = ClaimPermission.ClaimAction.getClaimActionByName(actionName);
        DomainManager domainManager = ExtensiveDomains.instance.domainManager;

        switch (conditionName.toLowerCase()) {
            case "player-with-title":
                domainManager.addClaimPermission(claim, claimAction, new HasTitleCondition(conditionArg));
                break;
            case "player-with-name":
                domainManager.addClaimPermission(claim, claimAction, new HasNameCondition(conditionArg));
                break;
            case "player-belongs-to":
                break;
        }
    }

    private void removePermission(Player player, String actionName) {

    }
}
