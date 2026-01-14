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
package com.github.lukesky19.skyFlight.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * This class stores the player's flight time and if they were last known to be flying.
 */
public class PlayerData {
    private final @NotNull UUID playerId;
    private long flightTime = 0;
    private boolean timedFlight = false;

    /**
     * Constructor
     * @param playerId The {@link UUID} of the player.
     */
    public PlayerData(@NotNull UUID playerId) {
        this.playerId = playerId;
    }

    /**
     * Get the {@link UUID} this player data belongs to.
     * @return The {@link UUID} this player data belongs to.
     */
    public @NotNull UUID getPlayerId() {
        return playerId;
    }

    /**
     * Add the time to the player's flight time.
     * @param time The time to add. Must be greater than 0.
     * @return true if successful, false if not.
     */
    public boolean addFlightTime(long time) {
        if(time <= 0) return false;

        this.flightTime += time;

        return true;
    }

    /**
     * Remove the time from the player's flight time.
     * @param time The time to remove. Must be greater than 0.
     * @return true if successful, false if not.
     */
    public boolean removeFlightTime(long time) {
        if(time <= 0) return false;

        this.flightTime = Math.max(0, this.flightTime - time);

        return true;
    }

    /**
     * Set the player's flight time.
     * @param time The time to set. Must be greater than or equal to 0.
     * @return true if successful, false if not.
     */
    public boolean setFlightTime(long time) {
        if(time < 0) return false;

        this.flightTime = time;

        return true;
    }

    /**
     * Get the player's flight time.
     * @return The player's flight time.
     */
    public long getFlightTime() {
        return flightTime;
    }

    /**
     * Set whether the player is flying with limited time or not.
     * @param isFlying true if flying, false if not.
     */
    public void setTimedFlight(boolean isFlying) {
        this.timedFlight = isFlying;
    }

    /**
     * Was the player last known to be flying with limited time?
     * @return true if flying, false if not.
     */
    public boolean isTimedFlight() {
        return timedFlight;
    }

    /**
     * Are the two objects equal?
     * @param obj The object to compare.
     * @return True if all data is equal, false if not.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof PlayerData comparePlayerData)) return false;

        return this.getPlayerId().equals(comparePlayerData.getPlayerId())
                && this.getFlightTime() == comparePlayerData.getFlightTime()
                && this.isTimedFlight() == comparePlayerData.isTimedFlight();
    }
}
