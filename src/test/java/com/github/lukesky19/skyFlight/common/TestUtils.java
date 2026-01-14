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
package com.github.lukesky19.skyFlight.common;

import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * This class contains methods used to help with testing.
 */
public class TestUtils {
    /**
     * Helper method to save a bundled resource.
     */
    public static void saveResource(@NotNull Class clazz, @NotNull String resourcePath, @NotNull File output) {
        if(!resourcePath.startsWith("/")) resourcePath = "/" + resourcePath;

        // Open the input stream
        try(InputStream inputStream = clazz.getResourceAsStream(resourcePath)) {
            // Display an error if the input stream is invalid.
            if(inputStream == null) {
                throw new RuntimeException("Failed to create the input stream. Resource Path: " + resourcePath);
            }

            // Prevent overwriting existing file
            if(output.exists()) return;

            // Write the data to disk
            try(OutputStream out = new FileOutputStream(output)) {
                byte[] buffer = new byte[1024];
                int length;

                while((length = inputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error saving default configuration " + output.getName() + " to " + output.getParentFile().getPath() + ". Resource path: " + resourcePath + ". Error: " + e.getMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving default configuration. Resource path: " + resourcePath + ". Error: " + e.getMessage());
        }
    }
}
