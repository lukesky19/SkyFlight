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
package com.github.lukesky19.skyFlight.command;

import com.github.lukesky19.skyFlight.command.arguments.*;
import com.github.lukesky19.skyFlight.locale.Locale;
import com.github.lukesky19.skyFlight.time.TimeManager;
import com.github.lukesky19.skyFlight.locale.LocaleManager;
import com.github.lukesky19.skyFlight.flight.FlightManager;
import com.github.lukesky19.skyFlight.player.PlayerDataManager;
import com.github.lukesky19.skyFlight.settings.SettingsManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.common.abstracts.SkyPlugin;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class manages the creation of the plugin's command(s).
 */
public class SkyFlightCommand {
    private final @NotNull SkyPlugin plugin;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull FlightManager flightManager;
    private final @NotNull TimeManager timeManager;

    /**
     * Constructor
     * @param plugin A {@link SkyPlugin} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param flightManager A {@link FlightManager} instance.
     * @param timeManager A {@link TimeManager} instance.
     */
    public SkyFlightCommand(
            @NotNull SkyPlugin plugin,
            @NotNull SettingsManager settingsManager,
            @NotNull LocaleManager localeManager,
            @NotNull PlayerDataManager playerDataManager,
            @NotNull FlightManager flightManager,
            @NotNull TimeManager timeManager) {
        this.plugin = plugin;
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.playerDataManager = playerDataManager;
        this.flightManager = flightManager;
        this.timeManager = timeManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the /skyflight command.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the /skyflight command.
     */
    public @NotNull LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("skyflight")
                .requires(ctx -> ctx.getSender().hasPermission("skyflight.commands.skyflight"))
                .then(new HelpCommand(localeManager).createCommand())
                .then(new ImportCommand(plugin, playerDataManager, flightManager).createCommand())
                .then(new InfoCommand(settingsManager, localeManager, playerDataManager, flightManager).createCommand())
                .then(new ReloadCommand(plugin, localeManager).createCommand())
                .then(new TimeCommand(localeManager, timeManager).createCommand())
                .then(Commands.literal("infinite")
                        .requires(ctx -> ctx.getSender() instanceof Player player && player.hasPermission("skyflight.fly.infinite"))
                        .executes(ctx -> {
                            Player player = (Player) ctx.getSource().getSender();

                            // Check if flight should be enabled or disabled
                            if(player.getAllowFlight()) {
                                // Disable flight
                                return flightManager.disableFlight(player, true) ? 1 : 0;
                            } else {
                                // Enable flight if the player can fly
                                if(flightManager.canFlyInfinite(player, true)) {
                                    return flightManager.enableInfiniteFlight(player, true) ? 1 : 0;
                                }

                                return 0;
                            }
                        }))
                .then(Commands.literal("timed")
                        .requires(ctx -> ctx.getSender() instanceof Player player && player.hasPermission("skyflight.fly.timed"))
                        .executes(ctx -> {
                            Player player = (Player) ctx.getSource().getSender();

                            // Check if flight should be enabled or disabled
                            if(player.getAllowFlight()) {
                                // Disable flight
                                return flightManager.disableFlight(player, true) ? 1 : 0;
                            } else {
                                // Enable flight if the player can fly
                                if(flightManager.canFlyTimed(player, true)) {
                                    return flightManager.enableTimedFlight(player, true) ? 1 : 0;
                                }

                                return 0;
                            }
                        }))
                .executes(ctx -> {
                    Locale locale = localeManager.getConfiguration();
                    CommandSender sender = ctx.getSource().getSender();
                    if(!(sender instanceof Player player)) {
                        sender.sendMessage(AdventureUtil.deserialize(locale.commandPlayerOnly()));
                        return 0;
                    }

                    // Check if flight should be enabled or disabled
                    if(player.getAllowFlight()) {
                        // Disable flight
                        return flightManager.disableFlight(player, true) ? 1 : 0;
                    } else {
                        // Enable flight if the player can fly
                        if(flightManager.canFly(player, true)) {
                            return flightManager.enableFlight(player, true) ? 1 : 0;
                        }

                        return 0;
                    }
                }).build();
    }
}