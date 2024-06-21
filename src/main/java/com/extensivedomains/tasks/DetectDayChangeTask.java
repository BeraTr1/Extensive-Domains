package com.extensivedomains.tasks;

import com.extensivedomains.events.ChangeDayEvent;
import org.bukkit.Bukkit;
import com.extensivedomains.events.ChangeDayEvent;

public class DetectDayChangeTask implements Runnable {
    int lastTime = -1;

    @Override
    public void run() {
        // todo get list of worlds the plugin is running on
        int currentTime = (int) (Bukkit.getWorld("world").getTime() % 24000);
        boolean dayHasPassed = currentTime < this.lastTime;

        if (dayHasPassed) {
            ChangeDayEvent changeDayEvent = new ChangeDayEvent();
            Bukkit.getPluginManager().callEvent(changeDayEvent);
        }

        this.lastTime = currentTime;
    }
}
