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

import com.github.lukesky19.skyFlight.locale.Locale;
import com.github.lukesky19.skyFlight.locale.LocaleManager;
import com.github.lukesky19.skyFlight.time.TimeManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.mojang.brigadier.arguments.LongArgumentType;
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

import java.util.List;

/**
 * This class creates the time command argument for the /skyflight command.
 */
public class TimeCommand {
    private final @NotNull LocaleManager localeManager;
    private final @NotNull TimeManager timeManager;

    /**
     * Constructor
     * @param localeManager A {@link LocaleManager} instance.
     * @param timeManager A {@link TimeManager} instance.
     */
    public TimeCommand(
            @NotNull LocaleManager localeManager,
            @NotNull TimeManager timeManager) {
        this.localeManager = localeManager;
        this.timeManager = timeManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the time command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the time command argument.
     */
    public @NotNull LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("time")
            .requires(ctx -> ctx.getSender().hasPermission("skyflight.commands.skyflight.time"))
            .then(Commands.literal("add")
                .requires(ctx -> ctx.getSender().hasPermission("skyflight.commands.skyflight.time.add"))
                .then(Commands.argument("player", ArgumentTypes.player())
                    .then(Commands.argument("time", LongArgumentType.longArg(1))
                        .executes(ctx -> {
                            Locale locale = localeManager.getConfiguration();
                            CommandSender sender = ctx.getSource().getSender();
                            Player player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                            long time = ctx.getArgument("time", Long.class);
                            if(time <= 0) {
                                if(sender instanceof Player) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.timeInvalid()));
                                } else {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.timeInvalid()));
                                }

                                return 0;
                            }

                            if(timeManager.addFlightTime(player, time)) {
                                List<TagResolver.Single> placeholders = List.of(
                                        Placeholder.parsed("player", player.getName()),
                                        Placeholder.parsed("time", localeManager.formatFlightTime(
                                                locale.timeFormat(), timeManager.getFlightTime(player))));

                                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightTimeUpdated(), placeholders));

                                if(sender instanceof Player) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.playerFlightTimeUpdated(), placeholders));
                                } else {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.playerFlightTimeUpdated(), placeholders));
                                }

                                return 1;
                            } else {
                                if(sender instanceof Player) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.playerFlightTimeUpdateFailed(),
                                            List.of(Placeholder.parsed("player", player.getName()))));
                                } else {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.playerFlightTimeUpdateFailed(),
                                            List.of(Placeholder.parsed("player", player.getName()))));
                                }

                                return 0;
                            }
                        }))))
            .then(Commands.literal("remove")
                .requires(ctx -> ctx.getSender().hasPermission("skyflight.commands.skyflight.time.remove"))
                .then(Commands.argument("player", ArgumentTypes.player())
                    .then(Commands.argument("time", LongArgumentType.longArg(1))
                        .executes(ctx -> {
                            Locale locale = localeManager.getConfiguration();
                            CommandSender sender = ctx.getSource().getSender();
                            Player player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                            long time = ctx.getArgument("time", Long.class);
                            if(time <= 0) {
                                if(sender instanceof Player) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.timeInvalid()));
                                } else {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.timeInvalid()));
                                }

                                return 0;
                            }

                            if(timeManager.removeFlightTime(player, time)) {
                                List<TagResolver.Single> placeholders = List.of(
                                        Placeholder.parsed("player", player.getName()),
                                        Placeholder.parsed("time", localeManager.formatFlightTime(
                                                locale.timeFormat(), timeManager.getFlightTime(player))));

                                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightTimeUpdated(), placeholders));

                                if(sender instanceof Player) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.playerFlightTimeUpdated(), placeholders));
                                } else {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.playerFlightTimeUpdated(), placeholders));
                                }

                                return 1;
                            } else {
                                if(sender instanceof Player) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.playerFlightTimeUpdateFailed(),
                                            List.of(Placeholder.parsed("player", player.getName()))));
                                } else {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.playerFlightTimeUpdateFailed(),
                                            List.of(Placeholder.parsed("player", player.getName()))));
                                }

                                return 0;
                            }
                        }))))
            .then(Commands.literal("set")
                .requires(ctx -> ctx.getSender().hasPermission("skyflight.commands.skyflight.time.set"))
                .then(Commands.argument("player", ArgumentTypes.player())
                    .then(Commands.argument("time", LongArgumentType.longArg(1))
                        .executes(ctx -> {
                            Locale locale = localeManager.getConfiguration();
                            CommandSender sender = ctx.getSource().getSender();
                            Player player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                            long time = ctx.getArgument("time", Long.class);
                            if(time <= 0) {
                                if(sender instanceof Player) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.timeInvalid()));
                                } else {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.timeInvalid()));
                                }

                                return 0;
                            }

                            if(timeManager.setFlightTime(player, time)) {
                                List<TagResolver.Single> placeholders = List.of(
                                        Placeholder.parsed("player", player.getName()),
                                        Placeholder.parsed("time", localeManager.formatFlightTime(
                                                locale.timeFormat(), timeManager.getFlightTime(player))));

                                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightTimeUpdated(), placeholders));

                                if(sender instanceof Player) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.playerFlightTimeUpdated(), placeholders));
                                } else {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.playerFlightTimeUpdated(), placeholders));
                                }

                                return 1;
                            } else {
                                if(sender instanceof Player) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.playerFlightTimeUpdateFailed(),
                                            List.of(Placeholder.parsed("player", player.getName()))));
                                } else {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.playerFlightTimeUpdateFailed(),
                                            List.of(Placeholder.parsed("player", player.getName()))));
                                }

                                return 0;
                            }
                        }))))
            .then(Commands.literal("get")
                .requires(ctx -> ctx.getSender().hasPermission("skyflight.commands.skyflight.time.get"))
                .then(Commands.argument("player", ArgumentTypes.player())
                    .executes(ctx -> {
                        Locale locale = localeManager.getConfiguration();
                        CommandSender sender = ctx.getSource().getSender();
                        Player player = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();

                        List<TagResolver.Single> placeholders = List.of(
                                Placeholder.parsed("player", player.getName()),
                                Placeholder.parsed("time", localeManager.formatFlightTime(
                                        locale.timeFormat(), timeManager.getFlightTime(player))));

                        if(sender instanceof Player) {
                            sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.playerFlightTime(), placeholders));
                        } else {
                            sender.sendMessage(AdventureUtil.deserialize(locale.playerFlightTime(), placeholders));
                        }

                        return 1;
                    })))

            .executes(ctx -> {
                Locale locale = localeManager.getConfiguration();
                CommandSender sender = ctx.getSource().getSender();
                if(!(sender instanceof Player player)) {
                    sender.sendMessage(AdventureUtil.deserialize(locale.commandPlayerOnly()));
                    return 0;
                }

                List<TagResolver.Single> placeholders = List.of(
                        Placeholder.parsed("time", localeManager.formatFlightTime(
                                locale.timeFormat(), timeManager.getFlightTime(player))));

                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightTime(), placeholders));

                return 1;
            }).build();
    }
}