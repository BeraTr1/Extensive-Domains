package org.saulo.extensivedomains.listeners;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.saulo.extensivedomains.ExtensiveDomains;
import org.saulo.extensivedomains.managers.CitizenManager;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerListenerTest {
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
    public void onPlayerJoin_1NewPlayerJoins_registerNewCitizen() {
        PlayerMock player = server.addPlayer();
        UUID playerUUID = player.getUniqueId();
        boolean playerHasBeenRegistered = citizenManager.getRegisteredCitizen(playerUUID).getUUID() == playerUUID;

        assertTrue(playerHasBeenRegistered);
    }
}