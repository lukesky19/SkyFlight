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
package com.github.lukesky19.skyFlight.settings;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.github.lukesky19.skyFlight.SkyFlight;
import com.github.lukesky19.skyFlight.common.TestUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests the {@link SettingsManager} class.
 */
@ExtendWith(MockitoExtension.class)
public class SettingsManagerTest {
    @Mock
    private SkyFlight skyFlight;
    @Mock
    private ComponentLogger logger;

    /**
     * Setup data required for each test.
     * @param testInfo The {@link TestInfo}.
     */
    @BeforeEach
    public void beforeEach(@NotNull TestInfo testInfo) {
        // Get the name of the test method and create a unique folder for it
        String displayName = testInfo.getDisplayName();
        displayName = displayName.replace("(", "");
        displayName = displayName.replace(")", "");
        displayName = displayName.replaceAll("[^a-zA-Z0-9]", "_");
        displayName = displayName.replace("TestInfo", "");

        // Intercept data folder requests
        when(skyFlight.getDirectoryFile()).thenReturn(new File("test_data_" + this.getClass().getName() + "_" + displayName));
        when(skyFlight.getComponentLogger()).thenReturn(logger);
    }

    /**
     * Test the creation of a settings manager class.
     */
    @Test
    public void testConstructor() {
        SettingsManager settingsManager = new SettingsManager(skyFlight);

        assertNotNull(settingsManager);
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
        File file = new File("test_data_" + this.getClass().getName() + "_" + displayName);
        file.mkdirs();

        // Intercepting the call and simulating the real method's behavior without real execution
        doAnswer(invocation -> {
            String resourceName = invocation.getArgument(0);
            File output = Path.of(file.toPath() + File.separator + resourceName).toFile();

            TestUtils.saveResource(this.getClass(), resourceName, output);

            return null;
        }).when(skyFlight).saveResource(anyString(), anyBoolean());

        SettingsManager settingsManager = new SettingsManager(skyFlight);
        settingsManager.loadConfiguration();

        verify(logger, never()).warn(any(Component.class));
        verify(logger, never()).error(any(Component.class));
        assertNotNull(settingsManager.getConfiguration());
    }

    /**
     * Since no migration currently occurs, test that the two settings are the same.
     */
    @Test
    public void testMigrateConfiguration() {
        SettingsManager settingsManager = new SettingsManager(skyFlight);
        Settings settings = new Settings(
                "1.0.0.0",
                "en_US",
                List.of(),
                new Settings.BossBarConfig("Flight Enabled", BossBar.Color.BLUE, BossBar.Overlay.PROGRESS),
                new Settings.BossBarConfig("Flight Enabled | Time: <time>", BossBar.Color.RED, BossBar.Overlay.PROGRESS));

        Settings migratedSettings = settingsManager.migrateConfiguration(settings);

        assertEquals(settings, migratedSettings);
    }

    /**
     * Test that the configuration is valid.
     */
    @Test
    public void testValidateConfigurationValid() {
        SettingsManager settingsManager = new SettingsManager(skyFlight);
        Settings settings = new Settings(
                "1.0.0.0",
                "en_US",
                List.of(),
                new Settings.BossBarConfig("Flight Enabled", BossBar.Color.BLUE, BossBar.Overlay.PROGRESS),
                new Settings.BossBarConfig("Flight Enabled | Time: <time>", BossBar.Color.RED, BossBar.Overlay.PROGRESS));

        assertTrue(settingsManager.validateConfiguration(settings));
    }

    /**
     * Test that the configuration is invalid.
     */
    @Test
    public void testValidateConfigurationInvalid() {
        SettingsManager settingsManager = new SettingsManager(skyFlight);

        assertFalse(settingsManager.validateConfiguration(null));
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
        File[] files = directory.listFiles();
        if(files != null) {
            for(File file : files) {
                file.delete();
            }
        }
        directory.delete();
    }
}