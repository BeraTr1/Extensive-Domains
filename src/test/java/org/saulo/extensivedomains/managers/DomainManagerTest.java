package org.saulo.extensivedomains.managers;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Chunk;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.saulo.extensivedomains.ExtensiveDomains;
import org.saulo.extensivedomains.exceptions.ExtensiveDomainsException;
import org.saulo.extensivedomains.objects.Domain;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DomainManagerTest {
    private ServerMock server;
    private ExtensiveDomains plugin;

    private DomainManager domainManager;
    private CitizenManager citizenManager;
    private ClaimManager claimManager;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(ExtensiveDomains.class);
        domainManager = plugin.domainManager;
        citizenManager = plugin.citizenManager;
        claimManager = plugin.claimManager;
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void registerDomain_existingDomain_registerDomain() throws ExtensiveDomainsException {
        UUID uuid = UUID.randomUUID();
        Domain expected = new Domain(uuid);
        domainManager.registerDomain(uuid, expected);
        Domain actual = domainManager.getRegisteredDomain(expected.getUUID());

        assertEquals(expected, actual);
    }

    @Test
    void registerDomain_nullDomain_throwException() {
        assertThrows(ExtensiveDomainsException.class, () -> {
            domainManager.registerDomain(null, null);
        });
    }

    @Test
    void unregisterDomain_existingDomain_unregisterDomain() throws ExtensiveDomainsException {
        UUID uuid = UUID.randomUUID();
        Domain domain = new Domain(uuid);
        Domain actual = domainManager.getRegisteredDomain(uuid);
        domainManager.unregisterDomain(domain);

        assertNull(actual);
    }

    @Test
    void unregisterDomain_nullDomain_throwException() {
        assertThrows(ExtensiveDomainsException.class, () -> {
            domainManager.unregisterDomain(null);
        });
    }

    @Test
    void createDomain_anyUnclaimedChunk_createNewDomain() throws ExtensiveDomainsException {
        PlayerMock player = server.addPlayer();
        Chunk chunk = player.getChunk();
        Domain expected = domainManager.createDomain(chunk);
        UUID uuid = expected.getUUID();
        Domain actual = domainManager.getRegisteredDomain(uuid);

        assertEquals(expected, actual);
    }

    @Test
    void createDomain_nullChunk_throwException() {
        assertThrows(ExtensiveDomainsException.class, () -> {
            domainManager.createDomain(null);
        });
    }

    @Test
    void createDomain_anyClaimedChunk_throwException() throws ExtensiveDomainsException {
        PlayerMock player = server.addPlayer();
        Chunk chunk = player.getChunk();

        domainManager.createDomain(chunk);

        assertThrows(ExtensiveDomainsException.class, () -> {
            domainManager.createDomain(chunk);
        });
    }

    @Test
    void deleteDomain_existingDomain_unregisterDomain() throws ExtensiveDomainsException {
        UUID uuid = UUID.randomUUID();
        Domain domain = new Domain(uuid);
        domainManager.registerDomain(uuid, domain);
        domainManager.deleteDomain(domain);
        Domain actual = domainManager.getRegisteredDomain(uuid);

        assertNull(actual);
    }

    @Test
    void deleteDomain_nullDomain_throwException() {
        assertThrows(ExtensiveDomainsException.class, () -> {
            domainManager.deleteDomain(null);
        });
    }

    @Test
    void claimChunk_existingDomainNoClaimedChunks_claimChunk() throws ExtensiveDomainsException {
        UUID uuid = UUID.randomUUID();
        Domain domain = new Domain(uuid);
        PlayerMock player = server.addPlayer();
        Chunk chunk = player.getChunk();
        domainManager.claimChunk(domain, chunk);
        assertTrue(domainManager.domainHasClaim(domain, chunk));
    }

    @Test
    void claimChunk_nullClaimAndNullDomain_throwException() {
        assertThrows(ExtensiveDomainsException.class, () -> {
            domainManager.claimChunk(null, null);
        });
    }

    @Test
    void unclaimChunk_existingDomain1ClaimedChunk_unclaimChunk() throws ExtensiveDomainsException {
        PlayerMock player = server.addPlayer();
        Chunk chunk = player.getChunk();
        Domain domain = domainManager.createDomain(chunk);
        domainManager.unclaimChunk(domain, chunk);

        assertFalse(domainManager.domainHasClaim(domain, chunk));
    }

    @Test
    void unclaimChunk_nullClaimAndNullDomain_throwException() {
        assertThrows(ExtensiveDomainsException.class, () -> {
            domainManager.unclaimChunk(null, null);
        });
    }
}