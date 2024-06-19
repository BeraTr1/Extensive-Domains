package org.saulo.extensivedomains.managers;

import org.saulo.extensivedomains.objects.Citizen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CitizenManager {
    private Map<UUID, Citizen> citizenInstances = new HashMap<>();

    public void registerCitizen(UUID uuid, Citizen citizen) {
        this.citizenInstances.put(uuid, citizen);
    }

    public void registerCitizen(Citizen citizen) {
        UUID citizenUUID = citizen.getUUID();
        registerCitizen(citizenUUID, citizen);
    }

    public void unregisterCitizen(Citizen citizen) {
        UUID citizenUUID = citizen.getUUID();
        this.citizenInstances.remove(citizenUUID);
    }

    public List<Citizen> getCitizenInstances() {
        return (List<Citizen>) this.citizenInstances.values();
    }

    public Citizen getRegisteredCitizen(UUID uuid) {
        return this.citizenInstances.getOrDefault(uuid, null);
    }

    public void registerNewCitizen(UUID uuid) {
        Citizen citizen = new Citizen(uuid);
        registerCitizen(uuid, citizen);
    }
}
