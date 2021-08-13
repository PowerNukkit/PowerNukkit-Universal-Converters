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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.swing.Swing
import org.powernukkit.converters.gui.extensions.buffered
import org.powernukkit.converters.gui.extensions.scaleDownKeepingAspect
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.InvalidPathException
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
    private val temporaryImage: BufferedImage,
    parent: Job,
) : FileView(), CoroutineScope {
    private val job = Job(parent)
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    private val log = InlineLogger()
    private val icons = mutableMapOf<File, ImageIcon?>()
    private val names = mutableMapOf<File, String?>()

    @ExperimentalCoroutinesApi
    private val needsRepaint = MutableStateFlow(false)

    @ExperimentalCoroutinesApi
    private val _isRepainting = MutableStateFlow(false)

    @ExperimentalCoroutinesApi
    val isRepainting = _isRepainting.asStateFlow()

    @ExperimentalCoroutinesApi
    private var currentDir: File = chooser.currentDirectory
        get() {
            return if (isRepainting.value) {
                field
            } else {
                chooser.currentDirectory
            }
        }

    @ExperimentalCoroutinesApi
    private val repaintJob = launch(Dispatchers.Swing) {
        while (true) {
            needsRepaint.collect { repaint ->
                if (repaint) {
                    delay(5)
                    log.debug { "Repainting" }
                    // I did using this hacky way because the custom names were not expanding
                    _isRepainting.value = true
                    val x = chooser.currentDirectory
                    currentDir = x
                    chooser.currentDirectory = x.parentFile ?: File(System.getProperty("user.home"))
                    chooser.currentDirectory = x
                    _isRepainting.value = false
                    needsRepaint.value = false
                }
            }
        }
    }

    private fun isPartOfCurrentDirectoryPath(f: File): Boolean {
        val path = try {
            f.toPath()
        } catch (e: InvalidPathException) {
            return false
        }

        val currentDir = try {
            currentDir.toPath()
        } catch (e: InvalidPathException) {
            return false
        }

        return try {
            currentDir == path || path.relativize(currentDir).toString().startsWith("..")
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    @ExperimentalCoroutinesApi
    override fun getName(f: File): String? {
        if (!isPartOfCurrentDirectoryPath(f)) {
            return null
        }

        if (f in names) {
            return names[f]
        }

        launch(Dispatchers.IO) {
            val name = buildName(f)
            withContext(Dispatchers.Swing) {
                names[f] = name
                if (name != null) {
                    needsRepaint.value = true
                }
            }
        }

        return null
    }

    private fun buildName(f: File): String? {
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
        if (!isPartOfCurrentDirectoryPath(f)) {
            return null
        }

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
