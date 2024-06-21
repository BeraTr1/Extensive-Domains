package com.extensivedomains.managers;

import com.extensivedomains.ExtensiveDomains;

public class DailyTaskManager {
    private final ExtensiveDomains plugin;

    public DailyTaskManager(ExtensiveDomains plugin) {
        this.plugin = plugin;
    }

    public void executeDailyTasks() {
        System.out.println("Running daily tasks...");
        System.out.println("Collecting taxes...");
    }
}
