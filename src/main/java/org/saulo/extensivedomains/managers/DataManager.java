package org.saulo.extensivedomains.managers;

import org.saulo.extensivedomains.ExtensiveDomains;
import org.saulo.extensivedomains.data.Data;

public class DataManager {
    private final ExtensiveDomains plugin;
    private final Data data;

    public DataManager(ExtensiveDomains plugin, Data data) {
        this.plugin = plugin;
        this.data = data;
    }

    public void loadData() {
        data.loadAll();
    }

    public void saveData() {
        data.saveAll();
    }
}
