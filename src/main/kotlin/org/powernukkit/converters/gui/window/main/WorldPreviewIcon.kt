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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.swing.Swing
import org.powernukkit.converters.gui.extensions.buffered
import org.powernukkit.converters.gui.extensions.scaleDownKeepingAspect
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JFileChooser
import javax.swing.filechooser.FileView
import kotlin.coroutines.CoroutineContext


/**
 * @author joserobjr
 * @since 2020-11-12
 */
class WorldPreviewIcon(
    private val chooser: JFileChooser,
    private val cache: LevelDataCache,
    parent: Job,
) : FileView(), CoroutineScope {
    private val job = Job(parent)
    override val coroutineContext: CoroutineContext
        get() = job

    private val log = InlineLogger()
    private val icons = mutableMapOf<File, ImageIcon?>()
    private val temporaryImage = cache.defaultWideIcon
        ?.scaleDownKeepingAspect(64, 64)
        ?: BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)

    @ExperimentalCoroutinesApi
    private val needsRepaint = MutableStateFlow(false)

    @ExperimentalCoroutinesApi
    private val repaintJob = launch {
        while (true) {
            needsRepaint.collect { repaint ->
                if (repaint) {
                    delay(50)
                    withContext(Dispatchers.Swing) {
                        log.debug { "Repainting" }
                        chooser.repaint()
                        needsRepaint.value = false
                    }
                }
            }
        }
    }

    override fun getName(f: File): String? {
        log.debug { "getName(${f.name})" }
        if (f.isDirectory) {
            try {
                f.resolve("level.dat").takeIf { it.isFile }?.let { dat ->
                    val data = cache[dat] ?: return null
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

        val data = cache[f] ?: return null
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

    @ExperimentalCoroutinesApi
    override fun getIcon(f: File): Icon? {
        return icons.computeIfAbsent(f) icon@{
            if (f.isDirectory) {
                try {
                    f.resolve("level.dat").takeIf { it.isFile }?.let {
                        val icon = ImageIcon(temporaryImage)
                        val loading = cache.getIcon(it)
                        launch {
                            val loadedIcon = loading.await()
                                ?.scaleDownKeepingAspect(64, 64)
                                ?: return@launch
                            withContext(Dispatchers.Swing) {
                                icon.image = loadedIcon.buffered()
                                needsRepaint.value = true
                            }
                        }
                        return@icon icon
                    }
                } catch (e: Exception) {
                    log.debug(e) { "Failed to load the image icon of the folder $f" }
                }
                return@icon null
            }
            if (!f.isFile || !f.name.equals("level.dat", ignoreCase = true)) {
                return@icon null
            }

            return@icon cache[f]?.icon?.let(::ImageIcon)
        }
    }
}
