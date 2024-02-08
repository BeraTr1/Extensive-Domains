package org.saulo.extensivedomains.playerconditions;

import org.bukkit.entity.Player;

public class HasNameCondition extends Condition{
    private String playerName;

    public HasNameCondition(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public boolean test(Player player) {
        return this.playerName.equals(player.getName());
    }
}
