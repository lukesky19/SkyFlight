/*
    SkyPrestige allows players to prestige or reset their Island to unlock rewards after obtaining the required prestige points.
    Copyright (C) 2025 lukeskywlker19

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

import com.github.lukesky19.skyFlight.SkyFlight;
import com.github.lukesky19.skyFlight.integration.interfaces.Hook;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.flags.Flag;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.FlagsManager;
import world.bentobox.bentobox.managers.RanksManager;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/**
 * This class manages interfacing with the BentoBox plugin.
 */
public class BentoBoxHook implements Hook {
    private final @NotNull SkyFlight skyFlight;
    private @Nullable BentoBox bentoBox;
    private @Nullable Flag flightFlag;

    /**
     * Constructor
     * @param skyFlight A {@link SkyFlight} instance.
     */
    public BentoBoxHook(@NotNull SkyFlight skyFlight) {
        this.skyFlight = skyFlight;
        initialize();
    }

    /**
     * Get the {@link BentoBox} instance and any other classes necessary.
     */
    @Override
    public void initialize() {
        if(skyFlight.getServer().getPluginManager().getPlugin("BentoBox") != null) {
            bentoBox = BentoBox.getInstance();
        }
    }

    /**
     * Is the hook initialized?
     * @return true if hooked, otherwise false.
     */
    @Override
    public boolean isHooked() {
        return bentoBox != null;
    }

    /**
     * Is the player in a world managed by a BentoBox GameMode?
     * @param world The {@link World}.
     * @return true if the world is managed, false if not.
     */
    public boolean isGameModeWorld(@NotNull World world) {
        if(bentoBox == null) return false;

        return bentoBox.getIWM().getAddon(world).isPresent();
    }

    /**
     * Get the {@link Island} at the location provided.
     * @param location The {@link Location}.
     * @return The {@link Island} or null.
     */
    public @Nullable Island getIslandAtLocation(@NotNull Location location) {
        if(bentoBox == null) return null;

        return bentoBox.getIslandsManager().getIslandAt(location).orElse(null);
    }

    /**
     * Is the player outside the island's protected area?
     * @param player The {@link Player}.
     * @param island The {@link Island}.
     * @return true if outside the island's protected area, false if not.
     */
    public boolean isOutsideIsland(@NotNull Player player, @NotNull Island island) {
        Location playerLocation = player.getLocation();
        int playerX = playerLocation.getBlockX();
        int playerZ = playerLocation.getBlockZ();

        int minX = Math.min(island.getMinProtectedX(), island.getMaxProtectedX());
        int maxX = Math.max(island.getMinProtectedX(), island.getMaxProtectedX());
        int minZ = Math.min(island.getMinProtectedZ(), island.getMaxProtectedZ());
        int maxZ = Math.max(island.getMinProtectedZ(), island.getMaxProtectedZ());

        return playerX < minX || playerX > maxX || playerZ < minZ || playerZ > maxZ;
    }

    /**
     * Is the player disallowed to fly on the island?
     * First checks the flight flag if non-null otherwise checks if the player is the island owner or an island member.
     * @param player The {@link Player}.
     * @param island The {@link Island}.
     * @return true if flight is disallowed, false if not.
     */
    public boolean isFlightDisallowed(@NotNull Player player, @NotNull Island island) {
        if(flightFlag != null) {
            return !island.isAllowed(User.getInstance(player), flightFlag);
        } else {
            UUID playerId = player.getUniqueId();

            return (island.getOwner() != null && !island.getOwner().equals(playerId)) || !island.getMemberSet().contains(playerId);
        }
    }

    /**
     * Register the flag that controls island flight.
     */
    public void registerFlightFlag() {
        if(bentoBox == null) return;
        if(flightFlag != null) return;
        FlagsManager flagsManager = bentoBox.getFlagsManager();

        Optional<Flag> optionalFlag = flagsManager.getFlag("FLIGHT");
        if(optionalFlag.isPresent()) {
            flightFlag = optionalFlag.get();

            flightFlag.setGameModes(new HashSet<>(bentoBox.getAddonsManager().getGameModeAddons()));

            return;
        }

        flightFlag = new Flag.Builder("FLIGHT", Material.ELYTRA)
                .type(Flag.Type.PROTECTION)
                .mode(Flag.Mode.BASIC)
                .defaultRank(RanksManager.MEMBER_RANK)
                .defaultSetting(true)
                .build();

        if(flightFlag != null) {
            flightFlag.setTranslatedName(Locale.US, "Flight");
            flightFlag.setTranslatedDescription(Locale.US, "Set who can use flight on the island.");

            flightFlag.setGameModes(new HashSet<>(bentoBox.getAddonsManager().getGameModeAddons()));

            flagsManager.registerFlag(flightFlag);
        }
    }

    /**
     * Unregisters the flag that controls island flight.
     */
    public void unRegisterFlightFlag() {
        if(bentoBox == null) return;
        if(flightFlag != null) return;
        FlagsManager flagsManager = bentoBox.getFlagsManager();

        flagsManager.unregister(flightFlag);
    }

    /**
     * Get the {@link Flag} that was created to control flight.
     * @return The {@link Flag} or null if creation failed.
     */
    public @Nullable Flag getFlightFlag() {
        return flightFlag;
    }
}
