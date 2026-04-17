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
package com.github.lukesky19.skyFlight.bossbar;

import com.github.lukesky19.skyFlight.SkyFlight;
import com.github.lukesky19.skyFlight.locale.Locale;
import com.github.lukesky19.skyFlight.locale.LocaleManager;
import com.github.lukesky19.skyFlight.player.PlayerData;
import com.github.lukesky19.skyFlight.player.PlayerDataManager;
import com.github.lukesky19.skyFlight.settings.Settings;
import com.github.lukesky19.skyFlight.settings.SettingsManager;
import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests {@link BossBarManager}.
 */
@ExtendWith(MockitoExtension.class)
public class BossBarManagerTest {
    private static Settings settings;
    private static Locale locale;
    private static MockedStatic<BossBar> bossBarMockedStatic;

    /**
     * Create the settings for the tests.
     */
    @BeforeAll
    public static void beforeAll() {
        bossBarMockedStatic = mockStatic(BossBar.class);

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
     * Cleanup after all tests.
     */
    @AfterAll
    public static void afterAll() {
        bossBarMockedStatic.close();
    }

    /**
     * Test {@link BossBarManager#showTimeBossBar(Player)}, but the plugin's settings are invalid.
     */
    @Test
    public void showTimeBossBarInvalidSettings() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);

