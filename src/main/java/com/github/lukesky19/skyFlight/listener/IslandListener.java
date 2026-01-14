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

import com.github.lukesky19.skyFlight.SkyFlight;
import com.github.lukesky19.skyFlight.bossbar.BossBarManager;
import com.github.lukesky19.skyFlight.flight.FlightManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.bentobox.bentobox.api.events.island.IslandEnterEvent;
import world.bentobox.bentobox.api.events.island.IslandExitEvent;

/**
 * This class listens to when a player enters or exits an island and then checks if flight needs to be enabled or disabled.
 */
public class IslandListener implements Listener {
    private final @NotNull SkyFlight skyFlight;
    private final @NotNull FlightManager flightManager;
    private final @NotNull BossBarManager bossBarManager;

    /**
     * Constructor
     * @param skyFlight A {@link SkyFlight} instance.
     * @param flightManager A {@link FlightManager} instance.
     * @param bossBarManager A {@link BossBarManager} instance.
     */
    public IslandListener(@NotNull SkyFlight skyFlight, @NotNull FlightManager flightManager, @NotNull BossBarManager bossBarManager) {
        this.skyFlight = skyFlight;
        this.flightManager = flightManager;
        this.bossBarManager = bossBarManager;
    }

    /**
     * Listens for when a player enters an island and checks if flight should be enabled if not already enabled.
     * @param islandEnterEvent An {@link IslandEnterEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEnterIsland(IslandEnterEvent islandEnterEvent) {
        @Nullable Player player = skyFlight.getServer().getPlayer(islandEnterEvent.getPlayerUUID());
        if(player == null) return;

        // Wait until player is on the Island
        skyFlight.getServer().getScheduler().runTaskLater(skyFlight, () -> {
            if(!player.isOnline() || !player.isConnected()) return;
            if(player.getAllowFlight()) return;

            if(flightManager.canFly(player, true)) {
                flightManager.enableFlight(player, true);
            }
        }, 1L);
    }

    /**
     * Listens for when a player exits an island and checks if flight should be disabled if not already disabled.
     * @param islandExitEvent An {@link IslandExitEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onExitIsland(IslandExitEvent islandExitEvent) {
        @Nullable Player player = skyFlight.getServer().getPlayer(islandExitEvent.getPlayerUUID());
        if(player == null) return;

        // Wait until player has left the Island
        skyFlight.getServer().getScheduler().runTaskLater(skyFlight, () -> {
            if(!player.isOnline() || !player.isConnected()) return;
            if(!player.getAllowFlight()) {
                // Remove any lingering boss bar
                bossBarManager.removeBossBar(player);
                return;
            }

            if(!flightManager.canFly(player, true)) {
                if(player.isFlying()) {
                    flightManager.disableFlightWithDelay(player, 5);
                } else {
                    flightManager.disableFlight(player, true);
                }
            }
        }, 1L);
    }
}
