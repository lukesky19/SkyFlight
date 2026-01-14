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
package com.github.lukesky19.skyFlight.settings;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The plugin's settings configuration.
 * @param configVersion The config version of the file.
 * @param locale The locale file to use.
 * @param disabledWorlds Worlds flight is disabled in.
 * @param infiniteBossBar The boss bar displayed for infinite flight.
 * @param timedBossBar The boss bar displayed for timed flight.
 */
@ConfigSerializable
public record Settings(
        @Nullable String configVersion,
        @Nullable String locale,
        @NotNull List<String> disabledWorlds,
        @NotNull BossBarConfig infiniteBossBar,
        @NotNull BossBarConfig timedBossBar) {
    /**
     * The config for the boss bar to show flight status.
     * @param bossBarText The boss bar text to show.
     * @param color The color of the boss bar.
     * @param overlay The overlay of the boss bar.
     */
    @ConfigSerializable
    public record BossBarConfig(
            @Nullable String bossBarText,
            @Nullable BossBar.Color color,
            @Nullable BossBar.Overlay overlay) {}
}
