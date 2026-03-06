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
package com.github.lukesky19.skyFlight.api;

import com.github.lukesky19.skyFlight.flight.FlightManager;
import com.github.lukesky19.skyFlight.time.TimeManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * This class allows other plugins to interface with SkyFlight.
 */
public class SkyFlightAPI {
    private final @NotNull FlightManager flightManager;
    private final @NotNull TimeManager timeManager;

    /**
     * Constructor
     * @param flightManager A {@link FlightManager} instance.
     * @param timeManager A {@link TimeManager} instance.
     */
    public SkyFlightAPI(
            @NotNull FlightManager flightManager,
            @NotNull TimeManager timeManager) {
        this.flightManager = flightManager;
        this.timeManager = timeManager;
    }

    /**
     * Is the player allowed to fly? Use {@link #enableFlight(Player, boolean)} if this returns true.
     * @apiNote This automatically handles deciding between infinite flight and timed flight, where infinite flight takes priority.
     * @param player The {@link Player} to check.
     * @param message Should detailed messages be sent to the player?
     * @return true if they can fly, false if not.
     */
    public boolean canFly(@NotNull Player player, boolean message) {
        return flightManager.canFly(player, message);
    }

    /**
     * Is the player allowed to use infinite fly? Use {@link #enableInfiniteFlight(Player, boolean)} if this returns true.
     * @param player The {@link Player} to check.
     * @param message Should detailed messages be sent to the player?
     * @return true if they can fly, false if not.
     */
    public boolean canFlyInfinite(@NotNull Player player, boolean message) {
        return flightManager.canFlyInfinite(player, message);
    }

    /**
     * Is the player allowed to use timed fly? Use {@link #enableTimedFlight(Player, boolean)} if this returns true.
     * @param player The {@link Player} to check.
     * @param message Should detailed messages be sent to the player?
     * @return true if they can fly, false if not.
     */
    public boolean canFlyTimed(@NotNull Player player, boolean message) {
        return flightManager.canFlyTimed(player, message);
    }

    /**
     * Enable flight for the player.
     * @apiNote This automatically handles deciding between infinite flight and timed flight, where infinite flight takes priority.
     * @param player The {@link Player}.
     * @param message Should messages be sent to the player?
     * @return true if flight was enabled, false if not.
     */
    public boolean enableFlight(@NotNull Player player, boolean message) {
        return flightManager.enableFlight(player, message);
    }

    /**
     * Enable infinite flight for the player.
     * @param player The {@link Player}.
     * @param message Should messages be sent to the player?
     * @return true if flight was enabled, false if not.
     */
    public boolean enableInfiniteFlight(@NotNull Player player, boolean message) {
        return flightManager.enableInfiniteFlight(player, message);
    }

    /**
     * Enable timed flight for the player.
     * @param player The {@link Player}.
     * @param message Should messages be sent to the player?
     * @return true if flight was enabled, false if not.
     */
    public boolean enableTimedFlight(@NotNull Player player, boolean message) {
        return flightManager.enableTimedFlight(player, message);
    }

    /**
     * Disable flight for the player after the seconds provided.
     * @apiNote This will check if the player can fly before actually disabling flight.
     * @param player The {@link Player}.
     * @param delaySeconds The delay in seconds.
     * @return A {@link CompletableFuture} of type {@link Boolean} when complete. The boolean will be true if successful, false if not.
     */
    public @NotNull CompletableFuture<Boolean> disableFlightWithDelay(@NotNull Player player, int delaySeconds) {
        return flightManager.disableFlightWithDelay(player, delaySeconds);
    }

    /**
     * Disable flight for the player.
     * @param player The {@link Player}.
     * @param message Should the message that flight was disabled be sent to the player?
     * @return true if flight was disabled, false if not.
     */
    public boolean disableFlight(@NotNull Player player, boolean message) {
        return flightManager.disableFlight(player, message);
    }

    /**
     * Add flight time to the player.
     * @param player The {@link Player}.
     * @param time The time to add.
     * @return true if successful, false if not.
     */
    public boolean addFlightTime(@NotNull Player player, long time) {
        return timeManager.addFlightTime(player, time);
    }

    /**
     * Remove flight time from the player.
     * @param player The {@link Player}.
     * @param time The time to remove.
     * @return true if successful, false if not.
     */
    public boolean removeFlightTime(@NotNull Player player, long time) {
        return timeManager.removeFlightTime(player, time);
    }

    /**
     * Set the player's flight time.
     * @param player The {@link Player}.
     * @param time The time to remove.
     * @return true if successful, false if not.
     */
    public boolean setFlightTime(@NotNull Player player, long time) {
        return timeManager.setFlightTime(player, time);
    }

    /**
     * Get the amount of flight time the player has.
     * @param player The {@link Player}
     * @return The flight time. Returns 0 if no player data is loaded.
     */
    public long getFlightTime(@NotNull Player player) {
        return timeManager.getFlightTime(player);
    }
}