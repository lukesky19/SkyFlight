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
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This class listens for when a player joins and enables flight if they can fly.
 */
public class PlayerJoinListener implements Listener {
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull FlightManager flightManager;

    /**
     * Constructor
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param flightManager A {@link FlightManager} instance.
     */
    public PlayerJoinListener(@NotNull PlayerDataManager playerDataManager, @NotNull FlightManager flightManager) {
        this.playerDataManager = playerDataManager;
        this.flightManager = flightManager;
    }

    /**
     * Listens for when a player joins and if they can fly, enables flight.
     * @param playerJoinEvent A {@link PlayerJoinEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();

        playerDataManager.loadPlayerData(player.getUniqueId()).thenAccept(v -> {
            if(flightManager.canFly(player, true)) {
                flightManager.enableFlight(player, true);
            }
        });
    }
}