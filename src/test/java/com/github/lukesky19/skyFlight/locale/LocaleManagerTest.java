package com.github.lukesky19.skyFlight.locale;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.github.lukesky19.skyFlight.SkyFlight;
import com.github.lukesky19.skyFlight.common.TestUtils;
import com.github.lukesky19.skyFlight.settings.Settings;
import com.github.lukesky19.skyFlight.settings.SettingsManager;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * This class tests the {@link LocaleManager} class.
 */
@ExtendWith(MockitoExtension.class)
public class LocaleManagerTest {
    @Mock
    private SkyFlight skyFlight;
    @Mock
    private ComponentLogger logger;
    @Mock
    private SettingsManager settingsManager;

    /**
     * Setup data required for each test.
     */
    @BeforeEach
    public void beforeEach() {
        when(skyFlight.getComponentLogger()).thenReturn(logger);
    }

    /**
     * Test the creation of a locale manager class.
     */
    @Test
    public void testConstructor() {
        LocaleManager localeManager = new LocaleManager(skyFlight, settingsManager);

        assertNotNull(localeManager);
    }

    /**
     * Test that the loading of the configuration works properly.
     * @param testInfo The {@link TestInfo}.
     */
    @Test
    public void testLoadConfiguration(@NotNull TestInfo testInfo) {
        // Get the name of the test method and create a unique folder for it
        String displayName = testInfo.getDisplayName();
        displayName = displayName.replace("(", "");
        displayName = displayName.replace(")", "");
        displayName = displayName.replaceAll("[^a-zA-Z0-9]", "_");
        displayName = displayName.replace("TestInfo", "");
        File dataFolder = new File("test_data_" + this.getClass().getName() + "_" + displayName);
        File localeFolder = new File(dataFolder.getPath() + File.separator + "locale");
        localeFolder.mkdirs();

        // Intercept data folder requests
        when(skyFlight.getDataFolder()).thenReturn(dataFolder);

        // Intercepting the call and simulating the real method's behavior without real execution
        doAnswer(invocation -> {
            String resourceName = invocation.getArgument(0);
            File output = Path.of(dataFolder.toPath() + File.separator + resourceName).toFile();

            TestUtils.saveResource(this.getClass(), resourceName, output);

            return null;
        }).when(skyFlight).saveResource(anyString(), anyBoolean());

        Settings settings = new Settings(
                "1.0.0.0",
                "en_US",
                List.of(),
                new Settings.BossBarConfig("Flight Enabled", BossBar.Color.BLUE, BossBar.Overlay.PROGRESS),
                new Settings.BossBarConfig("Flight Enabled | Time: <time>", BossBar.Color.RED, BossBar.Overlay.PROGRESS));
        when(settingsManager.getConfiguration()).thenReturn(settings);

        LocaleManager localeManager = new LocaleManager(skyFlight, settingsManager);
        localeManager.loadConfiguration();

        verify(logger, never()).info(any(Component.class));
        verify(logger, never()).warn(any(Component.class));
        verify(logger, never()).error(any(Component.class));
        assertNotNull(localeManager.getConfiguration());
    }

