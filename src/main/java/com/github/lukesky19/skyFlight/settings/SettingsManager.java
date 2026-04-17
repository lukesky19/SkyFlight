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

import com.github.lukesky19.skylib.common.api.configuration.abstracts.SimpleConfigManager;
import com.github.lukesky19.skylib.paper.api.plugin.SkyPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

/**
 * This class manages the plugin's settings.
 */
public class SettingsManager extends SimpleConfigManager<Settings> {
    /**
     * Constructor
     * @param plugin A {@link SkyPlugin}.
     */
    public SettingsManager(@NotNull SkyPlugin plugin) {
        super(plugin, Path.of(plugin.getDirectoryFile() + File.separator + "settings.yml"), Settings.class);
    }

    @Override
    public @Nullable Settings migrateConfiguration(@NotNull Settings settings) {
        return settings;
    }

    @Override
    public boolean validateConfiguration(@Nullable Settings settings) {
        return settings != null;
    }

    @Override
    public void saveDefaultConfiguration() {
        plugin.saveResource("settings.yml", false);
    }
}