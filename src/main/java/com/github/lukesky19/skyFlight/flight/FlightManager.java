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
package com.github.lukesky19.skyFlight.flight;

import com.github.lukesky19.skyFlight.SkyFlight;
import com.github.lukesky19.skyFlight.api.event.FlightEnableEvent;
import com.github.lukesky19.skyFlight.bossbar.BossBarManager;
import com.github.lukesky19.skyFlight.locale.Locale;
import com.github.lukesky19.skyFlight.locale.LocaleManager;
import com.github.lukesky19.skyFlight.player.PlayerDataManager;
import com.github.lukesky19.skyFlight.settings.Settings;
import com.github.lukesky19.skyFlight.settings.SettingsManager;
import com.github.lukesky19.skyFlight.player.PlayerData;
import com.github.lukesky19.skyFlight.integration.HookManager;
import com.github.lukesky19.skyFlight.integration.hooks.BentoBoxHook;
import com.github.lukesky19.skyFlight.integration.hooks.WorldGuardHook;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.bentobox.bentobox.database.objects.Island;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * This class manages the checking, enabling, and disabling of flight.
 */
public class FlightManager {
    private final @NotNull SkyFlight skyFlight;
    private final @NotNull ComponentLogger logger;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull BossBarManager bossBarManager;
    private final @NotNull HookManager hookManager;

    /**
     * Constructor
     * @param skyFlight A {@link SkyFlight} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param bossBarManager A {@link BossBarManager} instance.
     * @param hookManager A {@link HookManager} instance.
     */
    public FlightManager(
            @NotNull SkyFlight skyFlight,
            @NotNull SettingsManager settingsManager,
            @NotNull LocaleManager localeManager,
            @NotNull PlayerDataManager playerDataManager,
            @NotNull BossBarManager bossBarManager,
            @NotNull HookManager hookManager) {
        this.skyFlight = skyFlight;
        this.logger = skyFlight.getComponentLogger();
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.playerDataManager = playerDataManager;
        this.bossBarManager = bossBarManager;
        this.hookManager = hookManager;
    }

    /**
     * Get all player data for players that are using timed flight.
     * @return The player data collection.
     */
    public @NotNull Collection<PlayerData> getTimedFlightPlayerData() {
        return playerDataManager.getPlayerData()
                .stream()
                .filter(PlayerData::isTimedFlight)
                .toList();
    }

    /**
     * Is the player allowed to fly? Use {@link #enableFlight(Player, boolean)} if this returns true.
     * @param player The {@link Player} to check.
     * @param message Should detailed messages be sent to the player?
     * @return true if they can fly, false if not.
     */
    public boolean canFly(@NotNull Player player, boolean message) {
        // If the plugin's settings are invalid, log an error and always return false
        Settings settings = settingsManager.getConfiguration();
        if(!isSettingsValid(player, settings, message)) return false;

        // Operators can always fly
        if(player.isOp()) return true;

        boolean hasInfinite = hasInfiniteFlightPermission(player, false);
        boolean hasTimed = hasTimedFlightPermission(player, false);

        // If the player doesn't have either fly permissions, return false
        if(!hasInfinite && !hasTimed) {
            if(message) {
                Locale locale = localeManager.getConfiguration();

                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightNoPermission()));
            }

