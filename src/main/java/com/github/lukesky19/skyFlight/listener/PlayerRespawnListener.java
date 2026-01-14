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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This class manages listening to when a player respawns and enabling their flight if they can fly.
 */
public class PlayerRespawnListener implements Listener {
    private final @NotNull FlightManager flightManager;

    /**
     * Constructor
     * @param flightManager A {@link FlightManager} instance.
     */
    public PlayerRespawnListener(@NotNull FlightManager flightManager) {
        this.flightManager = flightManager;
    }

    /**
     * Listens to when a player respawns and then enables their flight if they can fly.
     * @param playerRespawnEvent A {@link PlayerRespawnEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent playerRespawnEvent) {
        Player player = playerRespawnEvent.getPlayer();

        if(flightManager.canFly(player, true)) {
            flightManager.enableFlight(player, true);
        }
    }
}
