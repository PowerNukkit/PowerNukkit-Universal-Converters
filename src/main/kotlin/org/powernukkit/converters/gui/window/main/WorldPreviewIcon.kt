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
import org.powernukkit.converters.gui.extensions.scaleDownKeepingAspect
import org.powernukkit.converters.storage.api.leveldata.LevelDataIO
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.filechooser.FileView

/**
 * @author joserobjr
 * @since 2020-11-12
 */
class WorldPreviewIcon : FileView() {
    private val log = InlineLogger()
    private val cache = mutableMapOf<File, Optional<LevelData>>()

    override fun getName(f: File): String? {
        if (f.isDirectory) {
            try {
                f.resolve("level.dat").takeIf { it.isFile }?.let { dat ->
                    val data = requestLevelData(dat) ?: return null
                    return addDetails(f, data)
                }
            } catch (e: Exception) {
                log.debug(e) { "Failed to parse the folder to get the level name $f" }
                return null
            }
        }

        if (!f.isFile || !f.name.equals("level.dat", ignoreCase = true)) {
            return null
        }

        val data = requestLevelData(f) ?: return null
        return addDetails(f, data)
    }

    private fun addDetails(file: File, data: LevelData): String? {
        val version = data.versionData?.lastOpenedWithVersion ?: data.versionData?.baseGameVersion
        if (data.levelName == null && version == null) {
            return null
        }
        return buildString {
            append(file.name)
            data.levelName?.let { append(" │ ").append(it) }
            version?.let { append(" │ ").append(it) }
        }
    }

    /*override fun getTypeDescription(f: File): String? {
        if (!f.isFile || !f.name.equals("level.dat", ignoreCase = true)) {
            return null
        }
        
        val data = requestLevelData(f) ?: return null
        return buildString {
            with(data) {
                versionData?.minecraftEdition?.let { append(it.name.capitalize()).append(' ') }
                dialect?.let { append(it.name.capitalize()).append(' ') }
                storageEngineType?.let { append(it.name.capitalize()).append(' ') }
                versionData?.lastOpenedWithVersion?.let { append(it).append(' ') }
                versionData?.baseGameVersion?.let { append(it).append(' ') }
            }
        }.takeUnless { it.isBlank() }
    }*/

    override fun getIcon(f: File): Icon? {
        if (f.isDirectory) {
            try {
                if (f.resolve("level.dat").isFile) {
                    val icon =
                        f.resolve("icon.png").takeIf { it.isFile }
                            ?: f.resolve("world_icon.jpeg").takeIf { it.isFile }

                    return icon
                        ?.let(ImageIO::read)
                        ?.scaleDownKeepingAspect(64, 64)
                        ?.let(::ImageIcon)
                }
            } catch (e: Exception) {
                log.debug(e) { "Failed to load the image icon of the folder $f" }
                return null
            }
        }
        if (!f.isFile || !f.name.equals("level.dat", ignoreCase = true)) {
            return null
        }

        return requestLevelData(f)?.icon?.let(::ImageIcon)
    }

    private fun requestLevelData(f: File): LevelData? {
        return cache.computeIfAbsent(f) {
            try {
                Optional.of(LevelDataIO.readLevelDataBlocking(it))
            } catch (e: Exception) {
                log.debug(e) { "Failed to parse the level.dat file for type description. File: $f" }
                Optional.empty()
            }
        }.orElse(null)
    }
}
