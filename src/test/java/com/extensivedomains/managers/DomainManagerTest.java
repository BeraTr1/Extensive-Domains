package com.extensivedomains.managers;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.extensivedomains.conditions.domainconditions.DomainCondition;
import com.extensivedomains.objects.domain.actions.DomainAction;
import com.extensivedomains.objects.domain.tier.DomainTier;
import org.bukkit.Chunk;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.extensivedomains.ExtensiveDomains;
import com.extensivedomains.exceptions.ExtensiveDomainsException;
import com.extensivedomains.objects.domain.Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DomainManagerTest {
    public static class MockDomainCondition implements DomainCondition {
        private final boolean meetsCondition;

        public MockDomainCondition(boolean meetsCondition) {
            this.meetsCondition = meetsCondition;
        }

        @Override
        public boolean test(Domain domain) {
            return this.meetsCondition;
        }

        @Override
        public void setArgs(String... args) throws ExtensiveDomainsException {}
    }

    private ServerMock server;
    private ExtensiveDomains plugin;

    private DomainManager domainManager;
    private DomainTierManager domainTierManager;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(ExtensiveDomains.class);
        domainManager = plugin.domainManager;
        domainTierManager = plugin.domainTierManager;
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void registerDomain_existingDomain_registerDomain() throws ExtensiveDomainsException {
        UUID uuid = UUID.randomUUID();
        Domain expected = new Domain(uuid, null);
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
        Domain domain = new Domain(uuid, null);
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
        Domain domain = new Domain(uuid, null);
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
        Domain domain = new Domain(uuid, null);
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

    @Test
    void upgradeDomain_domainMeetsConditions_levelChanges() throws ExtensiveDomainsException {
        List<DomainCondition> domainConditions = new ArrayList<>();
        domainConditions.add(new MockDomainCondition(true));
        DomainTier domainTier = domainTierManager.createDomainTier(0, new ArrayList<>(), new ArrayList<>());
        DomainTier nextDomainTier = domainTierManager.createDomainTier(1, domainConditions, new ArrayList<>());

        UUID uuid = UUID.randomUUID();
        Domain domain = new Domain(uuid, domainTier);
        domainManager.upgradeDomain(domain);

        int expected = nextDomainTier.getLevel();
        int actual = domain.getDomainTier().getLevel();

        assertEquals(expected, actual);
    }

    @Test
    void upgradeDomain_domainDoesNotMeetConditions_throwException() throws ExtensiveDomainsException {
        List<DomainCondition> domainConditions = new ArrayList<>();
        domainConditions.add(new MockDomainCondition(false));
        DomainTier domainTier = domainTierManager.createDomainTier(0, new ArrayList<>(), new ArrayList<>());
        domainTierManager.createDomainTier(1, domainConditions, new ArrayList<>());

        assertThrows(ExtensiveDomainsException.class, () -> {
            UUID uuid = UUID.randomUUID();
            Domain domain = new Domain(uuid, domainTier);
            domainManager.upgradeDomain(domain);
        });
    }

    @Test
    void upgradeDomain_domainHasHighestTier_throwException() throws ExtensiveDomainsException {
        DomainTier domainTier = domainTierManager.createDomainTier(0, new ArrayList<>(), new ArrayList<>());

        assertThrows(ExtensiveDomainsException.class, () -> {
            UUID uuid = UUID.randomUUID();
            Domain domain = new Domain(uuid, domainTier);
            domainManager.upgradeDomain(domain);
        });
    }

    @Test
    void domainCanPerformAction_domainHasAction_true() {
        List<Class<? extends DomainAction>> domainActions = new ArrayList<>();
        domainActions.add(DomainAction.class);
        DomainTier domainTier = new DomainTier(0, new ArrayList<>(), domainActions);

        UUID uuid = UUID.randomUUID();
        Domain domain = new Domain(uuid, domainTier);
        Class<DomainAction> domainActionClass = DomainAction.class;

        assertTrue(domainManager.domainCanPerformAction(domain, domainActionClass));
    }

    @Test
    void domainCanPerformAction_domainDoesNotHaveAction_false() {
        DomainTier domainTier = new DomainTier(0, new ArrayList<>(), new ArrayList<>());

        UUID uuid = UUID.randomUUID();
        Domain domain = new Domain(uuid, domainTier);
        Class<DomainAction> domainActionClass = DomainAction.class;

        assertFalse(domainManager.domainCanPerformAction(domain, domainActionClass));
    }

    @Test
    void domainCanPerformAction_domainHasInheritedAction_true() throws ExtensiveDomainsException {
        List<Class<? extends DomainAction>> domainActions = new ArrayList<>();
        domainActions.add(DomainAction.class);

        DomainTier domainTier1 = new DomainTier(1, new ArrayList<>(), domainActions);
        DomainTier domainTier2 = new DomainTier(2, new ArrayList<>(), new ArrayList<>());
        domainTierManager.registerAllDomainTiers(domainTier1, domainTier2);
        domainTierManager.resolveAllowedActionsInheritance();

        UUID uuid = UUID.randomUUID();
        Domain domain = new Domain(uuid, domainTier2);
        Class<DomainAction> domainActionClass = DomainAction.class;

        assertTrue(domainManager.domainCanPerformAction(domain, domainActionClass));
    }

    @Test
    void domainCanUpgrade_domainMeetsRequirements_true() throws ExtensiveDomainsException {
        List<DomainCondition> domainConditions = new ArrayList<>();
        domainConditions.add(new MockDomainCondition(true));
        DomainTier domainTier = domainTierManager.createDomainTier(0, new ArrayList<>(), new ArrayList<>());
        DomainTier nextDomainTier = domainTierManager.createDomainTier(1, domainConditions, new ArrayList<>());

        UUID uuid = UUID.randomUUID();
        Domain domain = new Domain(uuid, domainTier);

        assertTrue(domainManager.domainCanUpgradeTier(domain, nextDomainTier));
    }

    @Test
    void domainCanUpgrade_domainDoesNotMeetRequirements_true() throws ExtensiveDomainsException {
        List<DomainCondition> domainConditions = new ArrayList<>();
        domainConditions.add(new MockDomainCondition(false));
        DomainTier domainTier = domainTierManager.createDomainTier(0, new ArrayList<>(), new ArrayList<>());
        DomainTier nextDomainTier = domainTierManager.createDomainTier(1, domainConditions, new ArrayList<>());

        UUID uuid = UUID.randomUUID();
        Domain domain = new Domain(uuid, domainTier);

        assertFalse(domainManager.domainCanUpgradeTier(domain, nextDomainTier));
    }

    @Test
    void downgradeDomain_domainHasLowestTier_throwException() throws ExtensiveDomainsException {
        DomainTier domainTier = domainTierManager.createDomainTier(0, new ArrayList<>(), new ArrayList<>());

        assertThrows(ExtensiveDomainsException.class, () -> {
            UUID uuid = UUID.randomUUID();
            Domain domain = new Domain(uuid, domainTier);
            domainManager.downgradeDomain(domain);
        });
    }

    @Test
    void downgradeDomain_domainDoesNotHaveLowestTier_throwException() throws ExtensiveDomainsException {
        DomainTier domainTier = domainTierManager.createDomainTier(1, new ArrayList<>(), new ArrayList<>());
        DomainTier previousDomainTier = domainTierManager.createDomainTier(0, new ArrayList<>(), new ArrayList<>());

        UUID uuid = UUID.randomUUID();
        Domain domain = new Domain(uuid, domainTier);
        domainManager.downgradeDomain(domain);

        int expected = previousDomainTier.getLevel();
        int actual = domain.getDomainTier().getLevel();

        assertEquals(expected, actual);
    }
}