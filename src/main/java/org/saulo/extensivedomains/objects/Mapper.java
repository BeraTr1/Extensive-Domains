package org.saulo.extensivedomains.objects;

import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Mapper {
    private static Map<UUID, Domain> uuidDomainMap = new HashMap<>();

    public static Map<UUID, Domain> getUUIDDomainMap() {
        return uuidDomainMap;
    }

    public static Domain getDomainFromUUID(UUID uuid) {
        return uuidDomainMap.getOrDefault(uuid, null);
    }

    public static void addDomainWithUUID(Domain domain, UUID uuid) {
        uuidDomainMap.put(uuid, domain);
    }
}
