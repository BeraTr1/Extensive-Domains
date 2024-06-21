package com.extensivedomains.listeners;

import com.extensivedomains.events.ChangeDayEvent;
import com.extensivedomains.ExtensiveDomains;
import com.extensivedomains.managers.DailyTaskManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.extensivedomains.events.ChangeDayEvent;
import com.extensivedomains.managers.DailyTaskManager;

public class ChangeDayListener implements Listener {
    @EventHandler
    public void onDayChange(ChangeDayEvent event) {
        DailyTaskManager dailyTaskManager = ExtensiveDomains.instance.dailyTaskManager;
        dailyTaskManager.executeDailyTasks();
    }
}
