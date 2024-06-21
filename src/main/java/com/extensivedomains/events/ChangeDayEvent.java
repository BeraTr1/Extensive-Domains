package com.extensivedomains.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ChangeDayEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    // static method for compatibility with older versions
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
