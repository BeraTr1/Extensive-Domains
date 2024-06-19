package org.saulo.extensivedomains.objects;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Citizen {
    private Player player;
    private UUID uuid;
    private Domain domain;
    private CitizenTitle title; // if more are to be allowed, there needs to be a way to rank each title (i.e. check which rank is higher)

    public Citizen(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        Mapper.addCitizenWithUUID(this, uuid); //todo move this to event listener (onPlayerJoinEvent)
    }

    public Citizen(UUID uuid) {
        this.uuid = uuid;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public Domain getDomain() {
        return this.domain;
    }

    public CitizenTitle getTitle() {
        return this.title;
    }

    @Deprecated
    public Player getPlayer() {
        return this.player;
    }

    public boolean hasTitle(CitizenTitle citizenTitle) {
        return false;
    }

    public UUID getUUID() {
        return this.uuid;
    }
}
