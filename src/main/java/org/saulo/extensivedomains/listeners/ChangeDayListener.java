package org.saulo.extensivedomains.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.saulo.extensivedomains.ExtensiveDomains;
import org.saulo.extensivedomains.events.ChangeDayEvent;
import org.saulo.extensivedomains.managers.DailyTaskManager;

public class ChangeDayListener implements Listener {
    @EventHandler
    public void onDayChange(ChangeDayEvent event) {
        DailyTaskManager dailyTaskManager = ExtensiveDomains.instance.dailyTaskManager;
        dailyTaskManager.executeDailyTasks();
    }
}
