package org.saulo.extensivedomains.objects;

import org.bukkit.Chunk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Mapper {
    private static Map<UUID, Citizen> uuidCitizenMap = new HashMap<>();
    private static Map<UUID, Domain> uuidDomainMap = new HashMap<>();
    private static Map<Chunk, Claim> chunkClaimMap = new HashMap<>();

    private static Map<UUID, Shop> uuidShopMap = new HashMap<>();
    private static Map<Citizen, Shop> citizenShopMap = new HashMap<>();
    private static Map<String, Shop> nameShopMap = new HashMap<>();

    private static Map<String, Currency> nameCurrencyMap = new HashMap<>();
    private static Map<UUID, Currency> uuidCurrencyMap = new HashMap<>();

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

    public static void addShopWithCitizen(Shop shop, Citizen citizen) {
        citizenShopMap.put(citizen, shop);
    }

    public static Shop getShopFromCitizen(Citizen citizen) {
        return citizenShopMap.getOrDefault(citizen, null);
    }

    public static Shop getShopFromUUID(UUID uuid) {
        return uuidShopMap.getOrDefault(uuid, null);
    }

    public static void addShopWithUUID(UUID uuid, Shop shop) {
        uuidShopMap.put(uuid, shop);
    }

    @Deprecated
    public static Currency getCurrencyFromName(String name) {
        return nameCurrencyMap.getOrDefault(name, null);
    }

    @Deprecated
    public static void addCurrencyWithName(String name, Currency currency) {
        nameCurrencyMap.put(name, currency);
    }

    public static Currency getCurrencyFromUUID(UUID uuid) {
        return uuidCurrencyMap.getOrDefault(uuid, null);
    }

    public static void addCurrencyWithUUID(UUID uuid, Currency currency) {
        uuidCurrencyMap.put(uuid, currency);
    }
}
