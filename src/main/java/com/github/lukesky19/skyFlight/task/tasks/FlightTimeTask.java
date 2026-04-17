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
package com.github.lukesky19.skyFlight.task.tasks;

import com.github.lukesky19.skyFlight.SkyFlight;
import com.github.lukesky19.skyFlight.locale.Locale;
import com.github.lukesky19.skyFlight.locale.LocaleManager;
import com.github.lukesky19.skyFlight.player.PlayerData;
import com.github.lukesky19.skyFlight.flight.FlightManager;
import com.github.lukesky19.skyFlight.time.TimeManager;
import com.github.lukesky19.skylib.paper.api.adventure.PaperAdventureUtility;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This task decrements player flight time if timed flight is being used, if they are actively flying, displays warnings before flight time runs out, and disables flight if time runs out.
 */
public class FlightTimeTask extends BukkitRunnable {
    private final @NotNull SkyFlight skyFlight;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull FlightManager flightManager;
    private final @NotNull TimeManager timeManager;

    /**
     * Constructor
     * @param skyFlight A {@link SkyFlight} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param flightManager A {@link FlightManager} instance.
     * @param timeManager A {@link TimeManager} instance.
     */
    public FlightTimeTask(
            @NotNull SkyFlight skyFlight,
            @NotNull LocaleManager localeManager,
            @NotNull FlightManager flightManager,
            @NotNull TimeManager timeManager) {
        this.skyFlight = skyFlight;
        this.localeManager = localeManager;
        this.flightManager = flightManager;
        this.timeManager = timeManager;
    }

    @Override
    public void run() {
        Locale locale = localeManager.getConfiguration();

        // Populate the Map for all players that are online and connected, allowed to fly, and are actively flying.
        Map<Player, PlayerData> playerDataMap = new HashMap<>();
        flightManager.getTimedFlightPlayerData()
                .forEach(playerData -> {
                    @Nullable Player player = skyFlight.getServer().getPlayer(playerData.getPlayerId());

                    if(player != null && player.isOnline() && player.isConnected() && player.getAllowFlight() && player.isFlying()) {
                        playerDataMap.put(player, playerData);
                    }
                });

        // Update flight time for all players actively flying (timed flight), send warnings for low flight time, and disable flight when flight time reaches 0
        playerDataMap.forEach((player, playerData) -> {
            long flightTime = playerData.getFlightTime();
            if(flightTime > 31) {
                timeManager.removeFlightTime(player, 1);
            } else if(flightTime == 31) {
                timeManager.removeFlightTime(player, 1);

                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.flightTimeWarning(), List.of(Placeholder.parsed("time", "30"))));
            } else if(flightTime == 16) {
                timeManager.removeFlightTime(player, 1);

                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.flightTimeWarning(), List.of(Placeholder.parsed("time", "15"))));
            } else if(flightTime == 11) {
                timeManager.removeFlightTime(player, 1);

                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.flightTimeWarning(), List.of(Placeholder.parsed("time", "10"))));
            } else if(flightTime == 6) {
                timeManager.removeFlightTime(player, 1);

                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.flightTimeWarning(), List.of(Placeholder.parsed("time", "5"))));
            } else if(flightTime == 5) {
                timeManager.removeFlightTime(player, 1);

                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.flightTimeWarning(), List.of(Placeholder.parsed("time", "4"))));
            } else if(flightTime == 4) {
                timeManager.removeFlightTime(player, 1);

                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.flightTimeWarning(), List.of(Placeholder.parsed("time", "3"))));
            } else if(flightTime == 3) {
                timeManager.removeFlightTime(player, 1);

                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.flightTimeWarning(), List.of(Placeholder.parsed("time", "2"))));
            } else if(flightTime == 2) {
                timeManager.removeFlightTime(player, 1);

                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.flightTimeWarning(), List.of(Placeholder.parsed("time", "1"))));
            } else if(flightTime == 1) {
                timeManager.removeFlightTime(player, 1);

                player.sendMessage(PaperAdventureUtility.deserialize(locale.prefix() + locale.flightTimeExhausted()));

                flightManager.disableFlight(player, false);
            } else {
                timeManager.setFlightTime(player, 0);

                player.sendMessage(PaperAdventureUtility.deserialize(locale.prefix() + locale.flightTimeExhausted()));

                flightManager.disableFlight(player, false);
            }
        });
    }
}
