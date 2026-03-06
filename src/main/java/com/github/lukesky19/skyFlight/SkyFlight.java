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
package com.github.lukesky19.skyFlight;

import com.github.lukesky19.skyFlight.api.SkyFlightAPI;
import com.github.lukesky19.skyFlight.command.SkyFlightCommand;
import com.github.lukesky19.skyFlight.locale.LocaleManager;
import com.github.lukesky19.skyFlight.settings.SettingsManager;
import com.github.lukesky19.skyFlight.database.DatabaseManager;
import com.github.lukesky19.skyFlight.database.connection.ConnectionManager;
import com.github.lukesky19.skyFlight.database.queue.QueueManager;
import com.github.lukesky19.skyFlight.integration.HookManager;
import com.github.lukesky19.skyFlight.integration.hooks.BentoBoxHook;
import com.github.lukesky19.skyFlight.integration.hooks.WorldGuardHook;
import com.github.lukesky19.skyFlight.listener.*;
import com.github.lukesky19.skyFlight.bossbar.BossBarManager;
import com.github.lukesky19.skyFlight.flight.FlightManager;
import com.github.lukesky19.skyFlight.player.PlayerDataManager;
import com.github.lukesky19.skyFlight.time.TimeManager;
import com.github.lukesky19.skyFlight.task.TaskManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.common.abstracts.SkyPlugin;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The plugin's main class and entry point.
 */
public final class SkyFlight extends SkyPlugin {
    private DatabaseManager databaseManager;
    private SettingsManager settingsManager;
    private LocaleManager localeManager;
    private PlayerDataManager playerDataManager;
    private FlightManager flightManager;
    private TaskManager taskManager;
    private HookManager hookManager;

    /**
     * Constructor
     */
    public SkyFlight() {}

    /**
     * Initializes data before the plugin is enabled.
     */
    @Override
    public void onLoad() {
        // Integration/Hooks
        hookManager = new HookManager();

        // Register WorldGuard Hook
        WorldGuardHook worldGuardHook = new WorldGuardHook(this);
        hookManager.registerHook(WorldGuardHook.class, worldGuardHook);

        // Register WorldGuard Flag
        worldGuardHook.registerFlightFlag(this.getComponentLogger());
    }

    /**
     * Initializes all plugin data
     */
    @Override
    public void onEnable() {
        if(!checkSkyLibVersion()) return;

        // Database
        ConnectionManager connectionManager = new ConnectionManager(this);
        QueueManager queueManager = new QueueManager(connectionManager);
        databaseManager = new DatabaseManager(connectionManager, queueManager);

        // Config
        settingsManager = new SettingsManager(this);
        localeManager = new LocaleManager(this, settingsManager);

        // Register BentoBox Hook
        BentoBoxHook bentoBoxHook = new BentoBoxHook(this);
        hookManager.registerHook(BentoBoxHook.class, bentoBoxHook);

        // Register BentoBox Settings Flag
        bentoBoxHook.registerFlightFlag();

        // Data
        playerDataManager = new PlayerDataManager(this.getComponentLogger(), databaseManager);
        BossBarManager bossBarManager = new BossBarManager(this, settingsManager, localeManager, playerDataManager);
        TimeManager timeManager = new TimeManager(this, playerDataManager, bossBarManager);
        flightManager = new FlightManager(this, settingsManager, localeManager, playerDataManager, bossBarManager, hookManager);

        // Create and register the SkyFlightAPI
        SkyFlightAPI skyFlightAPI = new SkyFlightAPI(flightManager, timeManager);
        this.getServer().getServicesManager().register(SkyFlightAPI.class, skyFlightAPI, this, ServicePriority.Lowest);

        // Tasks
        taskManager = new TaskManager(this, localeManager, flightManager, timeManager);

        // Commands
        SkyFlightCommand skyFlightCommand = new SkyFlightCommand(this, settingsManager, localeManager, playerDataManager, flightManager, timeManager);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                commands ->
                        commands.registrar().register(skyFlightCommand.createCommand(),
                                "Command to manage and use the SkyFlight plugin.",
                                List.of("fly")));

        // Listeners
        PluginManager pluginManager = this.getServer().getPluginManager();
        if(pluginManager.isPluginEnabled("BentoBox")) {
            pluginManager.registerEvents(new FlyFlagListener(flightManager, hookManager), this);
            pluginManager.registerEvents(new IslandListener(this, flightManager, bossBarManager), this);
        }

        pluginManager.registerEvents(new PlayerDeathListener(flightManager), this);
        pluginManager.registerEvents(new PlayerJoinListener(playerDataManager, flightManager), this);
        pluginManager.registerEvents(new PlayerQuitListener(playerDataManager, flightManager), this);
        pluginManager.registerEvents(new PlayerRespawnListener(flightManager), this);
        pluginManager.registerEvents(new PlayerToggleFlightListener(flightManager, bossBarManager), this);

        // Reload any plugin data
        reload();

        // Load player data and enable flight if necessary
        for(Player player : this.getServer().getOnlinePlayers()) {
            playerDataManager.loadPlayerData(player.getUniqueId()).thenAccept(v -> {
                if(flightManager.canFly(player, true)) {
                    flightManager.enableFlight(player, true);
                }
            });
        }
    }

    /**
     * Cleans up any plugin data
     */
    @Override
    public void onDisable() {
        if(taskManager != null) taskManager.stopTasks();

        if(hookManager != null) {
            hookManager.getHook(BentoBoxHook.class).unRegisterFlightFlag();
        }

        if(flightManager != null) {
            this.getServer().getOnlinePlayers().forEach(player -> flightManager.disableFlight(player, false));
        }

        if(playerDataManager != null) {
            playerDataManager.savePlayerData().thenCompose(v1 -> {
                if(databaseManager != null) {
                    return databaseManager.handlePluginDisable();
                }

                return CompletableFuture.completedFuture(null);
            }).join();
        }
    }

    /**
     * Reloads any plugin data
     */
    @Override
    public void reload() {
        // Config
        settingsManager.loadConfiguration();
        localeManager.loadConfiguration();

        // Tasks
        taskManager.stopTasks();
        taskManager.startTasks();
    }

    /**
     * Checks if the Server has the proper SkyLib version.
     * @return true if it does, false if not.
     */
    private boolean checkSkyLibVersion() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        Plugin skyLib = pluginManager.getPlugin("SkyLib");
        if(skyLib != null && skyLib.isEnabled()) {
            String version = skyLib.getPluginMeta().getVersion();
            String[] splitVersion = version.split("\\.");
            int second = Integer.parseInt(splitVersion[1]);

            if(second >= 4) {
                return true;
            }
        }

        this.getComponentLogger().error(AdventureUtil.deserialize("SkyLib Version 1.4.0.0 or newer is required to run this plugin."));
        this.getServer().getPluginManager().disablePlugin(this);
        return false;
    }

    /**
     * This method is used for unit testing purposes only.
     * @param event The event to call.
     * @param <E> The class that extends {@link Event} passed to the method.
     * @return The {@link Event} passed.
     */
    public <E extends Event> @NonNull E callEvent(@NonNull E event) {
        this.getServer().getPluginManager().callEvent(event);
        return event;
    }
}