    /**
     * Since no migration currently occurs, test that the two locales are the same.
     */
    @Test
    public void testMigrateConfiguration() {
        LocaleManager localeManager = new LocaleManager(skyFlight, settingsManager);
        Locale locale = new Locale(
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

        Locale migratedLocale = localeManager.migrateConfiguration(locale);

        assertEquals(locale, migratedLocale);
    }

    /**
     * Test that the configuration is valid.
     * @param testInfo The {@link TestInfo}.
     */
    @Test
    public void testValidateConfigurationValid(@NotNull TestInfo testInfo) {
        // Get the name of the test method and create a unique folder for it
        String displayName = testInfo.getDisplayName();
        displayName = displayName.replace("(", "");
        displayName = displayName.replace(")", "");
        displayName = displayName.replaceAll("[^a-zA-Z0-9]", "_");
        displayName = displayName.replace("TestInfo", "");
        File dataFolder = new File("test_data_" + this.getClass().getName() + "_" + displayName);
        File localeFolder = new File(dataFolder.getPath() + File.separator + "locale");
        localeFolder.mkdirs();

        // Intercept data folder requests
        when(skyFlight.getDataFolder()).thenReturn(dataFolder);

        // Intercepting the call and simulating the real method's behavior without real execution
        doAnswer(invocation -> {
            String resourceName = invocation.getArgument(0);
            File output = Path.of(dataFolder.toPath() + File.separator + resourceName).toFile();

            TestUtils.saveResource(this.getClass(), resourceName, output);

            return null;
        }).when(skyFlight).saveResource(anyString(), anyBoolean());

        Settings settings = new Settings(
                "1.0.0.0",
                "en_US",
                List.of(),
                new Settings.BossBarConfig("Flight Enabled", BossBar.Color.BLUE, BossBar.Overlay.PROGRESS),
                new Settings.BossBarConfig("Flight Enabled | Time: <time>", BossBar.Color.RED, BossBar.Overlay.PROGRESS));
        when(settingsManager.getConfiguration()).thenReturn(settings);

        LocaleManager localeManager = new LocaleManager(skyFlight, settingsManager);
        localeManager.loadConfiguration();

        assertTrue(localeManager.validateConfiguration(localeManager.getConfiguration()));
    }

    /**
     * Test that the configuration is invalid.
     */
    @Test
    public void testValidateConfigurationInvalid() {
        LocaleManager localeManager = new LocaleManager(skyFlight, settingsManager);
        Locale invalidLocale1 = new Locale(
                null,
                null,
                null,
                List.of(),
                List.of(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale2 = new Locale(
                "1.0.0.0",
                null,
                null,
                List.of(),
                List.of(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale3 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                null,
                List.of(),
                List.of(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale4 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale5 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
                "<#FF4D4D>The plugin's settings are invalid. Please report the issue to your system administrator.</#FF4D4D>",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale6 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
                "<#FF4D4D>The plugin's settings are invalid. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>Your player lacks any player data. Please report the issue to your system administrator.</#FF4D4D>",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale7 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
                "<#FF4D4D>The plugin's settings are invalid. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>Your player lacks any player data. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>You do not have permission to fly.</#FF4D4D>",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale8 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
                "<#FF4D4D>The plugin's settings are invalid. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>Your player lacks any player data. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>You do not have permission to fly.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly in this world.</#FF4D4D>",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale9 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
                "<#FF4D4D>The plugin's settings are invalid. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>Your player lacks any player data. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>You do not have permission to fly.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly in this world.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly on this island.</#FF4D4D>",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale10 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
                "<#FF4D4D>The plugin's settings are invalid. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>Your player lacks any player data. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>You do not have permission to fly.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly in this world.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly on this island.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly outside of islands in this world.</#FF4D4D>",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale11 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
                "<#FF4D4D>The plugin's settings are invalid. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>Your player lacks any player data. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>You do not have permission to fly.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly in this world.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly on this island.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly outside of islands in this world.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly here.</#FF4D4D>",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale12 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
                "<#FF4D4D>The plugin's settings are invalid. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>Your player lacks any player data. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>You do not have permission to fly.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly in this world.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly on this island.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly outside of islands in this world.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly here.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly due to lack of flight time.</#FF4D4D>",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale13 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
                "<#FF4D4D>The plugin's settings are invalid. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>Your player lacks any player data. Please report the issue to your system administrator.</#FF4D4D>",
                "<#FF4D4D>You do not have permission to fly.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly in this world.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly on this island.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly outside of islands in this world.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly here.</#FF4D4D>",
                "<#FF4D4D>You are not allowed to fly due to lack of flight time.</#FF4D4D>",
                "<#FFA756>Your flight has been enabled.</#FFA756>",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale14 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale15 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale16 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale17 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale18 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale19 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                null,
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale20 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                null,
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale21 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                null,
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale22 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                null,
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale23 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                null,
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale24 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                null,
                new Locale.TimeFormat(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale25 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale26 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale27 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale28 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale29 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                        null,
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale30 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                        null,
                        null,
                        null,
                        null));
        Locale invalidLocale31 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                        null,
                        null,
                        null));
        Locale invalidLocale32 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                        null,
                        null));
        Locale invalidLocale33 = new Locale(
                "1.0.0.0",
                "<#D56600><bold>SkyFlight</bold></#D56600><gray> ▪ </gray>",
                "<#FFA756>The plugin has been reloaded.</#FFA756>",
                List.of(),
                List.of(),
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
                        null));

        assertFalse(localeManager.validateConfiguration(invalidLocale1));
        assertFalse(localeManager.validateConfiguration(invalidLocale2));
        assertFalse(localeManager.validateConfiguration(invalidLocale3));
        assertFalse(localeManager.validateConfiguration(invalidLocale4));
        assertFalse(localeManager.validateConfiguration(invalidLocale5));
        assertFalse(localeManager.validateConfiguration(invalidLocale6));
        assertFalse(localeManager.validateConfiguration(invalidLocale7));
        assertFalse(localeManager.validateConfiguration(invalidLocale8));
        assertFalse(localeManager.validateConfiguration(invalidLocale9));
        assertFalse(localeManager.validateConfiguration(invalidLocale10));
        assertFalse(localeManager.validateConfiguration(invalidLocale11));
        assertFalse(localeManager.validateConfiguration(invalidLocale12));
        assertFalse(localeManager.validateConfiguration(invalidLocale13));
        assertFalse(localeManager.validateConfiguration(invalidLocale14));
        assertFalse(localeManager.validateConfiguration(invalidLocale15));
        assertFalse(localeManager.validateConfiguration(invalidLocale16));
        assertFalse(localeManager.validateConfiguration(invalidLocale17));
        assertFalse(localeManager.validateConfiguration(invalidLocale18));
        assertFalse(localeManager.validateConfiguration(invalidLocale19));
        assertFalse(localeManager.validateConfiguration(invalidLocale20));
        assertFalse(localeManager.validateConfiguration(invalidLocale21));
        assertFalse(localeManager.validateConfiguration(invalidLocale22));
        assertFalse(localeManager.validateConfiguration(invalidLocale23));
        assertFalse(localeManager.validateConfiguration(invalidLocale24));
        assertFalse(localeManager.validateConfiguration(invalidLocale25));
        assertFalse(localeManager.validateConfiguration(invalidLocale26));
        assertFalse(localeManager.validateConfiguration(invalidLocale27));
        assertFalse(localeManager.validateConfiguration(invalidLocale28));
        assertFalse(localeManager.validateConfiguration(invalidLocale29));
        assertFalse(localeManager.validateConfiguration(invalidLocale30));
        assertFalse(localeManager.validateConfiguration(invalidLocale31));
        assertFalse(localeManager.validateConfiguration(invalidLocale32));
        assertFalse(localeManager.validateConfiguration(invalidLocale33));
    }