            return false;
        }

        // If the player has the bypass permission, they can fly everywhere.
        if(hasBypassPermission(player)) return true;

        // If the player is in a world where flight is configured to be not allowed, return false
        if(!isWorldAllowed(settings, player, message)) return false;

        // Check BentoBox protections
        if(!isBentoBoxAllowed(player, message)) return false;

        // Check WorldGuard protections
        if(!isWorldGuardAllowed(player, message)) return false;

        // Check player data and flight time if they can't use infinite flight
        if(!hasInfinite) {
            // Check if the player has valid player data
            @Nullable PlayerData playerData = playerDataManager.getPlayerData(player.getUniqueId());
            if(!isPlayerDataValid(player, playerData, message)) return false;

            // If the player can only use timed flight, and player has no flight time, return false
            return hasFlightTime(player, playerData, message);
        }

        // Return true as the player is allowed to fly
        return true;
    }

    /**
     * Is the player allowed to use infinite fly? Use {@link #enableInfiniteFlight(Player, boolean)} if this returns true.
     * @param player The {@link Player} to check.
     * @param message Should detailed messages be sent to the player?
     * @return true if they can fly, false if not.
     */
    public boolean canFlyInfinite(@NotNull Player player, boolean message) {
        // If the plugin's settings are invalid, log an error and always return false
        Settings settings = settingsManager.getConfiguration();
        if(!isSettingsValid(player, settings, message)) return false;

        // Operators can always fly
        if(player.isOp()) return true;

        // If the player doesn't have the fly permission, return false
        if(!hasInfiniteFlightPermission(player, message)) return false;

        // If the player has the bypass permission, they can fly everywhere.
        if(hasBypassPermission(player)) return true;

        // If the player is in a world where flight is configured to be not allowed, return false
        if(!isWorldAllowed(settings, player, message)) return false;

        // Check BentoBox protections
        if(!isBentoBoxAllowed(player, message)) return false;

        // Check WorldGuard protections
        return isWorldGuardAllowed(player, message);
    }

    /**
     * Is the player allowed to use timed fly? Use {@link #enableTimedFlight(Player, boolean)} if this returns true.
     * @param player The {@link Player} to check.
     * @param message Should detailed messages be sent to the player?
     * @return true if they can fly, false if not.
     */
    public boolean canFlyTimed(@NotNull Player player, boolean message) {
        // If the plugin's settings are invalid, log an error and always return false
        Settings settings = settingsManager.getConfiguration();
        if(!isSettingsValid(player, settings, message)) return false;

        // Check if the player has valid player data
        @Nullable PlayerData playerData = playerDataManager.getPlayerData(player.getUniqueId());
        if(!isPlayerDataValid(player, playerData, message)) return false;

        if(player.isOp()) {
            // If the player has no flight time, return false;
            return hasFlightTime(player, playerData, message);
        } else {
            // If the player doesn't have the fly permission, return false
            if(!hasTimedFlightPermission(player, message)) return false;

            // If the player has no flight time, return false;
            if(!hasFlightTime(player, playerData, message)) return false;
        }

        // If the player has the bypass permission, they can fly everywhere.
        if(hasBypassPermission(player)) return true;

        // If the player is in a world where flight is configured to be not allowed, return false
        if(!isWorldAllowed(settings, player, message)) return false;

        // Check BentoBox protections
        if(!isBentoBoxAllowed(player, message)) return false;

        // Check WorldGuard protections
        return isWorldGuardAllowed(player, message);
    }

    /**
     * Enable flight for the player.
     * @param player The {@link Player}.
     * @param message Should messages be sent to the player?
     * @return true if flight was enabled, false if not.
     */
    public boolean enableFlight(@NotNull Player player, boolean message) {
        if(player.hasPermission("skyflight.fly.infinite")) {
            return enableInfiniteFlight(player, message);
        } else if(player.hasPermission("skyflight.fly.timed")) {
            return enableTimedFlight(player, message);
        }

        return false;
    }

    /**
     * Enable infinite flight for the player.
     * @param player The {@link Player}.
     * @param message Should messages be sent to the player?
     * @return true if flight was enabled, false if not.
     */
    public boolean enableInfiniteFlight(@NotNull Player player, boolean message) {
        Locale locale = localeManager.getConfiguration();

        // Call FlightEnableEvent
        FlightEnableEvent flightEnableEvent = new FlightEnableEvent(player);
        //noinspection DataFlowIssue Required for Unit Testing purposes
        flightEnableEvent = skyFlight.callEvent(flightEnableEvent);

        // Check if cancelled
        if(flightEnableEvent.isCancelled()) return false;

        // Send a success message
        if(message) {
            player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightEnabled()));
        }

        // Show the boss bar
        bossBarManager.showInfiniteBossBar(player);

        // Set their flight to allowed
        player.setAllowFlight(true);

        return true;
    }

    /**
     * Enable timed flight for the player.
     * @param player The {@link Player}.
     * @param message Should messages be sent to the player?
     * @return true if flight was enabled, false if not.
     */
    public boolean enableTimedFlight(@NotNull Player player, boolean message) {
        Locale locale = localeManager.getConfiguration();
        // Check if the player has player data
        @Nullable PlayerData playerData = playerDataManager.getPlayerData(player.getUniqueId());
        if(playerData == null) {
            logger.warn(AdventureUtil.deserialize("Unable to enable flight for player " + player.getName() + " due to invalid player data."));

            if(message) {
                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.invalidPlayerData()));
            }

            return false;
        }

        // Call FlightEnableEvent
        FlightEnableEvent flightEnableEvent = new FlightEnableEvent(player);
        //noinspection DataFlowIssue Required for Unit Testing purposes
        flightEnableEvent = skyFlight.callEvent(flightEnableEvent);

        // Check if cancelled
        if(flightEnableEvent.isCancelled()) return false;

        // Send a success message
        if(message) {
            player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightEnabled()));
        }

        // Store that the player is using timed flight in their player data
        playerData.setTimedFlight(true);

        // Show the boss bar
        bossBarManager.showTimeBossBar(player);

        // Set their flight to allowed
        player.setAllowFlight(true);

        return true;
    }

    /**
     * Disable flight for the player after the seconds provided.
     * @apiNote This will check if the player can fly before actually disabling flight.
     * @param player The {@link Player}.
     * @param delaySeconds The delay in seconds.
     * @return A {@link CompletableFuture} of type {@link Boolean} when complete. The boolean will be true if successful, false if not.
     */
    public @NotNull CompletableFuture<Boolean> disableFlightWithDelay(@NotNull Player player, int delaySeconds) {
        Locale locale = localeManager.getConfiguration();
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();

        // Send a warning that flight will be disabled
        player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightDisabledDelay()));

        skyFlight.getServer().getScheduler().runTaskLater(skyFlight, () -> {
            if(!player.isOnline() || !player.isConnected()) {
                resultFuture.complete(false);
                return;
            }

            if(canFly(player, false)) {
                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightNotDisabled()));

                resultFuture.complete(false);
                return;
            }

            resultFuture.complete(disableFlight(player, true));
        }, 20L * delaySeconds);

        return resultFuture;
    }

    /**
     * Disable flight for the player.
     * @param player The {@link Player}.
     * @param message Should the message that flight was disabled be sent to the player?
     * @return true if flight was disabled, false if not.
     */
    public boolean disableFlight(@NotNull Player player, boolean message) {
        // Check if the player has player data
        @Nullable PlayerData playerData = playerDataManager.getPlayerData(player.getUniqueId());
        if(playerData != null) {
            // Store that they were last flying in their player data
            playerData.setTimedFlight(false);
        }

        // Set their flight to allowed and set them to be flying
        player.setAllowFlight(false);
        player.setFlying(false);

        // Set fall distance to 0
        player.setFallDistance(0);

        // Remove the boss bar
        bossBarManager.removeBossBar(player);

        // Send a message
        if(message) {
            Locale locale = localeManager.getConfiguration();

            player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightDisabled()));
        }

        return true;
    }

    /**
     * Is the plugin's settings valid?
     * @param player The {@link Player} to send error messages to.
     * @param settings The plugin's {@link Settings}.
     * @param message Should the player be sent error messages?
     * @return true if valid, false if not.
     */
    public boolean isSettingsValid(@NotNull Player player, @Nullable Settings settings, boolean message) {
        if(settings == null) {
            if(message) {
                Locale locale = localeManager.getConfiguration();

                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.invalidPluginSettings()));
            }

            return false;
        }

        return true;
    }

    /**
     * Does the player have the infinite flight permission?
     * @param player The {@link Player} to check and send error messages to.
     * @param message Should the player be sent error messages?
     * @return true if the player has the permission, false if not.
     */
    public boolean hasInfiniteFlightPermission(@NotNull Player player, boolean message) {
        if(!player.hasPermission("skyflight.fly.infinite")) {
            if(message) {
                Locale locale = localeManager.getConfiguration();

                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightNoPermission()));
            }

            return false;
        }

        return true;
    }

    /**
     * Does the player have the timed flight permission?
     * @param player The {@link Player} to check and send error messages to.
     * @param message Should the player be sent error messages?
     * @return true if the player has the permission, false if not.
     */
    public boolean hasTimedFlightPermission(@NotNull Player player, boolean message) {
        if(!player.hasPermission("skyflight.fly.timed")) {
            if(message) {
                Locale locale = localeManager.getConfiguration();

                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightNoPermission()));
            }

            return false;
        }

        return true;
    }

    /**
     * Does the player have the bypass permission?
     * @param player The {@link Player} to check.
     * @return true if the player has the permission, false if not.
     */
    public boolean hasBypassPermission(@NotNull Player player) {
        return player.hasPermission("skyflight.fly.bypass");
    }

    /**
     * Is flight allowed for the world the player's is in?
     * @param settings The plugin's {@link Settings}.
     * @param player The {@link Player}.
     * @param message Should the player be sent error messages?
     * @return true if flight is allowed in the world, false if not.
     */
    public boolean isWorldAllowed(@Nullable Settings settings, @NotNull Player player, boolean message) {
        Locale locale = localeManager.getConfiguration();

        if(settings == null) {
            if(message) {
                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightWorldNotAllowed()));
            }

            return false;
        }

        if(settings.disabledWorlds().contains(player.getWorld().getName())) {
            if(message) {
                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightWorldNotAllowed()));
            }

            return false;
        }

        return true;
    }

    /**
     * Is flight allowed based on BentoBox protections?
     * @param player The {@link Player}.
     * @param message Should the player be sent error messages?
     * @return true if flight is allowed, false if not.
     */
    public boolean isBentoBoxAllowed(@NotNull Player player, boolean message) {
        BentoBoxHook bentoBoxHook = hookManager.getHook(BentoBoxHook.class);
        if(!bentoBoxHook.isHooked()) return true;
        if(!bentoBoxHook.isGameModeWorld(player.getWorld())) return true;
        Locale locale = localeManager.getConfiguration();
        @Nullable Island island = bentoBoxHook.getIslandAtLocation(player.getLocation());

        if(island == null) {
            if(message) {
                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightOutsideIslandNotAllowed()));
            }

            return false;
        }

        if(bentoBoxHook.isOutsideIsland(player, island)) {
            if(message) {
                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightOutsideIslandNotAllowed()));
            }

            return false;
        }

        if(bentoBoxHook.isFlightDisallowed(player, island)) {
            if(message) {
                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightIslandNotAllowed()));
            }

            return false;
        }

        return true;
    }

    /**
     * Is flight allowed based on WorldGuard protections?
     * @param player The {@link Player}.
     * @param message Should the player be sent error messages?
     * @return true if flight is allowed, false if not.
     */
    public boolean isWorldGuardAllowed(@NotNull Player player, boolean message) {
        WorldGuardHook worldGuardHook = hookManager.getHook(WorldGuardHook.class);
        if(!worldGuardHook.isHooked()) return true;

        if(worldGuardHook.isFlightDisallowed(player, player.getLocation())) {
            if(message) {
                Locale locale = localeManager.getConfiguration();

                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightWorldGuardNotAllowed()));
            }

            return false;
        }

        return true;
    }

    /**
     * Does the player have valid {@link PlayerData}?
     * @param player The {@link Player}.
     * @param playerData The player's {@link PlayerData}.
     * @param message Should the player be sent error messages?
     * @return true if the player has valid player data, false if not.
     */
    public boolean isPlayerDataValid(@NotNull Player player, @Nullable PlayerData playerData, boolean message) {
        if(playerData == null) {
            if(message) {
                Locale locale = localeManager.getConfiguration();

                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.invalidPlayerData()));
            }

            return false;
        }

        return true;
    }

    /**
     * Does the player have flight time?
     * @param player The {@link Player}.
     * @param playerData The player's {@link PlayerData}.
     * @param message Should the player be sent error messages?
     * @return true if the player has flight time, false if not or player data is null.
     */
    public boolean hasFlightTime(@NotNull Player player, @Nullable PlayerData playerData, boolean message) {
        if(playerData == null || playerData.getFlightTime() <= 0) {
            if(message) {
                Locale locale = localeManager.getConfiguration();

                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightNoFlightTime()));
            }

            return false;
        }

        return true;
    }
}