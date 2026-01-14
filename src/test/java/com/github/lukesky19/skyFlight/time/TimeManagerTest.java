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
package com.github.lukesky19.skyFlight.time;

import com.github.lukesky19.skyFlight.SkyFlight;
import com.github.lukesky19.skyFlight.bossbar.BossBarManager;
import com.github.lukesky19.skyFlight.player.PlayerData;
import com.github.lukesky19.skyFlight.player.PlayerDataManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests {@link TimeManager}.
 */
@ExtendWith(MockitoExtension.class)
public class TimeManagerTest {
    /**
     * Test {@link TimeManager#addFlightTime(Player, long)}, but player data is invalid.
     */
    @Test
    public void testAddFlightTimeInvalidPlayerData() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        TimeManager timeManager = new TimeManager(skyFlight, playerDataManager, bossBarManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);

        assertFalse(timeManager.addFlightTime(player, 5));
    }

    /**
     * Test {@link TimeManager#addFlightTime(Player, long)}.
     */
    @Test
    public void testAddFlightTime() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        TimeManager timeManager = new TimeManager(skyFlight, playerDataManager, bossBarManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        assertTrue(timeManager.addFlightTime(player, 5));
        assertEquals(5, playerData.getFlightTime());
        verify(bossBarManager).updateBossBar(player);
    }

    /**
     * Test {@link TimeManager#removeFlightTime(Player, long)}, but player data is invalid.
     */
    @Test
    public void testRemoveFlightTimeInvalidPlayerData() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        TimeManager timeManager = new TimeManager(skyFlight, playerDataManager, bossBarManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);

        assertFalse(timeManager.removeFlightTime(player, 5));
    }

    /**
     * Test {@link TimeManager#removeFlightTime(Player, long)}.
     */
    @Test
    public void testRemoveFlightTime() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        TimeManager timeManager = new TimeManager(skyFlight, playerDataManager, bossBarManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(10);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        assertTrue(timeManager.removeFlightTime(player, 5));
        assertEquals(5, playerData.getFlightTime());
        verify(bossBarManager).updateBossBar(player);
    }

    /**
     * Test {@link TimeManager#setFlightTime(Player, long)}, but player data is invalid.
     */
    @Test
    public void testSetFlightTimeInvalidPlayerData() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        TimeManager timeManager = new TimeManager(skyFlight, playerDataManager, bossBarManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);

        assertFalse(timeManager.setFlightTime(player, 5));
    }

    /**
     * Test {@link TimeManager#setFlightTime(Player, long)}.
     */
    @Test
    public void testSetFlightTime() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        TimeManager timeManager = new TimeManager(skyFlight, playerDataManager, bossBarManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(10);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        assertTrue(timeManager.setFlightTime(player, 5));
        assertEquals(5, playerData.getFlightTime());
        verify(bossBarManager).updateBossBar(player);
    }

    /**
     * Test {@link TimeManager#getFlightTime(Player)}, but player data is invalid.
     */
    @Test
    public void testGetFlightTimeInvalidPlayerData() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        TimeManager timeManager = new TimeManager(skyFlight, playerDataManager, bossBarManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);

        assertEquals(0, timeManager.getFlightTime(player));
    }

    /**
     * Test {@link TimeManager#getFlightTime(Player)}.
     */
    @Test
    public void testGetFlightTime() {
        SkyFlight skyFlight = mock(SkyFlight.class);
        ComponentLogger logger = mock(ComponentLogger.class);
        when(skyFlight.getComponentLogger()).thenReturn(logger);
        PlayerDataManager playerDataManager = mock(PlayerDataManager.class);
        BossBarManager bossBarManager = mock(BossBarManager.class);
        TimeManager timeManager = new TimeManager(skyFlight, playerDataManager, bossBarManager);

        Player player = mock(Player.class);
        UUID playerId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(playerId);
        playerData.setFlightTime(10);
        when(playerDataManager.getPlayerData(playerId)).thenReturn(playerData);

        assertEquals(10, timeManager.getFlightTime(player));
    }
}