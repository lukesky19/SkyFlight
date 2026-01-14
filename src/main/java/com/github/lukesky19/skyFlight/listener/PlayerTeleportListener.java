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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

/**
 * This class listens for when a player teleports and checks if flight should be enabled or disabled.
 */
public class PlayerTeleportListener implements Listener {
    private final @NotNull FlightManager flightManager;
    private final @NotNull BossBarManager bossBarManager;

    /**
     * Constructor
     * @param flightManager A {@link FlightManager} instance.
     * @param bossBarManager A {@link BossBarManager} instance.
     */
    public PlayerTeleportListener(@NotNull FlightManager flightManager, @NotNull BossBarManager bossBarManager) {
        this.flightManager = flightManager;
        this.bossBarManager = bossBarManager;
    }

    /**
     * Listens for when a player teleports and checks if flight should be enabled or disabled.
     * @param playerTeleportEvent A {@link PlayerTeleportEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent playerTeleportEvent) {
        Player player = playerTeleportEvent.getPlayer();
        Location fromLocation = playerTeleportEvent.getFrom();
        Location toLocation = playerTeleportEvent.getTo();

        // Don't change flight on teleports in the same world
        if(fromLocation.getWorld().getName().equals(toLocation.getWorld().getName())) return;

        if(player.getAllowFlight()) {
            if(!flightManager.canFly(player, false)) {
                flightManager.disableFlight(player, false);
            }
        } else {
            if(flightManager.canFly(player, true)) {
                flightManager.enableFlight(player, true);
            } else {
                // Remove any lingering boss bar
                bossBarManager.removeBossBar(player);
            }
        }
    }
}
