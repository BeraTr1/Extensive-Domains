package com.extensivedomains.listeners;

import com.extensivedomains.managers.CitizenManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import com.extensivedomains.managers.CitizenManager;
import com.extensivedomains.objects.citizen.Citizen;

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
