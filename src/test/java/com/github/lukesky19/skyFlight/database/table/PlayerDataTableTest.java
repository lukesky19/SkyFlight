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
package com.github.lukesky19.skyFlight.database.table;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;
import com.github.lukesky19.skyFlight.common.AbstractTableTest;
import com.github.lukesky19.skyFlight.player.PlayerData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * This class tests the {@link PlayerDataTable} class.
 * Most code is tested against a live database except for errors.
 */
public class PlayerDataTableTest extends AbstractTableTest {
    // Classes being tested
    private PlayerDataTable livePlayerDataTable;
    private PlayerDataTable playerDataTableWithMockedQueueManager;

    /**
     * Set up the required data for the tests.
     */
    @Override
    @BeforeEach
    public void setup(@NotNull TestInfo testInfo) {
        super.setup(testInfo);

        // Setup table classes
        VersionsTable versionsTable = new VersionsTable(liveQueueManager);
        versionsTable.createTable().join();

        // Setup classes for tests
        livePlayerDataTable = new PlayerDataTable(liveQueueManager, versionsTable);
        playerDataTableWithMockedQueueManager = new PlayerDataTable(mockedQueueManager, versionsTable);
    }

    /**
     * Tests the creation of the table in the database.
     */
    @Test
    public void testCreateTable() {
        // Check that the table was created successfully and didn't error
        livePlayerDataTable.createTable()
                .thenAccept(Assertions::assertNull)
                .exceptionally(ex -> {
                    fail("Future completed exceptionally: " + ex.getMessage());
                    return null;
                })
                .join();
    }

