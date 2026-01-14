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
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.common.abstracts.SkyPlugin;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class creates the reload command argument for the /skyflight command.
 */
public class ReloadCommand {
    private final @NotNull SkyPlugin skyPlugin;
    private final @NotNull ComponentLogger logger;
    private final @NotNull LocaleManager localeManager;

    /**
     * Constructor
     * @param skyPlugin A {@link SkyPlugin} instance.
     * @param localeManager A {@link LocaleManager} instance.
     */
    public ReloadCommand(@NotNull SkyPlugin skyPlugin, @NotNull LocaleManager localeManager) {
        this.skyPlugin = skyPlugin;
        this.logger = skyPlugin.getComponentLogger();
        this.localeManager = localeManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the reload command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API for the reload command argument.
     */
    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("reload")
                .requires(ctx -> ctx.getSender().hasPermission("skyflight.commands.skyflight.reload"))
                .executes(ctx -> {
                    skyPlugin.reload();

                    Locale locale = localeManager.getConfiguration();

                    if(ctx.getSource().getSender() instanceof Player player) {
                        player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + locale.reload()));
                    } else {
                        logger.info(AdventureUtil.deserialize(locale.reload()));
                    }

                    return 1;
                }).build();
    }
}
