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
package com.github.lukesky19.skyFlight.command.arguments;

import com.github.lukesky19.skyFlight.player.LegacyIslandFlyData;
import com.github.lukesky19.skyFlight.player.PlayerData;
import com.github.lukesky19.skyFlight.flight.FlightManager;
import com.github.lukesky19.skyFlight.player.PlayerDataManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.common.abstracts.SkyPlugin;
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.gson.GsonConfigurationLoader;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

/**
 * This class creates the import command argument for the /skyflight command.
 */
public class ImportCommand {
    private final @NotNull SkyPlugin skyPlugin;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull FlightManager flightManager;

    /**
     * Constructor
     * @param skyPlugin A {@link SkyPlugin} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param flightManager A {@link FlightManager} instance.
     */
    public ImportCommand(
            @NotNull SkyPlugin skyPlugin,
            @NotNull PlayerDataManager playerDataManager,
            @NotNull FlightManager flightManager) {
        this.skyPlugin = skyPlugin;
        this.playerDataManager = playerDataManager;
        this.flightManager = flightManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the import command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the import command argument.
     */
    public @NotNull LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("import")
                .requires(ctx -> ctx.getSender().hasPermission("skyflight.commands.skyflight.import"))
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    Path legacyPath = Path.of("plugins" + File.separator + "BentoBox" + File.separator + "database" + File.separator + "IslandFlyPlayerData" + File.separator);
                    File legacyFile = legacyPath.toFile();

                    if(!legacyFile.exists() || !legacyFile.isDirectory()) {
                        sender.sendMessage(AdventureUtil.deserialize("<red>Unable to import data from the BentoBox IslandFly addon due to no data to import."));
                        return 0;
                    }

                    // Disable flight for any players then unload any player data
                    skyPlugin.getServer().getOnlinePlayers().forEach(player -> {
                        flightManager.disableFlight(player, false);

                        playerDataManager.unloadPlayerData(player.getUniqueId());
                    });

                    for(File playerFile : Objects.requireNonNull(legacyFile.listFiles())) {
                        if(playerFile.isDirectory()) continue;

                        try {
                            GsonConfigurationLoader loader = ConfigurationUtility.getGsonConfigurationLoader(playerFile.toPath());
                            LegacyIslandFlyData legacyIslandFlyData = loader.load().get(LegacyIslandFlyData.class);

                            if(legacyIslandFlyData == null) {
                                sender.sendMessage(AdventureUtil.deserialize("<red>Failed to load data from " + playerFile.getName()));
                                continue;
                            }

                            if(legacyIslandFlyData.uuid() == null) {
                                sender.sendMessage(AdventureUtil.deserialize("<red>The UUID from the legacy data is null. File Name: " + playerFile.getName()));
                                continue;
                            }

                            if(legacyIslandFlyData.timeSeconds() <= 0) continue;

                            PlayerData playerData = new PlayerData(legacyIslandFlyData.uuid());
                            playerData.setFlightTime(legacyIslandFlyData.timeSeconds());

                            playerDataManager.setPlayerData(legacyIslandFlyData.uuid(), playerData, true);
                        } catch (ConfigurateException e) {
                            sender.sendMessage(AdventureUtil.deserialize("<red>An error occurred while loading data from " + playerFile.getName() + ". Error: " + e.getMessage()));
                        }
                    }

                    // Save player data
                    playerDataManager.savePlayerData();

                    // Clear any loaded player data
                    playerDataManager.clearPlayerData();

                    // Load player data and enable flight if necessary
                    skyPlugin.getServer().getOnlinePlayers().forEach(player -> {
                        playerDataManager.loadPlayerData(player.getUniqueId()).thenAccept(v -> {
                            if(flightManager.canFly(player, true)) {
                                flightManager.enableFlight(player, true);
                            }
                        });
                    });

                    // Send a success message
                    sender.sendMessage(AdventureUtil.deserialize("<green>Data importation is done.</green>"));

                    return 1;
                }).build();
    }
}