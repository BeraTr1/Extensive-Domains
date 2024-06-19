package org.saulo.extensivedomains.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.saulo.extensivedomains.managers.CitizenManager;
import org.saulo.extensivedomains.objects.Citizen;

import java.util.UUID;

public class PlayerListener implements Listener {
    private CitizenManager citizenManager;

    public PlayerListener(CitizenManager citizenManager) {
        this.citizenManager = citizenManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        Citizen citizen = citizenManager.getRegisteredCitizen(playerUUID);

        if (citizen != null) {
            return;
        }

        citizenManager.registerNewCitizen(playerUUID);
    }
}
