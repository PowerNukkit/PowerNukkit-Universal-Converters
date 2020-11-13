/*
 * PowerNukkit Universal Worlds & Converters for Minecraft
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.powernukkit.converters.gui.window.main

import com.github.michaelbull.logging.InlineLogger
import org.powernukkit.converters.storage.api.leveldata.LevelDataIO
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author joserobjr
 * @since 2020-11-13
 */
class LevelDataCache {
    private val log = InlineLogger()
    private val cache: MutableMap<File, Optional<LevelData>> = ConcurrentHashMap()

    fun getOpt(file: File): Optional<LevelData> {
        return cache.computeIfAbsent(file) {
            try {
                Optional.of(LevelDataIO.readLevelDataBlocking(it))
            } catch (e: Exception) {
                log.debug(e) { "Failed to parse the level.dat file. File: $file" }
                Optional.empty()
            }
        }
    }

    operator fun get(file: File): LevelData? {
        return getOpt(file).orElse(null)
    }
}
