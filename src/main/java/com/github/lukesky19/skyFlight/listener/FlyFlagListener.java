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

import com.github.lukesky19.skyFlight.integration.HookManager;
import com.github.lukesky19.skyFlight.integration.hooks.BentoBoxHook;
import com.github.lukesky19.skyFlight.flight.FlightManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.bentobox.bentobox.api.events.flags.FlagProtectionChangeEvent;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.database.objects.Island;

/**
 * Listens for when the flight flag is changed and if disabled, disable any flight as necessary.
 */
public class FlyFlagListener implements Listener {
    private final @NotNull FlightManager flightManager;
    private final @NotNull HookManager hookManager;

    /**
     * Constructor
     * @param flightManager A {@link FlightManager} instance.
     * @param hookManager A {@link HookManager} instance.
     */
    public FlyFlagListener(@NotNull FlightManager flightManager, @NotNull HookManager hookManager) {
        this.flightManager = flightManager;
        this.hookManager = hookManager;
    }

    /**
     * When Island Fly Protection flag is changed, disable any players flight if necessary.
     * @param flagProtectionChangeEvent A {@link FlagProtectionChangeEvent}.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFlagChange(FlagProtectionChangeEvent flagProtectionChangeEvent) {
        // If BentoBox isn't hooked into, return
        BentoBoxHook bentoBoxHook = hookManager.getHook(BentoBoxHook.class);
        if(!bentoBoxHook.isHooked()) return;
        @Nullable Flag flightFlag = bentoBoxHook.getFlightFlag();
        if(flightFlag == null) return;

        // If the flag is not that of the flight flag, return
        if(!flagProtectionChangeEvent.getEditedFlag().equals(flightFlag)) return;

        // Get the island that the flag was changed for
        Island island = flagProtectionChangeEvent.getIsland();

        // For all players on the island, check if they are flying and can no longer fly, then disable their flight with a delay.
        island.getPlayersOnIsland()
                .stream()
                // Filter by players that can fly
                .filter(Player::getAllowFlight)
                // Filter by players that can't fly
                .filter(player -> !flightManager.canFly(player, false))
                // For each player, disable flight immediately or with a 5 second delay
                .forEach(player -> {
                    if(player.isFlying()) {
                        flightManager.disableFlightWithDelay(player, 5);
                    } else {
                        flightManager.disableFlight(player, true);
                    }
                });
    }
}
