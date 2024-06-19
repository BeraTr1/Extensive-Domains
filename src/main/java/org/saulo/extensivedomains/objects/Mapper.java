package org.saulo.extensivedomains.objects;

import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Mapper {
    private static Map<UUID, Citizen> uuidCitizenMap = new HashMap<>();
    private static Map<UUID, Domain> uuidDomainMap = new HashMap<>();
    private static Map<Chunk, Claim> chunkClaimMap = new HashMap<>();

    public static Citizen getCitizenFromUUID(UUID uuid) {
        return uuidCitizenMap.getOrDefault(uuid, null);
    }

    public static void addCitizenWithUUID(Citizen citizen, UUID uuid) {
        uuidCitizenMap.put(uuid, citizen);
    }

    public static Map<UUID, Citizen> getCitizenUUIDMap() {
        return uuidCitizenMap;
    }

    public static Claim getClaimFromChunk(Chunk chunk) {
        return chunkClaimMap.getOrDefault(chunk, null);
    }

    public static void addClaimWithChunk(Claim claim, Chunk chunk) {
        chunkClaimMap.put(chunk, claim);
    }

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
