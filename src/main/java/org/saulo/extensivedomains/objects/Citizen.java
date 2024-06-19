package org.saulo.extensivedomains.objects;

import org.bukkit.entity.Player;

import java.util.UUID;

public class Citizen {
    private Player player;
    private UUID uuid;
    private Domain domain;
    private CitizenTitle title; // if more are to be allowed, there needs to be a way to rank each title (i.e. check which rank is higher)

    public Citizen(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Citizen)) {
            return false;
        }

        UUID objUUID = ((Citizen) obj).uuid;

        return this.uuid == objUUID;
    }
}
