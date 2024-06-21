package com.extensivedomains;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class Utils {
    // todo move function somewhere else
    public static Chunk getChunkAtPlayerLocation(Player player) {
        return player.getLocation().getChunk();
    }
}
