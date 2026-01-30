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
package com.github.lukesky19.skyFlight.locale;

import com.github.lukesky19.skyFlight.settings.SettingsManager;
import com.github.lukesky19.skyFlight.settings.Settings;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.common.abstracts.SkyPlugin;
import com.github.lukesky19.skylib.api.common.abstracts.config.SimpleConfigManager;
import com.github.lukesky19.skylib.api.time.Time;
import com.github.lukesky19.skylib.api.time.TimeUtil;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * This class manages the plugin's locale.
 */
public class LocaleManager extends SimpleConfigManager<Locale> {
    private final @NotNull SimpleConfigManager<Settings> settingsManager;
    private @NotNull Locale DEFAULT_LOCALE;

    /**
     * Constructor
     *
     * @param plugin A {@link SkyPlugin}.
     * @param settingsManager A {@link SettingsManager} instance.
     */
    public LocaleManager(@NotNull SkyPlugin plugin, @NotNull SimpleConfigManager<Settings> settingsManager) {
        super(plugin, Locale.class);
        this.settingsManager = settingsManager;

        createDefaultLocale();
    }

    /**
     * Gets the plugin's locale if not null or the default locale otherwise.
     *
     * @return The plugin's locale if not null or the default locale otherwise.
     */
    @Override
    public @NotNull Locale getConfiguration() {
        if(configuration == null) return DEFAULT_LOCALE;
        return configuration;
    }

    @Override
    public void loadConfiguration() {
        Settings settings = settingsManager.getConfiguration();
        if (settings == null) {
            logger.error(AdventureUtil.deserialize("<red>Failed to load plugin's locale due to plugin settings being null.</red>"));
            return;
        }
        if (settings.locale() == null) {
            logger.error(AdventureUtil.deserialize("<red>Failed to load plugin's locale to use in settings.yml is null.</red>"));
            return;
        }

        String localeString = settings.locale();
        Path path = Path.of(plugin.getDataFolder() + File.separator + "locale" + File.separator + (localeString + ".yml"));
        setConfigurationPath(path);

        super.loadConfiguration();
    }

    @Override
    public void saveBundledConfig() {
        Path path = Path.of(plugin.getDataFolder() + File.separator + "locale" + File.separator + "en_US.yml");
        if(!path.toFile().exists()) {
            plugin.saveResource("locale" + File.separator + "en_US.yml", false);
        }
    }

    /**
     * There is currently no updates required so the locale passed is returned.
     * @param locale The {@link Locale} to migrate.
     * @return The original locale.
     */
    @Override
    public @NotNull Locale migrateConfiguration(@NotNull Locale locale) {
        return locale;
    }

    /**
     * Validates if the locale is missing any strings.
     * @param configuration The configuration to validate.
     */
    @Override
    public boolean validateConfiguration(@Nullable Locale configuration) {
        if(configuration == null) return false;

        if(configuration.configVersion() == null
                || configuration.prefix() == null
                || configuration.reload() == null
                || configuration.invalidPluginSettings() == null
                || configuration.invalidPlayerData() == null
                || configuration.flightNoPermission() == null
                || configuration.flightWorldNotAllowed() == null
                || configuration.flightIslandNotAllowed() == null
                || configuration.flightOutsideIslandNotAllowed() == null
                || configuration.flightWorldGuardNotAllowed() == null
                || configuration.flightNoFlightTime() == null
                || configuration.flightEnabled() == null
                || configuration.flightDisabled() == null
                || configuration.flightDisabledDelay() == null
                || configuration.flightNotDisabled() == null
                || configuration.flightTimeWarning() == null
                || configuration.flightTimeExhausted() == null
                || configuration.commandPlayerOnly() == null
                || configuration.timeInvalid() == null
                || configuration.flightTimeUpdated() == null
                || configuration.playerFlightTimeUpdated() == null
                || configuration.playerFlightTimeUpdateFailed() == null
                || configuration.flightTime() == null
                || configuration.playerFlightTime() == null
                || isTimeFormatInvalid(configuration.timeFormat())) {
            this.configuration = null;

            logger.error(AdventureUtil.deserialize("Your locale is missing one of the plugin's messages. The default locale will be used."));
            logger.info(AdventureUtil.deserialize("You can regenerate your locale file by deleting it or adding the missing messages to resolve the issue."));

            return false;
        }

        return true;
    }