        bossBarManager.showTimeBossBar(player);

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#showTimeBossBar(Player)}, but the plugin's settings are invalid.
     */
    @Test
    public void showTimeBossBarInvalidSettingsBossBarText() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        Settings settings = new Settings(
                "1.0.0.0",
                "en_US",
                List.of("world"),
                new Settings.BossBarConfig("Flight Enabled", BossBar.Color.BLUE, BossBar.Overlay.PROGRESS),
                new Settings.BossBarConfig(null, BossBar.Color.RED, BossBar.Overlay.PROGRESS));
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);

        bossBarManager.showTimeBossBar(player);

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#showTimeBossBar(Player)}, but the plugin's settings are invalid.
     */
    @Test
    public void showTimeBossBarInvalidSettingsBossBarColor() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        Settings settings = new Settings(
                "1.0.0.0",
                "en_US",
                List.of("world"),
                new Settings.BossBarConfig("Flight Enabled", BossBar.Color.BLUE, BossBar.Overlay.PROGRESS),
                new Settings.BossBarConfig("Flight Enabled | Time: <time>", null, BossBar.Overlay.PROGRESS));
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);

        bossBarManager.showTimeBossBar(player);

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#showTimeBossBar(Player)}, but the plugin's settings are invalid.
     */
    @Test
    public void showTimeBossBarInvalidSettingsBossBarOverlay() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        Settings settings = new Settings(
                "1.0.0.0",
                "en_US",
                List.of("world"),
                new Settings.BossBarConfig("Flight Enabled", BossBar.Color.BLUE, BossBar.Overlay.PROGRESS),
                new Settings.BossBarConfig("Flight Enabled | Time: <time>", BossBar.Color.RED, null));
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);

        bossBarManager.showTimeBossBar(player);

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#showTimeBossBar(Player)}, but the player's player data is invalid.
     */
    @Test
    public void showTimeBossBarInvalidPlayerData() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);

        bossBarManager.showTimeBossBar(player);

        verify(logger, never()).warn(any(Component.class));
        verify(player, never()).showBossBar(any(BossBar.class));
    }

    /**
     * Test {@link BossBarManager#showTimeBossBar(Player)}.
     */
    @Execution(ExecutionMode.SAME_THREAD)
    @Test
    public void testShowTimeBossBar() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        when(localeManager.formatFlightTime(locale.timeFormat(), 100)).thenReturn("<yellow><minutes></yellow> minute(s) <yellow><seconds></yellow> second(s)");
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(100);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        BossBar bossBar = mock(BossBar.class);
        bossBarMockedStatic.when(() -> {
            assertNotNull(settings.timedBossBar().bossBarText());

            BossBar.bossBar(
                    AdventureUtility.deserialize(settings.timedBossBar().bossBarText(),
                            List.of(Placeholder.parsed("time", localeManager.formatFlightTime(
                                    locale.timeFormat(), playerData.getFlightTime())))),
                    1,
                    BossBar.Color.RED,
                    BossBar.Overlay.PROGRESS);
        }).thenReturn(bossBar);

        bossBarManager.showTimeBossBar(player);

        verify(logger, never()).warn(any(Component.class));
        verify(bossBar).addViewer(player);
    }

    /**
     * Test {@link BossBarManager#showInfiniteBossBar(Player)}, but the plugin's settings are invalid.
     */
    @Test
    public void showInfiniteBossBarInvalidSettings() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);

        bossBarManager.showInfiniteBossBar(player);

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#showInfiniteBossBar(Player)}, but the plugin's settings are invalid.
     */
    @Test
    public void showInfiniteBossBarInvalidSettingsBossBarText() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        Settings settings = new Settings(
                "1.0.0.0",
                "en_US",
                List.of("world"),
                new Settings.BossBarConfig(null, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS),
                new Settings.BossBarConfig("Flight Enabled | Time: <time>", BossBar.Color.RED, BossBar.Overlay.PROGRESS));
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);

        bossBarManager.showInfiniteBossBar(player);

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#showInfiniteBossBar(Player)}, but the plugin's settings are invalid.
     */
    @Test
    public void showInfiniteBossBarInvalidSettingsBossBarColor() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        Settings settings = new Settings(
                "1.0.0.0",
                "en_US",
                List.of("world"),
                new Settings.BossBarConfig("Flight Enabled", null, BossBar.Overlay.PROGRESS),
                new Settings.BossBarConfig("Flight Enabled | Time: <time>", BossBar.Color.RED, BossBar.Overlay.PROGRESS));
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);

        bossBarManager.showInfiniteBossBar(player);

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#showInfiniteBossBar(Player)}, but the plugin's settings are invalid.
     */
    @Test
    public void showInfiniteBossBarInvalidSettingsBossBarOverlay() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        Settings settings = new Settings(
                "1.0.0.0",
                "en_US",
                List.of("world"),
                new Settings.BossBarConfig("Flight Enabled", BossBar.Color.BLUE, null),
                new Settings.BossBarConfig("Flight Enabled | Time: <time>", BossBar.Color.RED, BossBar.Overlay.PROGRESS));
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);

        bossBarManager.showInfiniteBossBar(player);

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#showInfiniteBossBar(Player)}.
     */
    @Execution(ExecutionMode.SAME_THREAD)
    @Test
    public void testShowInfiniteBossBar() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);

        BossBar bossBar = mock(BossBar.class);
        bossBarMockedStatic.when(() -> {
            assertNotNull(settings.infiniteBossBar().bossBarText());
            BossBar.bossBar(
                    AdventureUtility.deserialize(settings.infiniteBossBar().bossBarText()),
                    1,
                    BossBar.Color.BLUE,
                    BossBar.Overlay.PROGRESS);
        }).thenReturn(bossBar);

        bossBarManager.showInfiniteBossBar(player);

        verify(logger, never()).warn(any(Component.class));
        verify(bossBar).addViewer(player);
    }

    /**
     * Test {@link BossBarManager#updateBossBar(Player)}, but the plugin's settings are invalid.
     */
    @Test
    public void testUpdateBossBarInvalidSettings() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);

        assertFalse(bossBarManager.updateBossBar(player));

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#updateBossBar(Player)}, but the plugin's settings are invalid.
     */
    @Test
    public void testUpdateBossBarInvalidSettingsBossBarText() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        Settings settings = new Settings(
                "1.0.0.0",
                "en_US",
                List.of("world"),
                new Settings.BossBarConfig("Flight Enabled", BossBar.Color.BLUE, BossBar.Overlay.PROGRESS),
                new Settings.BossBarConfig(null, BossBar.Color.RED, BossBar.Overlay.PROGRESS));
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);

        assertFalse(bossBarManager.updateBossBar(player));

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#updateBossBar(Player)}, but the plugin's settings are invalid.
     */
    @Test
    public void testUpdateBossBarInvalidSettingsBossBarColor() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        Settings settings = new Settings(
                "1.0.0.0",
                "en_US",
                List.of("world"),
                new Settings.BossBarConfig("Flight Enabled", BossBar.Color.BLUE, BossBar.Overlay.PROGRESS),
                new Settings.BossBarConfig("Flight Enabled | Time: <time>", null, BossBar.Overlay.PROGRESS));
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);

        assertFalse(bossBarManager.updateBossBar(player));

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#updateBossBar(Player)}, but the plugin's settings are invalid.
     */
    @Test
    public void testUpdateBossBarInvalidSettingsBossBarOverlay() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        Settings settings = new Settings(
                "1.0.0.0",
                "en_US",
                List.of("world"),
                new Settings.BossBarConfig("Flight Enabled", BossBar.Color.BLUE, BossBar.Overlay.PROGRESS),
                new Settings.BossBarConfig("Flight Enabled | Time: <time>", BossBar.Color.RED, null));
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);

        assertFalse(bossBarManager.updateBossBar(player));

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#updateBossBar(Player)}, but the player has no player data.
     */
    @Test
    public void testUpdateBossBarInvalidPlayerData() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);

        assertFalse(bossBarManager.updateBossBar(player));

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#updateBossBar(Player)}, but the player isn't using timed flight.
     */
    @Test
    public void testUpdateBossBarNotTimedFlight() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        assertFalse(bossBarManager.updateBossBar(player));

        verify(logger, never()).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#updateBossBar(Player)}, but the player doesn't have a boss bar to update.
     */
    @Test
    public void testUpdateBossBarNoBossBar() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setTimedFlight(true);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        assertFalse(bossBarManager.updateBossBar(player));

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#updateBossBar(Player)}.
     */
    @Test
    public void testUpdateBossBar() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        when(settingsManager.getConfiguration()).thenReturn(settings);
        LocaleManager localeManager = mock(LocaleManager.class);
        when(localeManager.getConfiguration()).thenReturn(locale);
        when(localeManager.formatFlightTime(locale.timeFormat(), 0)).thenReturn("<yellow><seconds></yellow> second(s)");
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setTimedFlight(true);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        BossBar bossBar = mock(BossBar.class);
        bossBarManager.setBossBar(playerId, bossBar);

        assertTrue(bossBarManager.updateBossBar(player));

        verify(logger, never()).warn(any(Component.class));
        verify(bossBar).name(any(Component.class));
    }

    /**
     * Test {@link BossBarManager#removeBossBar(Player)}, but the player doesn't have a boss bar.
     */
    @Test
    public void testRemoveBossBarNoBossBar() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);

        assertFalse(bossBarManager.removeBossBar(player));
    }

    /**
     * Test {@link BossBarManager#removeBossBar(Player)}.
     */
    @Test
    public void testRemoveBossBar() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        SettingsManager settingsManager = mock(SettingsManager.class);
        LocaleManager localeManager = mock(LocaleManager.class);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = new BossBarManager(skyFlight, settingsManager, localeManager, playerDataManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);

        BossBar bossBar = mock(BossBar.class);
        bossBarManager.setBossBar(playerId, bossBar);

        assertTrue(bossBarManager.removeBossBar(player));
    }
}