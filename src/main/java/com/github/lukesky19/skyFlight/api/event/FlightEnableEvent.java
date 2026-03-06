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
package com.github.lukesky19.skyFlight.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

/**
 * This event is called before flight is enabled and can be cancelled.
 */
public class FlightEnableEvent extends Event implements Cancellable {
    private static final @NonNull HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;
    private final @NonNull Player player;

    /**
     * Constructor
     * @param player The player having flight enabled.
     */
    public FlightEnableEvent(@NonNull Player player) {
        this.player = player;
    }

    /**
     * Get the {@link Player} having flight enabled.
     * @return The {@link Player} having flight enabled.
     */
    public @NonNull Player getPlayer() {
        return player;
    }

    /**
     * Get the {@link HandlerList} for this event.
     * @return A {@link HandlerList}.
     */
    public static @NonNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get the {@link HandlerList} for this event.
     * @return A {@link HandlerList}.
     */
    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Checks if the event is cancelled.
     * @return true if cancelled, otherwise false.
     */
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    /**
     * Set if this event should be cancelled.
     * @param isCancelled {@code true} if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}