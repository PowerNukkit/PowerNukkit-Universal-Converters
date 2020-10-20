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

package org.powernukkit.converters.storage.anvil

import br.com.gamemods.nbtmanipulator.NbtFile
import br.com.gamemods.nbtmanipulator.NbtIO
import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.*
import org.powernukkit.converters.platform.universal.UniversalPlatform
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import javax.imageio.ImageIO

/**
 * @author joserobjr
 * @since 2020-10-19
 */
class AnvilStorageLoader(
    val universalPlatform: UniversalPlatform,
    val folder: File,
) {
    private val log = InlineLogger()

    fun CoroutineScope.loadIcon(iconFile: File = folder.resolve("icon.png")) = async(Dispatchers.IO) {
        try {
            withTimeout(10_000) {
                runInterruptible {
                    iconFile.takeIf { it.isFile }?.let { ImageIO.read(it) }
                }
            }
        } catch (e: IOException) {
            log.warn(e) { "Could not load the image $iconFile" }
            null
        } catch (e: CancellationException) {
            log.warn(e) { "The image of $iconFile was cancelled" }
            null
        }
    }

    private fun loadAutoDetectingFormat(levelDataFile: NbtFile) {
        val levelData = requireNotNull(levelDataFile.compound.getNullableCompound("Data")) {
            "The level data file is not valid. It is missing the root Data NBT Compound."
        }


    }

    fun CoroutineScope.loadLevelData(dataFile: File = folder.resolve("level.dat")) = async(Dispatchers.IO) {
        try {
            val levelDataFile = withTimeout(10_000) {
                runInterruptible {
                    if (!dataFile.isFile) {
                        throw FileNotFoundException(dataFile.toString())
                    }
                    NbtIO.readNbtFile(dataFile)
                }
            }

            loadAutoDetectingFormat(levelDataFile)
        } catch (e: Exception) {
            log.error(e) { "Could not load the level data file: $dataFile" }
            throw e
        }
    }
}
