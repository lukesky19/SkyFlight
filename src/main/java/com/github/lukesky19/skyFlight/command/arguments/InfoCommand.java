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

import com.github.lukesky19.skyFlight.player.PlayerData;
import com.github.lukesky19.skyFlight.locale.Locale;
import com.github.lukesky19.skyFlight.settings.Settings;
import com.github.lukesky19.skyFlight.flight.FlightManager;
import com.github.lukesky19.skyFlight.player.PlayerDataManager;
import com.github.lukesky19.skyFlight.locale.LocaleManager;
import com.github.lukesky19.skyFlight.settings.SettingsManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class creates the info command argument for the /skyflight command.
 */
public class InfoCommand {
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull FlightManager flightManager;

    /**
     * Constructor
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param flightManager A {@link FlightManager} instance.
     */
    public InfoCommand(
            @NotNull SettingsManager settingsManager,
            @NotNull LocaleManager localeManager,
            @NotNull PlayerDataManager playerDataManager,
            @NotNull FlightManager flightManager) {
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.playerDataManager = playerDataManager;
        this.flightManager = flightManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the info command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the info command argument.
     */
    public @NotNull LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("info")
                .requires(ctx -> ctx.getSender().hasPermission("skyflight.commands.skyflight.info"))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            Player player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();

                            sendInfoMessages(sender, player);

                            return 1;
                        }))
                .executes(ctx -> {
                    Locale locale = localeManager.getConfiguration();
                    CommandSender sender = ctx.getSource().getSender();
                    if(!(sender instanceof Player player)) {
                        sender.sendMessage(AdventureUtil.deserialize(locale.commandPlayerOnly()));
                        return 0;
                    }

                    sendInfoMessages(sender, player);

                    return 1;
                }).build();
    }

    /**
     * Send the info messages.
     * @param sender The {@link CommandSender} to send messages to.
     * @param player The {@link Player} to get info for.
     */
    private void sendInfoMessages(@NotNull CommandSender sender, @NotNull Player player) {
        Locale locale = localeManager.getConfiguration();
        @Nullable Settings settings = settingsManager.getConfiguration();
        @Nullable PlayerData playerData = playerDataManager.getPlayerData(player.getUniqueId());

        List<TagResolver.Single> placeholders = new ArrayList<>();
        placeholders.add(playerData != null ?
                Placeholder.component("has_player_data", AdventureUtil.deserialize("<green>true</green>")) :
                Placeholder.component("has_player_data", AdventureUtil.deserialize("<red>false</red>")));

        placeholders.add(flightManager.canFly(player, false) ?
                Placeholder.component("can_fly", AdventureUtil.deserialize("<green>true</green>")) :
                Placeholder.component("can_fly", AdventureUtil.deserialize("<red>false</red>")));

        placeholders.add(flightManager.isWorldAllowed(settings, player, false) ?
                Placeholder.component("world_allowed", AdventureUtil.deserialize("<green>true</green>")) :
                Placeholder.component("world_allowed", AdventureUtil.deserialize("<red>false</red>")));

        placeholders.add(flightManager.isBentoBoxAllowed(player, false) ?
                Placeholder.component("bentobox_allowed", AdventureUtil.deserialize("<green>true</green>")) :
                Placeholder.component("bentobox_allowed", AdventureUtil.deserialize("<red>false</red>")));

        placeholders.add(flightManager.isWorldGuardAllowed(player, false) ?
                Placeholder.component("worldguard_allowed", AdventureUtil.deserialize("<green>true</green>")) :
                Placeholder.component("worldguard_allowed", AdventureUtil.deserialize("<red>false</red>")));

        placeholders.add(flightManager.hasBypassPermission(player) ?
                Placeholder.component("has_bypass_permission", AdventureUtil.deserialize("<green>true</green>")) :
                Placeholder.component("has_bypass_permission", AdventureUtil.deserialize("<red>false</red>")));

        placeholders.add(flightManager.hasInfiniteFlightPermission(player, false) ?
                Placeholder.component("has_infinite_flight_permission", AdventureUtil.deserialize("<green>true</green>")) :
                Placeholder.component("has_infinite_flight_permission", AdventureUtil.deserialize("<red>false</red>")));

        placeholders.add(flightManager.hasTimedFlightPermission(player, false) ?
                Placeholder.component("has_timed_flight_permission", AdventureUtil.deserialize("<green>true</green>")) :
                Placeholder.component("has_timed_flight_permission", AdventureUtil.deserialize("<red>false</red>")));

        placeholders.add(Placeholder.component("flight_time", AdventureUtil.deserialize(
                localeManager.formatFlightTime(
                        localeManager.getConfiguration().timeFormat(),
                        playerData != null ? playerData.getFlightTime() : 0))));

        placeholders.add(player.getAllowFlight() ?
                Placeholder.component("player_allowed_flight", AdventureUtil.deserialize("<green>true</green>")) :
                Placeholder.component("player_allowed_flight", AdventureUtil.deserialize("<red>false</red>")));

        placeholders.add(player.isFlying() ?
                Placeholder.component("player_is_flying", AdventureUtil.deserialize("<green>true</green>")) :
                Placeholder.component("player_is_flying", AdventureUtil.deserialize("<red>false</red>")));

        placeholders.add(playerData != null && playerData.isTimedFlight() ?
                Placeholder.component("timed_flight", AdventureUtil.deserialize("<green>true</green>")) :
                Placeholder.component("timed_flight", AdventureUtil.deserialize("<red>false</red>")));

        for(String infoMessage : locale.info()) {
            sender.sendMessage(AdventureUtil.deserialize(player, infoMessage, placeholders));
        }
    }
}