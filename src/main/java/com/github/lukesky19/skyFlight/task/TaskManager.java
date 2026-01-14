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
package com.github.lukesky19.skyFlight.task;

import com.github.lukesky19.skyFlight.SkyFlight;
import com.github.lukesky19.skyFlight.locale.LocaleManager;
import com.github.lukesky19.skyFlight.flight.FlightManager;
import com.github.lukesky19.skyFlight.time.TimeManager;
import com.github.lukesky19.skyFlight.task.tasks.FlightTimeTask;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class manages all the plugin's tasks.
 */
public class TaskManager {
    private final @NotNull SkyFlight skyFlight;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull FlightManager flightManager;
    private final @NotNull TimeManager timeManager;

    private @Nullable BukkitTask flightTimeTask;

    /**
     * Constructor
     * @param skyFlight A {@link SkyFlight} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param flightManager A {@link FlightManager} instance.
     * @param timeManager A {@link TimeManager} instance.
     */
    public TaskManager(
            @NotNull SkyFlight skyFlight,
            @NotNull LocaleManager localeManager,
            @NotNull FlightManager flightManager,
            @NotNull TimeManager timeManager) {
        this.skyFlight = skyFlight;
        this.localeManager = localeManager;
        this.flightManager = flightManager;
        this.timeManager = timeManager;
    }

    /**
     * Start all tasks.
     */
    public void startTasks() {
        stopTasks();

        flightTimeTask = new FlightTimeTask(skyFlight, localeManager, flightManager, timeManager).runTaskTimer(skyFlight, 20L, 20L);
    }

    /**
     * Stop all tasks.
     */
    public void stopTasks() {
        if(flightTimeTask == null) return;

        if(!flightTimeTask.isCancelled()) {
            flightTimeTask.cancel();
        }

        flightTimeTask = null;
    }
}
