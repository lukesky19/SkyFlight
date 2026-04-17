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

import com.github.lukesky19.skyFlight.player.PlayerData;
import com.github.lukesky19.skyFlight.database.queue.QueueManager;
import com.github.lukesky19.skylib.common.api.database.parameter.Parameter;
import com.github.lukesky19.skylib.common.api.database.parameter.impl.LongParameter;
import com.github.lukesky19.skylib.common.api.database.parameter.impl.UUIDParameter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * This class handles the players table that stores player data.
 */
public class PlayerDataTable {
    private final @NotNull QueueManager queueManager;
    private final @NotNull VersionsTable versionsTable;
    private final @NotNull String tableName = "skyflight_player_data";

    /**
     * Constructor
     * @param queueManager A {@link QueueManager} instance.
     * @param versionsTable A {@link VersionsTable} instance.
     */
    public PlayerDataTable(
            @NotNull QueueManager queueManager,
            @NotNull VersionsTable versionsTable) {
        this.queueManager = queueManager;
        this.versionsTable = versionsTable;
    }

    /**
     * Creates a table to player data.
     * Queues the table creation and index creation sql.
     * @return A {@link CompletableFuture} of type {@link Void}.
     */
    public @NotNull CompletableFuture<Void> createTable() {
        String tableCreationSql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "player_id TEXT PRIMARY KEY NOT NULL UNIQUE, " +
                "flight_time LONG NOT NULL DEFAULT 0, " +
                "last_updated LONG NOT NULL DEFAULT 0)";
        String indexCreationSql = "CREATE INDEX IF NOT EXISTS idx_skyflight_player_data_player_ids ON " + tableName + "(player_id);";

        return queueManager.queueBulkWriteTransaction(List.of(tableCreationSql, indexCreationSql))
                .thenCompose(_ -> versionsTable.updateVersion(tableName, 1));
    }

    /**
     * Loads the player's data from the database.
     * @param playerId The {@link UUID} to load data for.
     * @param playerData The {@link PlayerData} to put data into.
     * @return A {@link CompletableFuture} with {@link PlayerData} when complete. The {@link PlayerData} passed to the method will be updated as well.
     */
    public @NotNull CompletableFuture<@NotNull PlayerData> loadPlayerData(@NotNull UUID playerId, @NotNull PlayerData playerData) {
        String selectSql = "SELECT flight_time FROM " + tableName + " WHERE player_id = ?";
        UUIDParameter uuidParameter = new UUIDParameter(playerId);

        return queueManager.queueReadTransaction(selectSql, List.of(uuidParameter), resultSet -> {
            try {
                if(resultSet.next()) {
                    playerData.setFlightTime(playerData.getFlightTime() + resultSet.getLong("flight_time"));
                }

                return playerData;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Saves the player data for a single player.
     * @param uuid The {@link UUID} of the player.
     * @param playerData The {@link PlayerData} for the player.
     * @return A {@link CompletableFuture} of type {@link Void} when complete.
     */
    public @NotNull CompletableFuture<Void> savePlayerData(@NotNull UUID uuid, @NotNull PlayerData playerData) {
        String updateSql = "INSERT INTO " + tableName + " (" +
                "player_id, " +
                "flight_time, " +
                "last_updated) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (player_id) " +
                "DO UPDATE SET " +
                "flight_time = ?, " +
                "last_updated = ? " +
                "WHERE last_updated <= ?";

        UUIDParameter uuidParameter = new UUIDParameter(uuid);
        LongParameter flightTimeParameter = new LongParameter(playerData.getFlightTime());
        LongParameter timestampParameter = new LongParameter(System.currentTimeMillis());

        List<Parameter<?>> parameters = List.of(
                uuidParameter,
                flightTimeParameter,
                timestampParameter,
                flightTimeParameter,
                timestampParameter,
                timestampParameter);

        return queueManager.queueWriteTransaction(updateSql, parameters).thenRun(() -> {});
    }

    /**
     * Saves all player data to the database.
     * @param playerDataMap A {@link Map} mapping {@link UUID}s to {@link PlayerData}.
     * @return A {@link CompletableFuture} of type {@link List} containing {@link Boolean}s when complete. true if successful, and false if not.
     */
    public @NotNull CompletableFuture<@NotNull List<@NotNull Boolean>> savePlayerData(@NotNull Map<@NotNull UUID, @NotNull PlayerData> playerDataMap) {
        if(playerDataMap.isEmpty()) return CompletableFuture.completedFuture(new ArrayList<>());

        List<List<Parameter<?>>> listOfParametersList = new ArrayList<>();
        String updateSql = "INSERT INTO " + tableName + " (" +
                "player_id, " +
                "flight_time, " +
                "last_updated) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (player_id) " +
                "DO UPDATE SET " +
                "flight_time = ?, " +
                "last_updated = ? " +
                "WHERE last_updated <= ?";

        playerDataMap.forEach((uuid, playerData) -> {
            UUIDParameter uuidParameter = new UUIDParameter(uuid);
            LongParameter flightTimeParameter = new LongParameter(playerData.getFlightTime());
            LongParameter timestampParameter = new LongParameter(System.currentTimeMillis());

            List<Parameter<?>> parameters = List.of(
                    uuidParameter,
                    flightTimeParameter,
                    timestampParameter,
                    flightTimeParameter,
                    timestampParameter,
                    timestampParameter);

            listOfParametersList.add(parameters);
        });

        return queueManager.queueBulkWriteTransaction(updateSql, listOfParametersList).thenApply(list -> {
                List<Boolean> results = new ArrayList<>();

                list.forEach(rowsUpdated -> {
                    if(rowsUpdated > 0) {
                        results.add(true);
                    } else  {
                        results.add(false);
                    }
                });

                return results;
            }
        );
    }
}