    @Test
    public void testSaveBundledConfig(@NotNull TestInfo testInfo) {
        // Get the name of the test method and create a unique folder for it
        String displayName = testInfo.getDisplayName();
        displayName = displayName.replace("(", "");
        displayName = displayName.replace(")", "");
        displayName = displayName.replaceAll("[^a-zA-Z0-9]", "_");

        // Intercept data folder requests
        when(skyFlight.getDataFolder()).thenReturn(new File("test_data_" + this.getClass().getName() + "_" + displayName));

        LocaleManager localeManager = new LocaleManager(skyFlight, settingsManager);
        localeManager.saveBundledConfig();
    }

    /**
     * Cleanup data after each test.
     * @param testInfo The {@link TestInfo}.
     */
    @AfterEach
    public void afterEach(@NotNull TestInfo testInfo) {
        String displayName = testInfo.getDisplayName();
        displayName = displayName.replace("(", "");
        displayName = displayName.replace(")", "");
        displayName = displayName.replaceAll("[^a-zA-Z0-9]", "_");
        displayName = displayName.replace("TestInfo", "");

        File directory = new File("test_data_" + this.getClass().getName() + "_" + displayName);
        deleteFiles(directory.listFiles());
        directory.delete();
    }

    /**
     * Loop through the array of {@link File} and delete recursively if a directory.
     * @param files The array of {@link File}.
     */
    private void deleteFiles(@Nullable File[] files) {
        if(files == null) return;

        for(File file : files) {
            if(file.isDirectory()) {
                deleteFiles(file.listFiles());
            }

            file.delete();
        }
    }
}