    /**
     * Tests the creation of the table in the database, but an error occurs.
     */
    @Test
    public void testCreateTableError() {
        // When a bulk write transaction is queued, return a failed future
        when(mockedQueueManager.queueBulkWriteTransaction(anyList()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Test Error")));

        // Check that the table creation errored
        playerDataTableWithMockedQueueManager.createTable()
                .thenAccept(v -> fail("Table creation should of failed exceptionally."))
                .exceptionally(ex -> {
                    assertEquals("java.lang.RuntimeException: Test Error", ex.getMessage());
                    return null;
                })
                .join();
    }

    /**
     * Test saving and loading of player data from the database.
     */
    @Test
    public void testSaveLoadPlayerData() {
        // Create a player id
        UUID playerId = UUID.randomUUID();
        // Create Player Data to save
        PlayerData savedPlayerData = new PlayerData(playerId);
        savedPlayerData.setFlightTime(100);

        // Create the table
        livePlayerDataTable.createTable().thenCompose(v1 -> {
            // Save the PlayerData to the database
            return livePlayerDataTable.savePlayerData(playerId, savedPlayerData).thenCompose(v2 -> {
                // Load the PlayerData
                return livePlayerDataTable.loadPlayerData(playerId, new PlayerData(playerId)).thenApply(loadedPlayerData -> {
                    // Test that the PlayerData saved and the PlayerData loaded are the same
                    assertEquals(savedPlayerData, loadedPlayerData);
                    return null;
                });
            });
        }).join();
    }

    /**
     * Test loading player data from the database, but none exists in the database.
     */
    @Test
    public void testLoadPlayerDataNoData() {
        // Create a player id
        UUID playerId = UUID.randomUUID();
        // Create Player Data to save
        PlayerData savedPlayerData = new PlayerData(playerId);

        // Create the table
        livePlayerDataTable.createTable().thenCompose(v1 -> {
            // Save the PlayerData to the database
            return livePlayerDataTable.savePlayerData(playerId, savedPlayerData).thenCompose(v2 -> {
                // Load the PlayerData
                return livePlayerDataTable.loadPlayerData(playerId, new PlayerData(playerId)).thenApply(loadedPlayerData -> {
                    // Test that the PlayerData saved and the PlayerData loaded are the same
                    assertEquals(savedPlayerData, loadedPlayerData);
                    return null;
                });
            });
        }).join();
    }

    /**
     * Test the loading of player data, but an error occurs.
     */
    @Test
    @SuppressWarnings("resource") // The ResultSet here is a mock, so a try-with-resources block is unnecessary.
    public void testLoadPlayerDataError() {
        // Created a mocked ResultSet
        ResultSet resultSetMock = Mockito.mock(ResultSet.class);

        // When the ResultSet is used, throw an SQLException for the test
        try {
            when(resultSetMock.next()).thenThrow(new SQLException("Test Error"));
        } catch (SQLException e) { // Required to make the IDE happy
            throw new RuntimeException(e);
        }

        // When a read transaction is queued, intercept the invocation to replace the existing ResultSet with the mocked one.
        when(mockedQueueManager.queueReadTransaction(Mockito.anyString(), anyList(), Mockito.<Function<ResultSet, PlayerData>>any()))
                .thenAnswer(invocation -> {
                    // Get the function
                    Function<ResultSet, PlayerData> function = invocation.getArgument(2);
                    // Call the function with the mocked ResultSet instead.
                    return CompletableFuture.completedFuture(function.apply(resultSetMock));
                });

        // Create a dummy player id
        UUID playerId = UUID.randomUUID();

        // Ensure that a RunTimeException is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                playerDataTableWithMockedQueueManager.loadPlayerData(playerId, new PlayerData(playerId)).join());

        // Ensure the error message is the same as the one used above
        assertEquals("Test Error", exception.getCause().getMessage());
    }

    /**
     * Test the bulk saving of player data.
     */
    @Test
    public void testSaveMapPlayerData() {
        // Create player ids
        UUID playerId1 = UUID.randomUUID();
        UUID playerId2 = UUID.randomUUID();
        UUID playerId3 = UUID.randomUUID();
        UUID playerId4 = UUID.randomUUID();
        UUID playerId5 = UUID.randomUUID();

        // Create the player data to save to the database
        PlayerData playerData1 = new PlayerData(playerId1);
        PlayerData playerData2 = new PlayerData(playerId2);
        PlayerData playerData3 = new PlayerData(playerId3);
        PlayerData playerData4 = new PlayerData(playerId4);
        PlayerData playerData5 = new PlayerData(playerId5);

        // Create a map of player ids to player data
        Map<UUID, PlayerData> playerDataMap = new HashMap<>();
        playerDataMap.put(playerId1, playerData1);
        playerDataMap.put(playerId2, playerData2);
        playerDataMap.put(playerId3, playerData3);
        playerDataMap.put(playerId4, playerData4);
        playerDataMap.put(playerId5, playerData5);

         // Create the player data table
        livePlayerDataTable.createTable().thenCompose(v6 -> {
            // Save the player data
            return livePlayerDataTable.savePlayerData(playerDataMap).thenCompose(v1 -> {
                // Load the player data for each player id and validate it was saved properly.
                return livePlayerDataTable.loadPlayerData(playerId1, new PlayerData(playerId1)).thenCompose(databasePlayerData1 -> {
                    assertEquals(playerData1, databasePlayerData1);

                    return livePlayerDataTable.loadPlayerData(playerId2, new PlayerData(playerId2)).thenCompose(databasePlayerData2 -> {
                        assertEquals(playerData2, databasePlayerData2);

                        return livePlayerDataTable.loadPlayerData(playerId3, new PlayerData(playerId3)).thenCompose(databasePlayerData3 -> {
                            assertEquals(playerData3, databasePlayerData3);

                            return livePlayerDataTable.loadPlayerData(playerId4, new PlayerData(playerId4)).thenCompose(databasePlayerData4 -> {
                                assertEquals(playerData4, databasePlayerData4);

                                return livePlayerDataTable.loadPlayerData(playerId5, new PlayerData(playerId5)).thenApply(databasePlayerData5 -> {
                                    assertEquals(playerData5, databasePlayerData5);

                                    return null;
                                });
                            });
                        });
                    });
                });
            });
        })
        .exceptionally(ex -> {
            fail("Future completed exceptionally: " + ex.getMessage());
            return null;
        }).join();
    }
}