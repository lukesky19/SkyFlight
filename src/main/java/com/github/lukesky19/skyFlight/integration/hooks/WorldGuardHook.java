/*
    SkyTools adds new unique tools to the game.
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
package com.github.lukesky19.skyFlight.integration.hooks;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.integration.Hook;
import com.github.lukesky19.skylib.paper.api.plugin.SkyPlugin;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class manages interfacing with WorldGuard.
 */
public class WorldGuardHook implements Hook {
    private final @NotNull SkyPlugin skyPlugin;
    private @Nullable WorldGuard worldGuard;
    private @Nullable StateFlag flightFlag;

    /**
     * Constructor
     * @param skyPlugin A {@link SkyPlugin} instance.
     */
    public WorldGuardHook(@NotNull SkyPlugin skyPlugin) {
        this.skyPlugin = skyPlugin;
    }

    @Override
    public void initialize() {
        if(skyPlugin.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            worldGuard = WorldGuard.getInstance();
        }
    }

    @Override
    public boolean isHooked() {
        return worldGuard != null;
    }

    /**
     * Is flight disallowed according to the flight flag for the player?
     * @param player The {@link Player} to check.
     * @param location The {@link Location} to check.
     * @return true if flight is disallowed, false if not.
     */
    public boolean isFlightDisallowed(@NotNull Player player, @NotNull Location location) {
        if(worldGuard == null) return false;
        if(flightFlag == null) return false;

        RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(location);
        LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        World world = BukkitAdapter.adapt(location.getWorld());

        if(worldGuard.getPlatform().getSessionManager().hasBypass(wgPlayer, world)) {
            return false;
        }

        @Nullable StateFlag.State state = query.queryState(wgLocation, wgPlayer, flightFlag);

        return state != null && state.equals(StateFlag.State.DENY);
    }

    /**
     * Register the WorldGuard flag that controls flight.
     * @param logger The plugin's {@link ComponentLogger}.
     */
    public void registerFlightFlag(@NotNull ComponentLogger logger) {
        if(flightFlag != null) return;

        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("skyflight", false);

            registry.register(flag);

            flightFlag = flag;
        } catch (FlagConflictException flagConflictException) {
            Flag<?> existing = registry.get("skyflight");
            if(existing instanceof StateFlag existingFlag) {
                flightFlag = existingFlag;
            } else {
                logger.warn(AdventureUtility.plain("SkyFlight WorldGuard Flight Flag overwritten by another plugin! Please report the incompatible plugin."));
            }
        } catch (IllegalStateException illegalStateException) {
            logger.warn(AdventureUtility.plain("WorldGuard flags cannot be registered after the server has been started. Please restart your server."));
            logger.warn(AdventureUtility.plain("Attempting to get an existing flag that was registered (if any)."));

            Flag<?> existing = registry.get("skyflight");
            if(existing instanceof StateFlag existingFlag) {
                flightFlag = existingFlag;
            } else {
                logger.warn(AdventureUtility.plain("No existing SkyFlight Flight Flag found. Most likely you loaded the plugin outside of a fresh server (re)start."));
            }
        }
    }
}