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
package com.github.lukesky19.skyFlight.bossbar;

import com.github.lukesky19.skyFlight.SkyFlight;
import com.github.lukesky19.skyFlight.locale.LocaleManager;
import com.github.lukesky19.skyFlight.player.PlayerDataManager;
import com.github.lukesky19.skyFlight.settings.Settings;
import com.github.lukesky19.skyFlight.settings.SettingsManager;
import com.github.lukesky19.skyFlight.player.PlayerData;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class manages the boss bar shown to players when flight is enabled.
 */
public class BossBarManager {
    private final @NotNull ComponentLogger logger;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull Map<UUID, BossBar> activeBossBars = new HashMap<>();

    /**
     * Constructor
     * @param skyFlight A {@link SkyFlight} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public BossBarManager(
            @NotNull SkyFlight skyFlight,
            @NotNull SettingsManager settingsManager,
            @NotNull LocaleManager localeManager,
            @NotNull PlayerDataManager playerDataManager) {
        this.logger = skyFlight.getComponentLogger();
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.playerDataManager = playerDataManager;
    }

    /**
     * Show the boss bar for timed flight.
     * @param player The {@link Player} to show the boss bar.
     */
    public void showTimeBossBar(@NotNull Player player) {
        @Nullable Settings settings = settingsManager.getConfiguration();
        if(settings == null || settings.timedBossBar().bossBarText() == null || settings.timedBossBar().color() == null || settings.timedBossBar().overlay() == null) {
            logger.warn(AdventureUtil.deserialize("Unable to show the timed boss bar to player " + player.getName() + " due to invalid plugin settings."));
            return;
        }

        // Remove the existing boss bar (if any) from being shown to the player
        removeBossBar(player);

        UUID playerId = player.getUniqueId();
        @Nullable PlayerData playerData = playerDataManager.getPlayerData(playerId);
        if(playerData == null) return;

        BossBar bossBar = BossBar.bossBar(
                AdventureUtil.deserialize(settings.timedBossBar().bossBarText(), List.of(Placeholder.parsed("time", localeManager.formatFlightTime(localeManager.getConfiguration().timeFormat(), playerData.getFlightTime())))),
                1,
                settings.timedBossBar().color(),
                settings.timedBossBar().overlay());

        bossBar.addViewer(player);

        activeBossBars.put(playerId, bossBar);
    }

    /**
     * Show the boss bar for when infinite flight.
     * @param player The {@link Player} to show the boss bar.
     */
    public void showInfiniteBossBar(@NotNull Player player) {
        @Nullable Settings settings = settingsManager.getConfiguration();
        if(settings == null || settings.infiniteBossBar().bossBarText() == null || settings.infiniteBossBar().color() == null || settings.infiniteBossBar().overlay() == null) {
            logger.warn(AdventureUtil.deserialize("Unable to show the infinite boss bar to player " + player.getName() + " due to invalid plugin settings."));
            return;
        }

        // Remove the existing boss bar (if any) from being shown to the player
        removeBossBar(player);

        BossBar bossBar = BossBar.bossBar(
                AdventureUtil.deserialize(settings.infiniteBossBar().bossBarText()),
                1,
                settings.infiniteBossBar().color(),
                settings.infiniteBossBar().overlay());

        bossBar.addViewer(player);

        activeBossBars.put(player.getUniqueId(), bossBar);
    }

    /**
     * If the player is flying using timed flight, update the boss bar for the player with the updated flight time.
     * @param player The {@link Player} to update the boss bar for.
     * @return true if updated, false if not.
     */
    public boolean updateBossBar(@NotNull Player player) {
        @Nullable Settings settings = settingsManager.getConfiguration();
        if(settings == null || settings.timedBossBar().bossBarText() == null || settings.timedBossBar().color() == null || settings.timedBossBar().overlay() == null) {
            logger.warn(AdventureUtil.deserialize("Unable to update the timed boss bar for player " + player.getName() + " due to invalid plugin settings."));
            return false;
        }

        UUID playerId = player.getUniqueId();
        @Nullable PlayerData playerData = playerDataManager.getPlayerData(playerId);
        if(playerData == null) {
            logger.warn(AdventureUtil.deserialize("Unable to update the timed boss bar for player " + player.getName() + " due to invalid player data."));
            return false;
        }
        if(!playerData.isTimedFlight()) {
            return false;
        }

        @Nullable BossBar bossBar = activeBossBars.get(playerId);
        if(bossBar == null) {
            logger.warn(AdventureUtil.deserialize("Unable to update the timed boss bar for player " + player.getName() + " due to no boss bar associated with the player."));
            return false;
        }

        bossBar.name(AdventureUtil.deserialize(settings.timedBossBar().bossBarText(),
                List.of(Placeholder.parsed("time", localeManager.formatFlightTime(
                        localeManager.getConfiguration().timeFormat(), playerData.getFlightTime())))));

        return true;
    }

    /**
     * Remove the boss bar currently shown to the player if any.
     * @param player The {@link Player} to remove the boss bar for.
     * @return true if removed, false if not.
     */
    public boolean removeBossBar(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        @Nullable BossBar bossBar = activeBossBars.get(uuid);
        if(bossBar == null) return false;

        bossBar.removeViewer(player);
        activeBossBars.remove(uuid);

        return true;
    }

    /**
     * Set the {@link BossBar} for the {@link BossBar} provided.
     * @param uuid The {@link UUID}
     * @param bossBar The {@link BossBar}
     */
    void setBossBar(@NotNull UUID uuid, @NotNull BossBar bossBar) {
        activeBossBars.put(uuid, bossBar);
    }
}