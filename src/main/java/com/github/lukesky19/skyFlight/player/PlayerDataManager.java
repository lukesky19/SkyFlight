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
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * This class manages all player data.
 */
public class PlayerDataManager {
    private final @NotNull ComponentLogger logger;
    private final @NotNull DatabaseManager databaseManager;
    private final @NotNull Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    /**
     * Constructor
     * @param logger A {@link ComponentLogger} instance.
     * @param databaseManager A {@link DatabaseManager} instance.
     */
    public PlayerDataManager(@NotNull ComponentLogger logger, @NotNull DatabaseManager databaseManager) {
        this.logger = logger;
        this.databaseManager = databaseManager;
    }

    /**
     * Get the {@link PlayerData} for the given {@link UUID}.
     * @param playerId The {@link UUID} of the player.
     * @return The {@link PlayerData} or null.
     */
    public @Nullable PlayerData getPlayerData(@NotNull UUID playerId) {
        return playerDataMap.get(playerId);
    }

    /**
     * Get all player data.
     * @return All player data.
     */
    public @NotNull Collection<PlayerData> getPlayerData() {
        return playerDataMap.values();
    }

    /**
     * Store the player data.
     * @param playerId The player's {@link UUID}.
     * @param playerData The {@link PlayerData}.
     * @param overwrite Should any existing data be overwritten?
     */
    public void setPlayerData(@NotNull UUID playerId, @NotNull PlayerData playerData, boolean overwrite) {
        if(playerDataMap.containsKey(playerId) && !overwrite) return;

        playerDataMap.put(playerId, playerData);
    }

    /**
     * Loads player data from the database.
     * @param uuid The {@link UUID} of the player to load data for.
     * @return A {@link CompletableFuture} of type {@link Void} when complete.
     */
    public @NotNull CompletableFuture<Void> loadPlayerData(@NotNull UUID uuid) {
        PlayerDataTable playerDataTable = databaseManager.getPlayerDataTable();
        PlayerData playerData = playerDataMap.getOrDefault(uuid, new PlayerData(uuid));

        return playerDataTable.loadPlayerData(uuid, playerData)
                .thenAccept(updatedPlayerData -> {
                    // Store the player data
                    playerDataMap.put(uuid, updatedPlayerData);
                })
                .exceptionally(ex -> {
                    // Log an error if an exception occurred during loading.
                    logger.error(AdventureUtil.deserialize("Failed to load player data from the database."));
                    return null;
                });
    }

    /**
     * Saves the {@link PlayerData} for the player with the provided {@link UUID} to the database and then unloads it from memory.
     * @param uuid The {@link UUID} of the player.
     */
    public void unloadPlayerData(@NotNull UUID uuid) {
        @Nullable PlayerData playerData = getPlayerData(uuid);
        if(playerData == null) {
            logger.warn(AdventureUtil.deserialize("No player data to save and unload."));
            return;
        }

        databaseManager.getPlayerDataTable().savePlayerData(uuid, playerData)
                .thenAccept(v -> playerDataMap.remove(uuid))
                .exceptionally(ex -> {
                    playerDataMap.remove(uuid);
                    logger.error(AdventureUtil.deserialize("Failed to save player data to the database."));
                    return null;
                });
    }

    /**
     * Saves all loaded player data to the database.
     * @return A {@link CompletableFuture} containing a {@link List} of type {@link Boolean}.
     * If any player data fails to save, the list will contain a false result, otherwise true.
     */
    public @NotNull CompletableFuture<@NotNull List<@NotNull Boolean>> savePlayerData() {
        PlayerDataTable playerDataTable = databaseManager.getPlayerDataTable();
        return playerDataTable.savePlayerData(playerDataMap);
    }

    /**
     * Clears any player data stored in memory.
     */
    public void clearPlayerData() {
        playerDataMap.clear();
    }
}