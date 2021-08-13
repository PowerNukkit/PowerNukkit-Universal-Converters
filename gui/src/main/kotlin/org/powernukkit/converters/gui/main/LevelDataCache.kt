/*
 *  PowerNukkit Universal Worlds & Converters for Minecraft
 *  Copyright (C) 2021  José Roberto de Araújo Júnior
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.powernukkit.converters.gui.window.main

import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.*
import org.powernukkit.converters.storage.api.StorageEngineType
import org.powernukkit.converters.storage.api.leveldata.LevelDataIO
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO
import kotlin.coroutines.CoroutineContext

/**
 * @author joserobjr
 * @since 2020-11-13
 */
class LevelDataCache : CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    private val log = InlineLogger()
    private val cache: MutableMap<File, Optional<LevelData>> = ConcurrentHashMap()
    private val iconCache: MutableMap<File, Optional<BufferedImage>> = ConcurrentHashMap()
    val defaultWideIcon by lazy {
        "/org/powernukkit/converters/gui/default-world-icon-wide.png".let(WorldValidationPanel::class.java::getResource)
            ?.let(ImageIO::read)
    }

    val defaultSquareIcon by lazy {
        "/org/powernukkit/converters/gui/default-world-icon-square.png".let(WorldValidationPanel::class.java::getResource)
            ?.let(ImageIO::read)
    }

    fun getIcon(levelData: LevelData): Deferred<BufferedImage?> {
        val file = cache.entries.firstOrNull { it.value.orElse(null) == levelData }?.key
            ?: levelData.folder!!.resolve("level.dat").toFile()

        if (file in iconCache) {
            return CompletableDeferred(iconCache[file]?.orElse(null))
        }

        return async {
            iconCache.computeIfAbsent(file) {
                Optional.ofNullable(getIconOrDefault(levelData))
            }.orElse(null)
        }
    }

    fun getIcon(levelDataFile: File): Deferred<BufferedImage?> =
        if (levelDataFile in iconCache) {
            CompletableDeferred(iconCache[levelDataFile]?.orElse(null))
        } else async {
            iconCache.computeIfAbsent(levelDataFile) {
                val levelFolder = levelDataFile.parentFile
                val imageFile = levelFolder.resolve("icon.png").takeIf { it.isFile }
                    ?: levelFolder.resolve("world_icon.jpeg").takeIf { it.isFile }

                Optional.ofNullable(
                    imageFile?.let(ImageIO::read)
                        ?: get(levelDataFile)?.let { getIconOrDefault(it) }
                )
            }.orElse(null)
        }

    private fun getIconOrDefault(levelData: LevelData): BufferedImage? {
        return levelData.icon
            ?: if (levelData.storageEngineType == StorageEngineType.LEVELDB) {
                defaultWideIcon
            } else {
                defaultSquareIcon
            }
    }

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
