package org.saulo.extensivedomains.playerconditions;

import org.bukkit.entity.Player;

import java.util.function.Predicate;

public abstract class Condition implements Predicate<Player> {
    @Override
    public boolean test(Player player) {
        return false;
    }
}
