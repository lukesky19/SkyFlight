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

import com.github.lukesky19.skyFlight.bossbar.BossBarManager;
import com.github.lukesky19.skyFlight.flight.FlightManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This class listens for when a player toggles their flight and checks if they are not allowed to fly, then disabling their flight.
 */
public class PlayerToggleFlightListener implements Listener {
    private final @NotNull FlightManager flightManager;
    private final @NotNull BossBarManager bossBarManager;

    /**
     * Constructor
     * @param flightManager A {@link FlightManager} instance.
     * @param bossBarManager A {@link BossBarManager} instance.
     */
    public PlayerToggleFlightListener(
            @NotNull FlightManager flightManager,
            @NotNull BossBarManager bossBarManager) {
        this.flightManager = flightManager;
        this.bossBarManager = bossBarManager;
    }

    /**
     * Listens for when the player toggles their flight and disables their flight if they are not allowed to fly.
     * @param playerToggleFlightEvent A {@link PlayerToggleFlightEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent playerToggleFlightEvent) {
        Player player = playerToggleFlightEvent.getPlayer();

        if(player.getAllowFlight()) {
            if(player.isFlying()) {
                if(!flightManager.canFly(player, false)) {
                    flightManager.disableFlightWithDelay(player, 5);
                }
            } else {
                if(!flightManager.canFly(player, false)) {
                    flightManager.disableFlight(player, true);
                }
            }
        } else {
            // Remove any lingering boss bar
            bossBarManager.removeBossBar(player);
        }
    }
}
