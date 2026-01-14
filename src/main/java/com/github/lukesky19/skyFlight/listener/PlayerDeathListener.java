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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This class manages listening to when a player dies and disabling their flight.
 */
public class PlayerDeathListener implements Listener {
    private final @NotNull FlightManager flightManager;

    /**
     * Constructor
     * @param flightManager A {@link FlightManager} instance.
     */
    public PlayerDeathListener(@NotNull FlightManager flightManager) {
        this.flightManager = flightManager;
    }

    /**
     * Listens to when a player dies and then disables their flight.
     * When they respawn, if they can fly at the respawn point, their flight will be re-enabled.
     * @param playerDeathEvent A {@link PlayerDeathEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
        flightManager.disableFlight(playerDeathEvent.getPlayer(), true);
    }
}
