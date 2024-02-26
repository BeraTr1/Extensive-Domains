package org.saulo.extensivedomains.tasks;

import org.bukkit.Bukkit;
import org.saulo.extensivedomains.events.ChangeDayEvent;

public class DetectDayChangeTask implements Runnable {
    int lastTime = -1;

    @Override
    public void run() {
        int currentTime = (int) (Bukkit.getWorld("world").getTime() % 24000);
        boolean dayHasPassed = currentTime < this.lastTime;

        if (dayHasPassed) {
            ChangeDayEvent changeDayEvent = new ChangeDayEvent();
            Bukkit.getPluginManager().callEvent(changeDayEvent);
        }

        this.lastTime = currentTime;
    }
}
