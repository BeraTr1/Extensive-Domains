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

    public static Citizen getCitizenFromUUID(UUID uuid) {
        return uuidCitizenMap.getOrDefault(uuid, null);
    }

    public static void addCitizenWithUUID(Citizen citizen, UUID uuid) {
        uuidCitizenMap.put(uuid, citizen);
    }

    public static Claim getClaimFromChunk(Chunk chunk) {
        return chunkClaimMap.getOrDefault(chunk, null);
    }

    public static void addClaimWithChunk(Claim claim, Chunk chunk) {
        chunkClaimMap.put(chunk, claim);
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

    public static Currency getCurrencyFromName(String name) {
        return nameCurrencyMap.getOrDefault(name, null);
    }

    public static void addCurrencyWithName(String name, Currency currency) {
        nameCurrencyMap.put(name, currency);
    }

    public static void addShopWithName(String name, Shop shop) {
        nameShopMap.put(name, shop);
    }

    public static Shop getShopFromName(String name) {
        return nameShopMap.getOrDefault(name, null);
    }
}
