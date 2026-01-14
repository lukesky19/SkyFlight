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
package com.github.lukesky19.skyFlight.player;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests {@link PlayerData}.
 */
@ExtendWith(MockitoExtension.class)
public class PlayerDataTest {
    /**
     * Test {@link PlayerData#getPlayerId()}.
     */
    @Test
    public void testGetPlayerId() {
        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);

        assertEquals(playerId, playerData.getPlayerId());
    }

    /**
     * Test {@link PlayerData#addFlightTime(long)}.
     */
    @Test
    public void testAddFlightTime() {
        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);

        assertTrue(playerData.addFlightTime(10));
        assertEquals(10, playerData.getFlightTime());
    }

    /**
     * Test {@link PlayerData#addFlightTime(long)}, but the time is below 0.
     */
    @Test
    public void testAddFlightTimeBelowZero() {
        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);

        assertFalse(playerData.addFlightTime(-10));
        assertEquals(0, playerData.getFlightTime());
    }

    /**
     * Test {@link PlayerData#removeFlightTime(long)}.
     */
    @Test
    public void testRemoveFlightTime() {
        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);

        assertTrue(playerData.addFlightTime(20));
        assertTrue(playerData.removeFlightTime(10));
        assertEquals(10, playerData.getFlightTime());
    }

    /**
     * Test {@link PlayerData#removeFlightTime(long)}, but the time is below 0.
     */
    @Test
    public void testRemoveFlightTimeBelowZero() {
        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);

        assertFalse(playerData.removeFlightTime(-10));
        assertEquals(0, playerData.getFlightTime());
    }

    /**
     * Test {@link PlayerData#setFlightTime(long)}.
     */
    @Test
    public void testSetFlightTime() {
        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);

        assertTrue(playerData.setFlightTime(10));
        assertEquals(10, playerData.getFlightTime());
    }

    /**
     * Test {@link PlayerData#setFlightTime(long)}, but the time is below 0.
     */
    @Test
    public void testSetFlightTimeBelowZero() {
        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);

        assertFalse(playerData.setFlightTime(-10));
        assertEquals(0, playerData.getFlightTime());
    }

    /**
     * Test {@link PlayerData#setTimedFlight(boolean)} and {@link PlayerData#isTimedFlight()}.
     */
    @Test
    public void testTimedFlight() {
        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);

        assertFalse(playerData.isTimedFlight());
        playerData.setTimedFlight(true);
        assertTrue(playerData.isTimedFlight());
    }

    /**
     * Test {@link PlayerData#equals(Object)}, but the object passed is null.
     */
    @Test
    public void testEqualsNullObject() {
        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);

        //noinspection SimplifiableAssertion, ConstantValue -- Do not simplify otherwise Jacoco reports as not tested.
        assertFalse(playerData.equals(null));
    }

    /**
     * Test {@link PlayerData#equals(Object)}, but the object passed is not that of {@link PlayerData}.
     */
    @Test
    public void testEqualsObjectNotPlayerData() {
        UUID playerId = UUID.randomUUID();
        PlayerData playerData = new PlayerData(playerId);

        //noinspection SimplifiableAssertion -- Do not simplify otherwise Jacoco reports as not tested.
        assertFalse(playerData.equals(""));
    }

    /**
     * Test {@link PlayerData#equals(Object)}, but the two player datas are not equal by player id.
     */
    @Test
    public void testEqualsNotEqualByPlayerId() {
        UUID playerId = UUID.randomUUID();
        PlayerData playerData1 = new PlayerData(playerId);
        PlayerData playerData2 = new PlayerData(UUID.randomUUID());

        assertNotEquals(playerData2, playerData1);
    }

    /**
     * Test {@link PlayerData#equals(Object)}, but the two player datas are not equal by flight time.
     */
    @Test
    public void testEqualsNotEqualByFlightTime() {
        UUID playerId = UUID.randomUUID();
        PlayerData playerData1 = new PlayerData(playerId);
        playerData1.setFlightTime(100);
        PlayerData playerData2 = new PlayerData(playerId);

        assertNotEquals(playerData2, playerData1);
    }

    /**
     * Test {@link PlayerData#equals(Object)}, but the two player datas are not equal by timed flight.
     */
    @Test
    public void testEqualsNotEqualByIsTimedFlight() {
        UUID playerId = UUID.randomUUID();
        PlayerData playerData1 = new PlayerData(playerId);
        playerData1.setTimedFlight(true);
        PlayerData playerData2 = new PlayerData(playerId);

        assertNotEquals(playerData2, playerData1);
    }

    /**
     * Test {@link PlayerData#equals(Object)}, but both player data are equal.
     */
    @Test
    public void testEqualsActuallyEqual() {
        UUID playerId = UUID.randomUUID();
        PlayerData playerData1 = new PlayerData(playerId);
        PlayerData playerData2 = new PlayerData(playerId);

        assertEquals(playerData2, playerData1);
    }
}