    /**
     * Creates the default locale.
     * It is created in a separate method so that the method can be minimized.
     */
    private void createDefaultLocale() {
        DEFAULT_LOCALE = new Locale(
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
     * Checks if all {@link String}s in a {@link Locale.TimeFormat} are null.
     * @param timeFormat The {@link Locale.TimeFormat} to check.
     * @return true if invalid, false if not.
     */
    private boolean isTimeFormatInvalid(@NotNull Locale.TimeFormat timeFormat) {
        return timeFormat.prefix() == null
                || timeFormat.years() == null
                || timeFormat.months() == null
                || timeFormat.weeks() == null
                || timeFormat.days() == null
                || timeFormat.hours() == null
                || timeFormat.minutes() == null
                || timeFormat.seconds() == null
                || timeFormat.suffix() == null;
    }

    /**
     * Formats the number of seconds to a formatted message to display in a chat message.
     * If any value is 0, it won't be shown.
     * @param timeMessage The {@link Locale.TimeFormat} to use for formatting.
     * @param timeInSeconds The time in seconds to format to a message.
     * @return The time in seconds formatted to a {@link String}.
     */
    public @NotNull String formatFlightTime(@NotNull Locale.TimeFormat timeMessage, long timeInSeconds) {
        boolean firstUnit = true;
        Time timeRecord = TimeUtil.millisToTime(timeInSeconds * 1000L);
        StringBuilder messageBuilder = new StringBuilder();

        if(!timeMessage.prefix().isEmpty()) messageBuilder.append(timeMessage.prefix());

        if (timeRecord.years() > 0) {
            messageBuilder.append(timeMessage.years());
            firstUnit = false;
        }

        if (timeRecord.months() > 0) {
            if (!firstUnit) {
                messageBuilder.append(" ");
            }
            messageBuilder.append(timeMessage.months());
            firstUnit = false;
        }

        if (timeRecord.weeks() > 0) {
            if (!firstUnit) {
                messageBuilder.append(" ");
            }
            messageBuilder.append(timeMessage.weeks());
            firstUnit = false;
        }

        if (timeRecord.days() > 0) {
            if (!firstUnit) {
                messageBuilder.append(" ");
            }
            messageBuilder.append(timeMessage.days());
            firstUnit = false;
        }

        if (timeRecord.hours() > 0) {
            if (!firstUnit) {
                messageBuilder.append(" ");
            }
            messageBuilder.append(timeMessage.hours());
            firstUnit = false;
        }

        if (timeRecord.minutes() > 0) {
            if (!firstUnit) {
                messageBuilder.append(" ");
            }
            messageBuilder.append(timeMessage.minutes());
            firstUnit = false;
        }

        if (timeRecord.seconds() > 0) {
            if (!firstUnit) {
                messageBuilder.append(" ");
            }
            messageBuilder.append(timeMessage.seconds());
            firstUnit = false;
        }

        if(firstUnit) {
            messageBuilder.append(timeMessage.seconds());
        }

        if(!timeMessage.suffix().isEmpty()) messageBuilder.append(timeMessage.suffix());

        List<TagResolver.Single> placeholders = List.of(
                Placeholder.parsed("years", String.valueOf(timeRecord.years())),
                Placeholder.parsed("months", String.valueOf(timeRecord.months())),
                Placeholder.parsed("weeks", String.valueOf(timeRecord.weeks())),
                Placeholder.parsed("days", String.valueOf(timeRecord.days())),
                Placeholder.parsed("hours", String.valueOf(timeRecord.hours())),
                Placeholder.parsed("minutes", String.valueOf(timeRecord.minutes())),
                Placeholder.parsed("seconds", String.valueOf(timeRecord.seconds())));

        return AdventureUtil.serialize(AdventureUtil.deserialize(messageBuilder.toString(), placeholders));
    }
}