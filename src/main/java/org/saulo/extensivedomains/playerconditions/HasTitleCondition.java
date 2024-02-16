package org.saulo.extensivedomains.playerconditions;

import org.bukkit.entity.Player;
import org.saulo.extensivedomains.objects.Citizen;
import org.saulo.extensivedomains.objects.CitizenTitle;
import org.saulo.extensivedomains.objects.Mapper;

import java.util.UUID;

public class HasTitleCondition extends Condition {
    private String titleName;

    public HasTitleCondition(String titleName) {
        this.titleName = titleName;
    }

    @Override
    public boolean test(Player player) {
        UUID uuid = player.getUniqueId();
        Citizen citizen = Mapper.getCitizenFromUUID(uuid);
        CitizenTitle citizenTitle = citizen.getTitle();
        String titleName = citizenTitle.getName();

        return this.titleName.equals(titleName);
    }
}