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
import com.github.lukesky19.skyFlight.bossbar.BossBarManager;
import com.github.lukesky19.skyFlight.common.MockBukkitExtension;
import com.github.lukesky19.skyFlight.integration.HookManager;
import com.github.lukesky19.skyFlight.integration.hooks.BentoBoxHook;
import com.github.lukesky19.skyFlight.integration.hooks.WorldGuardHook;
import com.github.lukesky19.skyFlight.locale.Locale;
import com.github.lukesky19.skyFlight.locale.LocaleManager;
import com.github.lukesky19.skyFlight.player.PlayerData;
import com.github.lukesky19.skyFlight.player.PlayerDataManager;
import com.github.lukesky19.skyFlight.settings.Settings;
import com.github.lukesky19.skyFlight.settings.SettingsManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.scheduler.BukkitSchedulerMock;
import org.mockito.junit.jupiter.MockitoExtension;
import world.bentobox.bentobox.database.objects.Island;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests {@link FlightManager}.
 */
@ExtendWith({MockBukkitExtension.class, MockitoExtension.class})
public class FlightManagerTest {
    private static Settings settings;
    private static Locale locale;

    /**
     * Create the settings and locale for the tests.
     */
    @BeforeAll
    public static void beforeAll() {
        settings = new Settings(
                "1.0.0.0",
                "en_US",
                List.of("world"),
                new Settings.BossBarConfig("Flight Enabled", BossBar.Color.BLUE, BossBar.Overlay.PROGRESS),
                new Settings.BossBarConfig("Flight Enabled | Time: <time>", BossBar.Color.RED, BossBar.Overlay.PROGRESS));

        locale = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(
                        "<#FFA756>SkyFlight is developed by <white><bold>lukeskywlker19</bold></white>.</#FFA756>",
                        "<#FFA756>Source code is released on GitHub: <click:OPEN_URL:https://github.com/lukesky19><yellow><underlined><bold>https://github.com/lukesky19</bold></underlined></yellow></click></#FFA756>",
                        " ",
                        "<#FFA756><bold>List of Commands:</bold></#FFA756>",
                        "<white>/</white><#FFA756>fly</#FFA756>",
                        "<white>/</white><#FFA756>skyflight</#FFA756>",
                        "<white>/</white><#FFA756>skyflight</#FFA756> <yellow>help</yellow>",
                        "<white>/</white><#FFA756>skyflight</#FFA756> <yellow>reload</yellow>",
                        "<white>/</white><#FFA756>skyflight</#FFA756> <yellow>import</yellow>",
                        "<white>/</white><#FFA756>skyflight</#FFA756> <yellow>time</yellow>",
                        "<white>/</white><#FFA756>skyflight</#FFA756> <yellow>time add <player> <time></yellow>",
                        "<white>/</white><#FFA756>skyflight</#FFA756> <yellow>time remove <player> <time></yellow>",
                        "<white>/</white><#FFA756>skyflight</#FFA756> <yellow>time set <player> <time></yellow>",
                        "<white>/</white><#FFA756>skyflight</#FFA756> <yellow>time get <player></yellow>",
                        "<white>/</white><#FFA756>skyflight</#FFA756> <yellow>info <player></yellow>"
                ),
                List.of(
                        "<#FFA756>===</#FFA756> <#D56600>SkyFlight Player Info</#D56600> <#FFA756>===</#FFA756>",
                        "<gray>Has Player Data: </gray> <has_player_data>",
                        "<gray>Can Fly: </gray> <can_fly>",
                        "<gray>World Allowed: </gray> <world_allowed>",
                        "<gray>BentoBox Allowed: </gray> <bentobox_allowed>",
                        "<gray>WorldGuard Allowed: </gray> <worldguard_allowed>",
                        "<gray>Has Bypass Permission: </gray> <has_bypass_permission>",
                        "<gray>Has Infinite Flight Permission: </gray> <has_infinite_flight_permission>",
                        "<gray>Has Timed Flight Permission: </gray> <has_timed_flight_permission>",
                        "<gray>Flight Time: </gray> <time>",
                        "<gray>Player Flight Allowed: </gray> <player_allowed_flight>",
                        "<gray>Is Player Flying: </gray> <player_is_flying>",
                        "<gray>Timed Flight: </gray> <timed_flight>"
                ),
                "<#FF4D4D>The plugin's settings are invalid. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>Your player lacks any player data. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>You do not have permission to fly.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly in this world.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly on this island.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly outside of islands in this world.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly here.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly due to lack of flight time.</#FF4D4D>",
                "<#FFA756>Your flight has been enabled.</#FFA756>",
                "<#FFA756>Your flight has been disabled.</#FFA756>",
                "<#FF4D4D>Your flight will be disabled in 5 seconds.</#FF4D4D>",
                "<#FFA756>Flight was not disabled because you are able to fly again before it was disabled.</#FFA756>",
                "<#FFA756>You have <time> flight time remaining.</#FFA756>",
                "<#FF4D4D>Your flight time has run out and your flight has been disabled.</#FF4D4D>",
                "<#FF4D4D>This command can only be used by a player.</#FF4D4D>",
                "<#FF4D4D>The time must be greater than 0.</#FF4D4D>",
                "<#FFA756>Your flight time has been updated. Flight time: <time>.</#FFA756>",
                "<#FFA756>Player <white><player></white> had their flight time updated. Flight time: <time>.</#FFA756>",
                "<#FF4D4D>Failed to update the flight time for player <white><player></white> due to an error.</#FF4D4D>",
                "<#FFA756>You have <time> flight time.</#FFA756>",
                "<#FFA756>Player <white><player></white> has <time> flight time.</#FFA756>",
                new Locale.TimeFormat(
                        "",
                        "<yellow><years></yellow> year(s)",
                        "<yellow><months></yellow> month(s)",
                        "<yellow><weeks></yellow> week(s)",
                        "<yellow><days></yellow> day(s)",
                        "<yellow><hours></yellow> hour(s)",
                        "<yellow><minutes></yellow> minute(s)",
                        "<yellow><seconds></yellow> second(s)",
                        ""));
    }

    /**
     * Test {@link FlightManager#getTimedFlightPlayerData()}.
     */
    @Test
    public void testGetTimedFlightPlayerData() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Map<UUID, PlayerData> playerDataMap = new HashMap<>();

        UUID playerId1 = UUID.randomUUID();
        PlayerData playerData1 = new PlayerData(playerId1);
        playerData1.setFlightTime(300);
        playerDataMap.put(playerId1, playerData1);

        UUID playerId2 = UUID.randomUUID();
        PlayerData playerData2 = new PlayerData(playerId2);
        playerData2.setFlightTime(100);
        playerData2.setTimedFlight(true);
        playerDataMap.put(playerId2, playerData2);

        when(playerDataManager.getPlayerData()).thenReturn(playerDataMap.values());

        List<PlayerData> timedFlightPlayerData = new ArrayList<>(flightManager.getTimedFlightPlayerData());
        assertEquals(1, timedFlightPlayerData.size());
        assertSame(playerData2, timedFlightPlayerData.getFirst());
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, but the plugin's settings are invalid.
     */
    @Test
    public void testCanFlyInvalidSettings() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(null);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);

        assertFalse(flightManager.canFly(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.invalidPluginSettings()));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, but the plugin's settings are invalid. The player is not messaged here.
     */
    @Test
    public void testCanFlyInvalidSettingsNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(null);
        Player player = mock(Player.class);

        assertFalse(flightManager.canFly(player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)} and the player is an operator.
     */
    @Test
    public void testCanFlyPlayerOp() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(true);

        assertTrue(flightManager.canFly(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, but the player doesn't have permission for infinite and timed flight.
     */
    @Test
    public void testCanFlyNoFlightPermissions() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);

        assertFalse(flightManager.canFly(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightNoPermission()));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, but the player doesn't have permission for infinite and timed flight.
     * The player is not messaged here.
     */
    @Test
    public void testCanFlyNoFlightPermissionsNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);

        assertFalse(flightManager.canFly(player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, but the player has the bypass permission.
     */
    @Test
    public void testCanFlyHasBypassPermission() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(true);

        assertTrue(flightManager.canFly(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, but the world the player is in doesn't allow flight.
     */
    @Test
    public void testCanFlyWorldNotAllowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world");

        assertFalse(flightManager.canFly(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightWorldNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, but the world the player is in doesn't allow flight.
     * The player is not messaged here.
     */
    @Test
    public void testCanFlyWorldNotAllowedNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world");

        assertFalse(flightManager.canFly(player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, but BentoBox isn't hooked into.
     */
    @Test
    public void testCanFlyBentoBoxNotHooked() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(false);

        assertTrue(flightManager.canFly(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, but the player isn't on an island.
     */
    @Test
    public void testCanFlyBentoBoxWorldNotGameModeWorld() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(false);

        assertTrue(flightManager.canFly(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, but the player isn't on an island.
     */
    @Test
    public void testCanFlyBentoBoxNoIsland() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(null);

        assertFalse(flightManager.canFly(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightOutsideIslandNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, but the player is outside the island's protection range.
     */
    @Test
    public void testCanFlyBentoBoxOutsideIsland() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(true);

        assertFalse(flightManager.canFly(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightOutsideIslandNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, but the island's settings disallow flight.
     */
    @Test
    public void testCanFlyBentoBoxIslandSettingsDisallowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(true);

        assertFalse(flightManager.canFly(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightIslandNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, where BentoBox allows flight.
     */
    @Test
    public void testCanFlyBentoBoxFlightAllowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(false);

        assertTrue(flightManager.canFly(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, where WorldGuard disallows flight.
     */
    @Test
    public void testCanFlyWorldGuardFlightDisallowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(true);
        when(worldGuardHook.isFlightDisallowed(player, location)).thenReturn(true);

        assertFalse(flightManager.canFly(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightWorldGuardNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, where WorldGuard allows flight.
     */
    @Test
    public void testCanFlyWorldGuardFlightAllowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(true);
        when(worldGuardHook.isFlightDisallowed(player, location)).thenReturn(false);

        assertTrue(flightManager.canFly(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, where the player only has timed flight, but no player data.
     */
    @Test
    public void testCanFlyTimedFlightOnlyNoPlayerData() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(true);
        when(worldGuardHook.isFlightDisallowed(player, location)).thenReturn(false);

        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(null);

        assertFalse(flightManager.canFly(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.invalidPlayerData()));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, where the player only has timed flight, but no flight time.
     */
    @Test
    public void testCanFlyTimedFlightOnlyNoFlightTime() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(true);
        when(worldGuardHook.isFlightDisallowed(player, location)).thenReturn(false);

        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        assertFalse(flightManager.canFly(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightNoFlightTime()));
    }

    /**
     * Test {@link FlightManager#canFly(Player, boolean)}, where the player only has timed flight, and has flight time.
     */
    @Test
    public void testCanFlyFlightOnlyHasFlightTime() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(true);
        when(worldGuardHook.isFlightDisallowed(player, location)).thenReturn(false);

        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        assertTrue(flightManager.canFly(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, but the plugin's settings are invalid.
     */
    @Test
    public void testCanFlyInfiniteFlyInvalidSettings() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(null);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);

        assertFalse(flightManager.canFlyInfinite(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.invalidPluginSettings()));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, but the plugin's settings are invalid. The player is not messaged here.
     */
    @Test
    public void testCanFlyInfiniteInvalidSettingsNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(null);
        Player player = mock(Player.class);

        assertFalse(flightManager.canFlyInfinite(player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)} and the player is an operator.
     */
    @Test
    public void testCanFlyInfinitePlayerOp() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(true);

        assertTrue(flightManager.canFlyInfinite(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, but the player doesn't have permission for infinite flight.
     */
    @Test
    public void testCanFlyInfiniteNoFlightPermission() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(false);

        assertFalse(flightManager.canFlyInfinite(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightNoPermission()));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, but the player doesn't have permission for infinite flight.
     * The player is not messaged here.
     */
    @Test
    public void testCanFlyInfiniteNoFlightPermissionNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(false);

        assertFalse(flightManager.canFlyInfinite(player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, but the player has the bypass permission.
     */
    @Test
    public void testCanFlyInfiniteHasBypassPermission() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(true);

        assertTrue(flightManager.canFlyInfinite(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, but the world the player is in doesn't allow flight.
     */
    @Test
    public void testCanFlyInfiniteWorldNotAllowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world");

        assertFalse(flightManager.canFlyInfinite(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightWorldNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, but the world the player is in doesn't allow flight.
     * The player is not messaged here.
     */
    @Test
    public void testCanFlyInfiniteWorldNotAllowedNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world");

        assertFalse(flightManager.canFlyInfinite(player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, but BentoBox isn't hooked into.
     */
    @Test
    public void testCanFlyInfiniteBentoBoxNotHooked() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(false);

        assertTrue(flightManager.canFlyInfinite(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, but the player isn't on an island.
     */
    @Test
    public void testCanFlyInfiniteBentoBoxWorldNotGameModeWorld() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(false);

        assertTrue(flightManager.canFlyInfinite(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, but the player isn't on an island.
     */
    @Test
    public void testCanFlyInfiniteBentoBoxNoIsland() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(null);

        assertFalse(flightManager.canFlyInfinite(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightOutsideIslandNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, but the player is outside the island's protection range.
     */
    @Test
    public void testCanFlyInfiniteBentoBoxOutsideIsland() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(true);

        assertFalse(flightManager.canFlyInfinite(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightOutsideIslandNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, but the island's settings disallow flight.
     */
    @Test
    public void testCanFlyInfiniteBentoBoxIslandSettingsDisallowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(true);

        assertFalse(flightManager.canFlyInfinite(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightIslandNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, where BentoBox allows flight.
     */
    @Test
    public void testCanFlyInfiniteBentoBoxFlightAllowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(false);

        assertTrue(flightManager.canFlyInfinite(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, where WorldGuard disallows flight.
     */
    @Test
    public void testCanFlyInfiniteWorldGuardFlightDisallowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(true);
        when(worldGuardHook.isFlightDisallowed(player, location)).thenReturn(true);

        assertFalse(flightManager.canFlyInfinite(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightWorldGuardNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFlyInfinite(Player, boolean)}, where WorldGuard allows flight.
     */
    @Test
    public void testCanFlyInfiniteWorldGuardFlightAllowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(true);
        when(worldGuardHook.isFlightDisallowed(player, location)).thenReturn(false);

        assertTrue(flightManager.canFlyInfinite(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but the plugin's settings are invalid.
     */
    @Test
    public void testCanFlyTimedInvalidSettings() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(null);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);

        assertFalse(flightManager.canFlyTimed(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.invalidPluginSettings()));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but the plugin's settings are invalid. The player is not messaged here.
     */
    @Test
    public void testCanFlyTimedInvalidSettingsNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(null);
        Player player = mock(Player.class);

        assertFalse(flightManager.canFlyTimed(player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but the player has invalid player data..
     */
    @Test
    public void testCanFlyTimedInvalidPlayerData() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);

        assertFalse(flightManager.canFlyTimed(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.invalidPlayerData()));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)} where the player is an operator and has flight time.
     */
    @Test
    public void testCanFlyTimedPlayerOpFlightTime() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(true);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        assertTrue(flightManager.canFlyTimed(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)} where the player is an operator, but has no flight time.
     */
    @Test
    public void testCanFlyTimedPlayerOpNoFlightTime() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(true);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        assertFalse(flightManager.canFlyTimed(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightNoFlightTime()));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but the player doesn't have permission for timed flight.
     */
    @Test
    public void testCanFlyTimedNoFlightPermission() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);

        assertFalse(flightManager.canFlyTimed(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightNoPermission()));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but the player doesn't have permission for timed flight.
     * The player is not messaged here.
     */
    @Test
    public void testCanFlyTimedNoFlightPermissionNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);

        assertFalse(flightManager.canFlyTimed(player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but the player doesn't have any flight time.
     */
    @Test
    public void testCanFlyTimedNoFlightTime() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);

        assertFalse(flightManager.canFlyTimed(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightNoFlightTime()));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but the player doesn't have any flight time.
     * The player is not messaged here.
     */
    @Test
    public void testCanFlyTimedNoFlightTimeNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);

        assertFalse(flightManager.canFlyTimed(player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but the player has the bypass permission.
     */
    @Test
    public void testCanFlyTimedHasBypassPermission() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(true);

        assertTrue(flightManager.canFlyTimed(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but the world the player is in doesn't allow flight.
     */
    @Test
    public void testCanFlyTimedWorldNotAllowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world");

        assertFalse(flightManager.canFlyTimed(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightWorldNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but the world the player is in doesn't allow flight.
     * The player is not messaged here.
     */
    @Test
    public void testCanFlyTimedWorldNotAllowedNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world");

        assertFalse(flightManager.canFlyTimed(player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but BentoBox isn't hooked into.
     */
    @Test
    public void testCanFlyTimedBentoBoxNotHooked() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(false);

        assertTrue(flightManager.canFlyTimed(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but the player isn't on an island.
     */
    @Test
    public void testCanFlyTimedBentoBoxWorldNotGameModeWorld() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(false);

        assertTrue(flightManager.canFlyTimed(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but the player isn't on an island.
     */
    @Test
    public void testCanFlyTimedBentoBoxNoIsland() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(null);

        assertFalse(flightManager.canFlyTimed(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightOutsideIslandNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but the player is outside the island's protection range.
     */
    @Test
    public void testCanFlyTimedBentoBoxOutsideIsland() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(true);

        assertFalse(flightManager.canFlyTimed(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightOutsideIslandNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, but the island's settings disallow flight.
     */
    @Test
    public void testCanFlyTimedBentoBoxIslandSettingsDisallowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(true);

        assertFalse(flightManager.canFlyTimed(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightIslandNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, where BentoBox allows flight.
     */
    @Test
    public void testCanFlyTimedBentoBoxFlightAllowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(false);

        assertTrue(flightManager.canFlyTimed(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, where WorldGuard disallows flight.
     */
    @Test
    public void testCanFlyTimedWorldGuardFlightDisallowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(true);
        when(worldGuardHook.isFlightDisallowed(player, location)).thenReturn(true);

        assertFalse(flightManager.canFlyTimed(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightWorldGuardNotAllowed()));
    }

    /**
     * Test {@link FlightManager#canFlyTimed(Player, boolean)}, where WorldGuard allows flight.
     */
    @Test
    public void testCanFlyTimedWorldGuardFlightAllowed() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(settingsManager.getConfiguration()).thenReturn(settings);
        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);
        when(player.isOp()).thenReturn(false);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(true);
        when(worldGuardHook.isFlightDisallowed(player, location)).thenReturn(false);

        assertTrue(flightManager.canFlyTimed(player, true));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#enableFlight(Player, boolean)} for infinite flight.
     */
    @Test
    public void testEnableFlightInfiniteFlight() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);

        assertTrue(flightManager.enableFlight(player, true));
    }

    /**
     * Test {@link FlightManager#enableFlight(Player, boolean)} for timed flight.
     */
    @Test
    public void testEnableFlightTimedFlight() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(true);

        assertTrue(flightManager.enableFlight(player, true));
    }

    /**
     * Test {@link FlightManager#enableFlight(Player, boolean)}, but the player doesn't have permission for infinite or timed flight.
     */
    @Test
    public void testEnableFlightNoPermission() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);

        assertFalse(flightManager.enableFlight(player, true));
    }

    /**
     * Test {@link FlightManager#enableInfiniteFlight(Player, boolean)}.
     */
    @Test
    public void testEnableInfiniteFlight() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);

        assertTrue(flightManager.enableInfiniteFlight(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightEnabled()));
        verify(bossBarManager).showInfiniteBossBar(player);
        verify(player).setAllowFlight(true);
    }

    /**
     * Test {@link FlightManager#enableInfiniteFlight(Player, boolean)}.
     * The player isn't messaged here.
     */
    @Test
    public void testEnableInfiniteFlightNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);

        assertTrue(flightManager.enableInfiniteFlight(player, false));
        verify(player, never()).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightEnabled()));
        verify(bossBarManager).showInfiniteBossBar(player);
        verify(player).setAllowFlight(true);
    }

    /**
     * Test {@link FlightManager#enableTimedFlight(Player, boolean)}, but the player lacks player data.
     */
    @Test
    public void testEnableTimedFlightInvalidPlayerData() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);

        assertFalse(flightManager.enableTimedFlight(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.invalidPlayerData()));
    }

    /**
     * Test {@link FlightManager#enableTimedFlight(Player, boolean)}, but the player lacks player data.
     * The player isn't messaged here.
     */
    @Test
    public void testEnableTimedFlightInvalidPlayerDataNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);

        assertFalse(flightManager.enableTimedFlight(player, false));
        verify(player, never()).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.invalidPlayerData()));
    }

    /**
     * Test {@link FlightManager#enableTimedFlight(Player, boolean)}.
     */
    @Test
    public void testEnableTimedFlight() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        assertTrue(flightManager.enableTimedFlight(player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightEnabled()));
        assertTrue(playerData.isTimedFlight());
        verify(bossBarManager).showTimeBossBar(player);
        verify(player).setAllowFlight(true);
    }

    /**
     * Test {@link FlightManager#enableTimedFlight(Player, boolean)}.
     * The player isn't messaged here.
     */
    @Test
    public void testEnableTimedFlightNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        assertTrue(flightManager.enableTimedFlight(player, false));
        verify(player, never()).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightEnabled()));
        assertTrue(playerData.isTimedFlight());
        verify(bossBarManager).showTimeBossBar(player);
        verify(player).setAllowFlight(true);
    }

    /**
     * Test {@link FlightManager#disableFlightWithDelay(Player, int)},b ut the player can fly again.
     */
    @Execution(ExecutionMode.SAME_THREAD)
    @Test
    public void testDisableFlightWithDelayCanFlyAgain() throws ExecutionException, InterruptedException {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ServerMock server = MockBukkitExtension.getServer();
        BukkitSchedulerMock scheduler = server.getScheduler();
        when(skyFlight.getServer()).thenReturn(server);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        when(player.isOnline()).thenReturn(true);
        when(player.isConnected()).thenReturn(true);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(true);
        when(player.hasPermission("skyflight.fly.timed")).thenReturn(false);
        when(player.hasPermission("skyflight.fly.bypass")).thenReturn(false);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(world.getName()).thenReturn("world1");
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(false);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(true);
        when(worldGuardHook.isFlightDisallowed(player, location)).thenReturn(false);

        CompletableFuture<Boolean> future = flightManager.disableFlightWithDelay(player, 5);
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightDisabledDelay()));

        scheduler.performTicks(20L * 6);

        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightNotDisabled()));
        assertFalse(future.get());
    }

    /**
     * Test {@link FlightManager#disableFlightWithDelay(Player, int)}, but the player is offline.
     */
    @Execution(ExecutionMode.SAME_THREAD)
    @Test
    public void testDisableFlightWithDelayPlayerOffline() throws ExecutionException, InterruptedException {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ServerMock server = MockBukkitExtension.getServer();
        BukkitSchedulerMock scheduler = server.getScheduler();
        when(skyFlight.getServer()).thenReturn(server);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        when(player.isOnline()).thenReturn(false);

        CompletableFuture<Boolean> future = flightManager.disableFlightWithDelay(player, 5);

        scheduler.performTicks(20L * 6);

        assertFalse(future.get());
    }

    /**
     * Test {@link FlightManager#disableFlightWithDelay(Player, int)}, but the player is not connected.
     */
    @Execution(ExecutionMode.SAME_THREAD)
    @Test
    public void testDisableFlightWithDelayPlayerNotConnected() throws ExecutionException, InterruptedException {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ServerMock server = MockBukkitExtension.getServer();
        BukkitSchedulerMock scheduler = server.getScheduler();
        when(skyFlight.getServer()).thenReturn(server);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        when(player.isOnline()).thenReturn(true);
        when(player.isConnected()).thenReturn(false);

        CompletableFuture<Boolean> future = flightManager.disableFlightWithDelay(player, 5);

        scheduler.performTicks(20L * 6);

        assertFalse(future.get());
    }

    /**
     * Test {@link FlightManager#disableFlightWithDelay(Player, int)}, and the player's flight is disabled.
     */
    @Execution(ExecutionMode.SAME_THREAD)
    @Test
    public void testDisableFlightWithDelayDisableFlight() throws ExecutionException, InterruptedException {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ServerMock server = MockBukkitExtension.getServer();
        BukkitSchedulerMock scheduler = server.getScheduler();
        when(skyFlight.getServer()).thenReturn(server);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        when(player.isOnline()).thenReturn(true);
        when(player.isConnected()).thenReturn(true);
        when(player.isOp()).thenReturn(false);
        when(player.hasPermission("skyflight.fly.infinite")).thenReturn(false);

        CompletableFuture<Boolean> future = flightManager.disableFlightWithDelay(player, 5);
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightDisabledDelay()));

        scheduler.performTicks(20L * 6);

        assertTrue(future.get());

        verify(player).setAllowFlight(false);
        verify(player).setFlying(false);
        verify(player).setFallDistance(0);
        verify(bossBarManager).removeBossBar(player);
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightDisabled()));
    }

    /**
     * Test {@link FlightManager#disableFlight(Player, boolean)}.
     */
    @Test
    public void testDisableFlight() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        assertTrue(flightManager.disableFlight(player, true));
        assertFalse(playerData.isTimedFlight());
        verify(player).setAllowFlight(false);
        verify(player).setFlying(false);
        verify(player).setFallDistance(0);
        verify(bossBarManager).removeBossBar(player);
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightDisabled()));
    }

    /**
     * Test {@link FlightManager#disableFlight(Player, boolean)}.
     * The player isn't messaged here.
     */
    @Test
    public void testDisableFlightNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        assertTrue(flightManager.disableFlight(player, false));
        assertFalse(playerData.isTimedFlight());
        verify(player).setAllowFlight(false);
        verify(player).setFlying(false);
        verify(player).setFallDistance(0);
        verify(bossBarManager).removeBossBar(player);
        verify(player, never()).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightDisabled()));
    }

    /**
     * Test {@link FlightManager#disableFlight(Player, boolean)}, but the player lacks valid player data.
     */
    @Test
    public void testDisableFlightInvalidPlayerData() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);

        assertTrue(flightManager.disableFlight(player, true));
        verify(player).setAllowFlight(false);
        verify(player).setFlying(false);
        verify(player).setFallDistance(0);
        verify(bossBarManager).removeBossBar(player);
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightDisabled()));
    }

    /**
     * Test {@link FlightManager#isWorldAllowed(Settings, Player, boolean)}, but the plugin's settings are invalid.
     */
    @Test
    public void testIsWorldAllowedInvalidSettings() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);

        assertFalse(flightManager.isWorldAllowed(null, player, true));
        verify(player).sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.flightWorldNotAllowed()));
    }

    /**
     * Test {@link FlightManager#isWorldAllowed(Settings, Player, boolean)}, but the plugin's settings are invalid.
     * The player is not messaged here.
     */
    @Test
    public void testIsWorldAllowedInvalidSettingsNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        when(localeManager.getConfiguration()).thenReturn(locale);
        Player player = mock(Player.class);

        assertFalse(flightManager.isWorldAllowed(null, player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#isBentoBoxAllowed(Player, boolean)}, but there is no island at the player's location.
     * The player is not messaged here.
     */
    @Test
    public void testIsBentoBoxAllowedNoIslandNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(null);

        assertFalse(flightManager.isBentoBoxAllowed(player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#isBentoBoxAllowed(Player, boolean)}, but the player is outside the island's protection range.no island at the player's location.
     * The player is not messaged here.
     */
    @Test
    public void testIsBentoBoxAllowedOutsideIslandNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(true);

        assertFalse(flightManager.isBentoBoxAllowed(player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#isBentoBoxAllowed(Player, boolean)}, but the island disallows the player to fly.
     * The player is not messaged here.
     */
    @Test
    public void testIsBentoBoxAllowedIslandSettingsDisallowsFlyNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        BentoBoxHook bentoBoxHook = mock(BentoBoxHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        World world = mock(World.class);
        when(player.getWorld()).thenReturn(world);
        when(hookManager.getHook(BentoBoxHook.class)).thenReturn(bentoBoxHook);
        when(bentoBoxHook.isHooked()).thenReturn(true);
        when(bentoBoxHook.isGameModeWorld(world)).thenReturn(true);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);
        Island island = mock(Island.class);
        when(bentoBoxHook.getIslandAtLocation(location)).thenReturn(island);
        when(bentoBoxHook.isOutsideIsland(player, island)).thenReturn(false);
        when(bentoBoxHook.isFlightDisallowed(player, island)).thenReturn(true);

        assertFalse(flightManager.isBentoBoxAllowed(player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#isWorldGuardAllowed(Player, boolean)}, but flight is disallowed.
     * The player is not messaged here.
     */
    @Test
    public void testIsWorldGuardAllowedDisallowedNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        WorldGuardHook worldGuardHook = mock(WorldGuardHook.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        Location location = mock(Location.class);
        when(player.getLocation()).thenReturn(location);

        when(hookManager.getHook(WorldGuardHook.class)).thenReturn(worldGuardHook);
        when(worldGuardHook.isHooked()).thenReturn(true);
        when(worldGuardHook.isFlightDisallowed(player, location)).thenReturn(true);

        assertFalse(flightManager.isWorldGuardAllowed(player, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#isPlayerDataValid(Player, PlayerData, boolean)}, but the player data is null.
     * The player is not messaged here.
     */
    @Test
    public void testIsPlayerDataValidNullPlayerDataNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);

        assertFalse(flightManager.isPlayerDataValid(player, null, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#hasFlightTime(Player, PlayerData, boolean)}, but the player data is null.
     * The player is not messaged here.
     */
    @Test
    public void testHasFlightTimeNullPlayerDataNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);

        assertFalse(flightManager.hasFlightTime(player, null, false));
        verify(player, never()).sendMessage(any(Component.class));
    }

    /**
     * Test {@link FlightManager#hasFlightTime(Player, PlayerData, boolean)}, but the player has no flight time.
     * The player is not messaged here.
     */
    @Test
    public void testHasFlightTimeNoFlightTimeNoMessage() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        HookManager hookManager = mock(HookManager.class);
        FlightManager flightManager = new FlightManager(skyFlight, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        Player player = mock(Player.class);
        PlayerData playerData = new PlayerData(UUID.randomUUID());

        assertFalse(flightManager.hasFlightTime(player, playerData, false));
        verify(player, never()).sendMessage(any(Component.class));
    }
}