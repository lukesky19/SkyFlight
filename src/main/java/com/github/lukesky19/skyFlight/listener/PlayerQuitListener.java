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
package com.github.lukesky19.skyFlight.listener;

import com.github.lukesky19.skyFlight.flight.FlightManager;
import com.github.lukesky19.skyFlight.player.PlayerDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * This class listens for when a player quits and disables flight.
 */
public class PlayerQuitListener implements Listener {
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull FlightManager flightManager;

    /**
     * Constructor
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param flightManager A {@link FlightManager} instance.
     */
    public PlayerQuitListener(@NotNull PlayerDataManager playerDataManager, @NotNull FlightManager flightManager) {
        this.playerDataManager = playerDataManager;
        this.flightManager = flightManager;
    }

    /**
     * Listens for when a player quits and disables flight.
     * @param playerQuitEvent A {@link PlayerQuitEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        UUID playerId = player.getUniqueId();
        flightManager.disableFlight(playerQuitEvent.getPlayer(), false);

        playerDataManager.unloadPlayerData(playerId);
    }
}
