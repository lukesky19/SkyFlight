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
package com.github.lukesky19.skyFlight.locale;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This record contains the locale configuration for the plugin's messages.
 * @param configVersion The config version.
 * @param prefix The plugin's prefix.
 * @param reload The message sent when the plugin is reloaded.
 * @param help The plugin's help messages.
 * @param info The info command's messages.
 * @param invalidPluginSettings The message sent to the player when the plugin's settings are invalid.
 * @param invalidPlayerData The message sent to the player when their player data is invalid.
 * @param flightNoPermission The message sent to the player when they don't have permission to fly.
 * @param flightWorldNotAllowed The message sent to the player when they can't fly in the world they are in.
 * @param flightIslandNotAllowed The message sent to the player when they are not allowed to fly on the island.
 * @param flightOutsideIslandNotAllowed The message sent to the player when they are not allowed to fly outside of islands.
 * @param flightWorldGuardNotAllowed The message sent to the player when they are not allowed to fly in the WorldGuard region they are in.
 * @param flightNoFlightTime The message sent to the player when they are not allowed to use timed flight due to no flight time.
 * @param flightEnabled The message sent to the player when flight is enabled.
 * @param flightDisabled The message sent to the player when flight is disabled.
 * @param flightDisabledDelay The message sent to the player when flight will be disabled after a delay.
 * @param flightNotDisabled The message sent to the player when flight wasn't disabled after the delay.
 * @param flightTimeWarning The message sent to the player when their flight time is running low.
 * @param flightTimeExhausted The message sent to the player when their flight time is exhausted.
 * @param commandPlayerOnly The message sent to a command sender when a command is player only.
 * @param timeInvalid The message sent to a command sender when a time argument is invalid.
 * @param flightTimeUpdated The message sent to a player when their flight time is updated.
 * @param playerFlightTimeUpdated The message sent to the command sender that the player's flight time was updated.
 * @param playerFlightTimeUpdateFailed The message sent to the command sender that updating the player's flight time failed.
 * @param flightTime The message sent to the player to display their flight time.
 * @param playerFlightTime The message sent to the command sender to display a player's flight time.
 * @param timeFormat The {@link TimeFormat} to use for any {@literal <time>} placeholders.
 */
@ConfigSerializable
public record Locale(
        String configVersion,
        String prefix,
        String reload,
        @NotNull List<String> help,
        @NotNull List<String> info,
        String invalidPluginSettings,
        String invalidPlayerData,
        String flightNoPermission,
        String flightWorldNotAllowed,
        String flightIslandNotAllowed,
        String flightOutsideIslandNotAllowed,
        String flightWorldGuardNotAllowed,
        String flightNoFlightTime,
        String flightEnabled,
        String flightDisabled,
        String flightDisabledDelay,
        String flightNotDisabled,
        String flightTimeWarning,
        String flightTimeExhausted,
        String commandPlayerOnly,
        String timeInvalid,
        String flightTimeUpdated,
        String playerFlightTimeUpdated,
        String playerFlightTimeUpdateFailed,
        String flightTime,
        String playerFlightTime,
        TimeFormat timeFormat) {
    /**
     * The record containing the data necessary to format a {@literal <time>} placeholder.
     * @param prefix The text to display before the first time unit.
     * @param years The text to display when the player's time enters years.
     * @param months The text to display when the player's time enters months.
     * @param weeks The text to display when the player's time enters weeks.
     * @param days The text to display when the player's time enters days.
     * @param hours The text to display when the player's time enters hours.
     * @param minutes The text to display when the player's time enters minutes.
     * @param seconds The text to display when the player's time enters seconds.
     * @param suffix The text to display after the last time unit.
     */
    @ConfigSerializable
    public record TimeFormat(
            String prefix,
            String years,
            String months,
            String weeks,
            String days,
            String hours,
            String minutes,
            String seconds,
            String suffix) {}
}
