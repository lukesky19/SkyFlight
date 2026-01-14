/*
    SkyFlight adds the ability for players to fly.
    Copyright (C) 2026 lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.lukesky19.skyFlight.player;

import com.github.lukesky19.skyFlight.database.DatabaseManager;
import com.github.lukesky19.skyFlight.database.table.PlayerDataTable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * This class tests {@link PlayerDataManager}.
 */
@ExtendWith(MockitoExtension.class)
public class PlayerDataManagerTest {
    /**
     * Test {@link PlayerDataManager#setPlayerData(UUID, PlayerData, boolean)}.
     */
    @Test
    public void testSetPlayerData() {
        ComponentLogger logger = mock(ComponentLogger.class);
        DatabaseManager databaseManager = mock(DatabaseManager.class);
        PlayerDataManager playerDataManager = new PlayerDataManager(logger, databaseManager);

        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);

        playerDataManager.setPlayerData(playerId, playerData, true);
    }

    /**
     * Test {@link PlayerDataManager#setPlayerData(UUID, PlayerData, boolean)}, but data already exists for the player.
     */
    @Test
    public void testSetPlayerDataPreventOverwrite() {
        ComponentLogger logger = mock(ComponentLogger.class);
        DatabaseManager databaseManager = mock(DatabaseManager.class);
        PlayerDataManager playerDataManager = new PlayerDataManager(logger, databaseManager);

        UUID playerId1 = UUID.randomUUID();
        PlayerData playerData1 = new PlayerData(playerId1);
        PlayerData playerData2 = new PlayerData(playerId1);

        playerDataManager.setPlayerData(playerId1, playerData1, false);
        playerDataManager.setPlayerData(playerId1, playerData2, false);

        assertSame(playerData1, playerDataManager.getPlayerData(playerId1));
    }

    /**
     * Test {@link PlayerDataManager#setPlayerData(UUID, PlayerData, boolean)} where data is overwritten.
     */
    @Test
    public void testSetPlayerDataOverwrite() {
        ComponentLogger logger = mock(ComponentLogger.class);
        DatabaseManager databaseManager = mock(DatabaseManager.class);
        PlayerDataManager playerDataManager = new PlayerDataManager(logger, databaseManager);

        UUID playerId = UUID.randomUUID();
        PlayerData playerData1 = new PlayerData(playerId);
        PlayerData playerData2 = new PlayerData(playerId);

        playerDataManager.setPlayerData(playerId, playerData1, true);
        playerDataManager.setPlayerData(playerId, playerData2, true);

        assertSame(playerData2, playerDataManager.getPlayerData(playerId));
    }

    /**
     * Test {@link PlayerDataManager#loadPlayerData(UUID)}.
     */
    @Test
    public void testLoadPlayerData() {
        ComponentLogger logger = mock(ComponentLogger.class);
        DatabaseManager databaseManager = mock(DatabaseManager.class);
        PlayerDataTable playerDataTable = mock(PlayerDataTable.class);
        when(databaseManager.getPlayerDataTable()).thenReturn(playerDataTable);
        PlayerDataManager playerDataManager = new PlayerDataManager(logger, databaseManager);

        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);

        when(playerDataTable.loadPlayerData(playerId, new PlayerData(playerId))).thenReturn(CompletableFuture.completedFuture(playerData));

        CompletableFuture<Void> future = playerDataManager.loadPlayerData(playerId);
        future.join();
        assertTrue(future.isDone());
        assertSame(playerData, playerDataManager.getPlayerData(playerId));
    }

    /**
     * Test {@link PlayerDataManager#loadPlayerData(UUID)}, but an exception occurs.
     */
    @Test
    public void testLoadPlayerDataException() {
        ComponentLogger logger = mock(ComponentLogger.class);
        DatabaseManager databaseManager = mock(DatabaseManager.class);
        PlayerDataTable playerDataTable = mock(PlayerDataTable.class);
        when(databaseManager.getPlayerDataTable()).thenReturn(playerDataTable);
        PlayerDataManager playerDataManager = new PlayerDataManager(logger, databaseManager);

        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);

        when(playerDataTable.loadPlayerData(playerId, new PlayerData(playerId))).thenReturn(CompletableFuture.failedFuture(new RuntimeException("Error")));

        CompletableFuture<Void> future = playerDataManager.loadPlayerData(playerId);
        future.join();
        verify(logger).error(any(Component.class));
    }

    /**
     * Test {@link PlayerDataManager#unloadPlayerData(UUID)}.
     */
    @Test
    public void testUnloadPlayerData() {
        ComponentLogger logger = mock(ComponentLogger.class);
        DatabaseManager databaseManager = mock(DatabaseManager.class);
        PlayerDataTable playerDataTable = mock(PlayerDataTable.class);
        when(databaseManager.getPlayerDataTable()).thenReturn(playerDataTable);
        PlayerDataManager playerDataManager = new PlayerDataManager(logger, databaseManager);

        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        playerDataManager.setPlayerData(playerId, playerData, true);
        when(playerDataTable.savePlayerData(playerId, playerData)).thenReturn(CompletableFuture.completedFuture(null));

        playerDataManager.unloadPlayerData(playerId);

        verify(logger, never()).warn(any(Component.class));
        verify(logger, never()).error(any(Component.class));
    }

    /**
     * Test {@link PlayerDataManager#unloadPlayerData(UUID)}, but no player data is loaded.
     */
    @Test
    public void testUnloadPlayerDataNoPlayerData() {
        ComponentLogger logger = mock(ComponentLogger.class);
        DatabaseManager databaseManager = mock(DatabaseManager.class);
        PlayerDataManager playerDataManager = new PlayerDataManager(logger, databaseManager);

        playerDataManager.unloadPlayerData(UUID.randomUUID());

        verify(logger).warn(any(Component.class));
        verify(logger, never()).error(any(Component.class));
    }

    /**
     * Test {@link PlayerDataManager#unloadPlayerData(UUID)}, but an exception occurs while saving player data.
     */
    @Test
    public void testUnloadPlayerDataException() {
        ComponentLogger logger = mock(ComponentLogger.class);
        DatabaseManager databaseManager = mock(DatabaseManager.class);
        PlayerDataTable playerDataTable = mock(PlayerDataTable.class);
        when(databaseManager.getPlayerDataTable()).thenReturn(playerDataTable);
        PlayerDataManager playerDataManager = new PlayerDataManager(logger, databaseManager);

        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        playerDataManager.setPlayerData(playerId, playerData, true);
        when(playerDataTable.savePlayerData(playerId, playerData)).thenReturn(CompletableFuture.failedFuture(new RuntimeException("Error")));

        playerDataManager.unloadPlayerData(playerId);

        verify(logger, never()).warn(any(Component.class));
        verify(logger).error(any(Component.class));
    }

    /**
     * Test {@link PlayerDataManager#savePlayerData()} for bulk saving of player data.
     */
    @Test
    public void testSavePlayerData() throws ExecutionException, InterruptedException {
        ComponentLogger logger = mock(ComponentLogger.class);
        DatabaseManager databaseManager = mock(DatabaseManager.class);
        PlayerDataTable playerDataTable = mock(PlayerDataTable.class);
        when(databaseManager.getPlayerDataTable()).thenReturn(playerDataTable);
        when(playerDataTable.savePlayerData(anyMap())).thenReturn(CompletableFuture.completedFuture(List.of(true, true, true)));
        PlayerDataManager playerDataManager = new PlayerDataManager(logger, databaseManager);

        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        playerDataManager.setPlayerData(playerId, playerData, true);

        CompletableFuture<List<Boolean>> future = playerDataManager.savePlayerData();
        future.join();
        assertTrue(future.isDone());
        assertTrue(future.get().stream().allMatch(result -> true));
    }

    /**
     * Test {@link PlayerDataManager#clearPlayerData()}.
     */
    @Test
    public void testClearPlayerData() {
        ComponentLogger logger = mock(ComponentLogger.class);
        DatabaseManager databaseManager = mock(DatabaseManager.class);
        PlayerDataManager playerDataManager = new PlayerDataManager(logger, databaseManager);

        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        playerDataManager.setPlayerData(playerId, playerData, true);

        assertEquals(1, playerDataManager.getPlayerData().size());

        playerDataManager.clearPlayerData();

        assertEquals(0, playerDataManager.getPlayerData().size());
    }
}