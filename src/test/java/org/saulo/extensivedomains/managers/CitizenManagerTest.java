package org.saulo.extensivedomains.managers;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.saulo.extensivedomains.ExtensiveDomains;
import org.saulo.extensivedomains.objects.Citizen;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CitizenManagerTest {
    private ServerMock server;
    private ExtensiveDomains plugin;
    private CitizenManager citizenManager;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(ExtensiveDomains.class);
        citizenManager = plugin.citizenManager;
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void registerCitizen_registeredCitizenWithRandomUUID_registerCitizen() {
        UUID randomUUID = UUID.randomUUID();
        Citizen expected = new Citizen(randomUUID);
        citizenManager.registerCitizen(randomUUID, expected);
        Citizen actual = citizenManager.getRegisteredCitizen(randomUUID);
        assertEquals(expected, actual);
    }

    @Test
    void unregisterCitizen_registeredCitizenWithRandomUUID_unregisterCitizen() {
        UUID uuid = UUID.randomUUID();
        Citizen citizen = new Citizen(uuid);
        citizenManager.registerCitizen(uuid, citizen);
        citizenManager.unregisterCitizen(citizen);
        Citizen actual = citizenManager.getRegisteredCitizen(uuid);
        assertNull(actual);
    }

    @Test
    void getRegisteredCitizen_registeredCitizenWithRandomUUID_returnCitizenWithSameUUID() {
        UUID uuid = UUID.randomUUID();
        Citizen expected = new Citizen(uuid);
        citizenManager.registerCitizen(uuid, expected);
        Citizen actual = citizenManager.getRegisteredCitizen(uuid);
        assertEquals(expected, actual);
    }

    @Test
    void registerNewCitizen_randomUUID_registerNewCitizen() {
        UUID expected = UUID.randomUUID();
        citizenManager.registerNewCitizen(expected);
        UUID actual = citizenManager.getRegisteredCitizen(expected).getUUID();
        assertEquals(expected, actual);
    }
}