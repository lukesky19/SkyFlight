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
package com.github.lukesky19.skyFlight.time;

import com.github.lukesky19.skyFlight.SkyFlight;
import com.github.lukesky19.skyFlight.bossbar.BossBarManager;
import com.github.lukesky19.skyFlight.player.PlayerData;
import com.github.lukesky19.skyFlight.player.PlayerDataManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class manages the updating and retrieval of player flight time.
 */
public class TimeManager {
    private final @NotNull ComponentLogger logger;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull BossBarManager bossBarManager;

    /**
     * Constructor
     * @param skyFlight A {@link SkyFlight} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param bossBarManager A {@link BossBarManager} instance.
     */
    public TimeManager(
            @NotNull SkyFlight skyFlight,
            @NotNull PlayerDataManager playerDataManager,
            @NotNull BossBarManager bossBarManager) {
        this.logger = skyFlight.getComponentLogger();
        this.playerDataManager = playerDataManager;
        this.bossBarManager = bossBarManager;
    }

    /**
     * Add flight time to the player.
     * @param player The {@link Player}.
     * @param time The time to add.
     * @return true if successful, false if not.
     */
    public boolean addFlightTime(@NotNull Player player, long time) {
        // Check if the player has player data
        @Nullable PlayerData playerData = playerDataManager.getPlayerData(player.getUniqueId());
        if(playerData == null) {
            logger.warn(AdventureUtil.deserialize("Player " + player.getName() + " has no player data loaded."));
            return false;
        }

        boolean timeResult = playerData.addFlightTime(time);

        bossBarManager.updateBossBar(player);

        return timeResult;
    }

    /**
     * Remove flight time from the player.
     * @param player The {@link Player}.
     * @param time The time to remove.
     * @return true if successful, false if not.
     */
    public boolean removeFlightTime(@NotNull Player player, long time) {
        // Check if the player has player data
        @Nullable PlayerData playerData = playerDataManager.getPlayerData(player.getUniqueId());
        if(playerData == null) {
            logger.warn(AdventureUtil.deserialize("Player " + player.getName() + " has no player data loaded."));
            return false;
        }

        boolean timeResult = playerData.removeFlightTime(time);

        bossBarManager.updateBossBar(player);

        return timeResult;
    }

    /**
     * Set the player's flight time.
     * @param player The {@link Player}.
     * @param time The time to remove.
     * @return true if successful, false if not.
     */
    public boolean setFlightTime(@NotNull Player player, long time) {
        // Check if the player has player data
        @Nullable PlayerData playerData = playerDataManager.getPlayerData(player.getUniqueId());
        if(playerData == null) {
            logger.warn(AdventureUtil.deserialize("Player " + player.getName() + " has no player data loaded."));
            return false;
        }

        boolean timeResult = playerData.setFlightTime(time);

        bossBarManager.updateBossBar(player);

        return timeResult;
    }

    /**
     * Get the amount of flight time the player has.
     * @param player The {@link Player}
     * @return The flight time. Returns 0 if no player data is loaded.
     */
    public long getFlightTime(@NotNull Player player) {
        // Check if the player has player data
        @Nullable PlayerData playerData = playerDataManager.getPlayerData(player.getUniqueId());
        if(playerData == null) {
            logger.warn(AdventureUtil.deserialize("Player " + player.getName() + " has no player data loaded."));
            return 0;
        }

        return playerData.getFlightTime();
    }
}
