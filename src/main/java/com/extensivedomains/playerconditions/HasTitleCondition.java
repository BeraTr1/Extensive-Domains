package com.extensivedomains.playerconditions;

import com.extensivedomains.managers.CitizenManager;
import org.bukkit.entity.Player;
import com.extensivedomains.managers.CitizenManager;
import com.extensivedomains.objects.citizen.Citizen;
import com.extensivedomains.objects.citizen.CitizenTitle;
import java.util.UUID;

public class HasTitleCondition extends Condition {
    private String titleName;
    private CitizenManager citizenManager;

    public HasTitleCondition(String titleName) {
        this.titleName = titleName;
    }

    @Override
    public boolean test(Player player) {
        UUID uuid = player.getUniqueId();
        Citizen citizen = citizenManager.getRegisteredCitizen(uuid);
        CitizenTitle citizenTitle = citizen.getTitle();
        String titleName = citizenTitle.getName();

        return this.titleName.equals(titleName);
    }
}
