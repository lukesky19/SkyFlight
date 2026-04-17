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
package com.github.lukesky19.skyFlight.database;

import com.github.lukesky19.skyFlight.database.connection.ConnectionManager;
import com.github.lukesky19.skyFlight.database.queue.QueueManager;
import com.github.lukesky19.skyFlight.database.table.PlayerDataTable;
import com.github.lukesky19.skyFlight.database.table.VersionsTable;
import com.github.lukesky19.skylib.common.api.database.AbstractDatabaseManager;
import org.jetbrains.annotations.NotNull;

/**
 * This class manages access to database tables and backing up the database.
 */
public class DatabaseManager extends AbstractDatabaseManager {
    private final PlayerDataTable playerDataTable;

    /**
     * Get the {@link PlayerDataTable} table.
     * @return A {@link PlayerDataTable}
     */
    public @NotNull PlayerDataTable getPlayerDataTable() {
        return playerDataTable;
    }

    /**
     * Constructor
     * Initializes the {@link ConnectionManager}, {@link QueueManager}, and all tables.
     * @param connectionManager A {@link ConnectionManager} instance.
     * @param queueManager A {@link QueueManager} instance.
     */
    public DatabaseManager(@NotNull ConnectionManager connectionManager, @NotNull QueueManager queueManager) {
        super(connectionManager, queueManager);

        VersionsTable versionsTable = new VersionsTable(queueManager);
        versionsTable.createTable();

        playerDataTable = new PlayerDataTable(queueManager, versionsTable);
        playerDataTable.createTable();
    }
}
