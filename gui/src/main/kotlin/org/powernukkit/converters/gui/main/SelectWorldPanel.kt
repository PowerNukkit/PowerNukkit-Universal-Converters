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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import org.powernukkit.converters.gui.extensions.*
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.filechooser.FileFilter
import kotlin.coroutines.CoroutineContext
import kotlin.math.min

/**
 * @author joserobjr
 * @since 2020-11-12
 */
@ExperimentalCoroutinesApi
class SelectWorldPanel(parent: Job) : CoroutineScope {
    private val job = Job(parent)
    override val coroutineContext: CoroutineContext
        get() = job

    private var openChooser: JFileChooser? = null

    private val worldSelectionLabel = JLabel("Input world:")
    private var selected: File? = null
    private var lastPlace: File? = null

    private val header = JPanel(GridBagLayout()).apply {
        var line = 0
        add(
            JLabel("<html><h1 style='text-align: center;'>PowerNukkit Universal Converters</h1></html>"),
            gridBagData(0, line++)
        )
        add(JSeparator(JSeparator.HORIZONTAL), gridBagData(0, line++, fill = GBFill.HORIZONTAL, weightX = 1.0))
        add(Box.createRigidArea(Dimension(0, 10)), gridBagData(0, line++))
        add(
            JLabel(
                "<html><p>Select either a <b>level.dat</b> file, a <b>.mcworld</b> file or a world folder to continue.</p></html>"
            ),
            gridBagData(0, line++)
        )
    }

    private val debug = JLabel("Test")

    private val form = JPanel(GridBagLayout()).apply {

        //add(worldSelectionLabel, gridBagData(0, 0))

        add(JButton(action("Browse...") {
            openFileChooser()
        }), gridBagData(1, 0))

        val isWindows = "Windows" in System.getProperty("os.name")

        add(JButton(action("Java Worlds") {
            val appData = if (isWindows) {
                System.getenv("APPDATA")
            } else {
                System.getProperty("user.home")
            }?.takeIf { it.isNotBlank() }
            val location = appData?.let { File(it, ".minecraft/saves") }
            if (location == null || !location.isDirectory && !location.parentFile.isDirectory) {
                JOptionPane.showMessageDialog(
                    this@apply,
                    "The .minecraft/saves folder was not found in your system. Click on \"Browse...\" to locate it manually.",
                    "Java Edition saves not found",
                    JOptionPane.ERROR_MESSAGE
                )
                openFileChooser(changesLastPlace = false)
            } else {
                openFileChooser(location, changesLastPlace = false)
            }
        }), gridBagData(2, 0))

        add(JButton(action("Windows 10 Edition") {
            val localAppData = System.getenv("LOCALAPPDATA")?.takeIf { it.isNotBlank() }
            val location = localAppData?.let {
                File(
                    it,
                    "Packages/Microsoft.MinecraftUWP_8wekyb3d8bbwe/LocalState/games/com.mojang/minecraftWorlds"
                )
            }
            if (location == null || !location.isDirectory) {
                JOptionPane.showMessageDialog(
                    this@apply,
                    "The Minecraft Windows 10 Edition save folder was not found in your system. Click on \"Browse...\" to locate it manually.",
                    "Windows 10 Edition saves not found",
                    JOptionPane.ERROR_MESSAGE
                )
            }
            openFileChooser(location, changesLastPlace = false, wideDefault = true)
        }).apply {
            isEnabled = isWindows
        }, gridBagData(3, 0))
    }

    private val panel = JPanel(BorderLayout()).apply {
        border = EmptyBorder(10, 10, 10, 10)
        add(header, BorderLayout.NORTH)
        add(JPanel(BorderLayout()).apply {
            border = EmptyBorder(10, 0, 0, 0)
            add(form, BorderLayout.NORTH)
        }, BorderLayout.CENTER)
    }

    val component: Component get() = panel

    private fun openFileChooser(
        location: File? = null,
        changesLastPlace: Boolean = true,
        wideDefault: Boolean = false
    ) {
        var current = location ?: lastPlace
        while (current != null && !current.exists()) {
            current = current.parentFile
        }


        val chooseFileJob = Job(job)
        val prev = UIManager.get("FileChooser.readOnly")
        try {
            UIManager.put("FileChooser.readOnly", true)
            openChooser = object : JFileChooser(current) {
                override fun approveSelection() {
                    with(selectedFile ?: return) {
                        if (isFile) {
                            super.approveSelection()
                        } else if (isDirectory) {
                            if (!resolve("level.dat").isFile) {
                                currentDirectory = selectedFile
                                selectedFile = File("")
                            } else {
                                selectedFile = resolve("level.dat")
                                approveSelection()
                            }
                        }
                    }
                }
            }.apply {
                val cache = LevelDataCache()
                isMultiSelectionEnabled = false
                isAcceptAllFileFilterUsed = false
                fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
                val defaultImage = if (wideDefault) {
                    cache.defaultWideIcon ?: BufferedImage(64, 36, BufferedImage.TYPE_INT_ARGB)
                } else {
                    cache.defaultSquareIcon ?: BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB)
                }.scaleDownKeepingAspect(64, 64).buffered()

                val previewIcon = WorldPreviewIcon(this, cache, defaultImage, chooseFileJob)
                fileView = previewIcon
                accessory = WorldValidationPanel(this, cache, chooseFileJob).component
                val screenSize = Toolkit.getDefaultToolkit().screenSize
                preferredSize = Dimension(min(screenSize.width - 50, 1024), min(screenSize.height - 50, 600))
                fileFilter = object : FileFilter() {
                    override fun getDescription() = "Minecraft World (level.dat, *.mcworld)"
                    override fun accept(f: File?): Boolean {
                        return when {
                            f == null -> false
                            f.isDirectory -> true
                            !f.isFile -> false
                            f.name.equals("level.dat", ignoreCase = true) -> true
                            else -> when (f.extension.toLowerCase()) {
                                "mcworld" -> true
                                else -> false
                            }
                        }
                    }
                }

                addPropertyChangeListener { ev ->
                    if (!previewIcon.isRepainting.value && ev.propertyName == JFileChooser.DIRECTORY_CHANGED_PROPERTY) {
                        ev.newValue?.toString()?.let(::File)?.takeIf { it.isDirectory }?.let { newDir ->
                            newDir.resolve("level.dat").let { levelDatFile ->
                                if (levelDatFile.isFile) {
                                    selectedFile = levelDatFile
                                    approveSelection()
                                } else if (changesLastPlace) {
                                    lastPlace = newDir
                                }
                            }
                        }
                    }
                }

                if (showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    selected = selectedFile.absoluteFile
                }
                openChooser = null
            }
        } finally {
            UIManager.put("FileChooser.readOnly", prev)
            chooseFileJob.cancel()
        }
    }

    fun cancel() {
        openChooser?.cancelSelection()
        openChooser?.isVisible = false
        job.cancel()
    }
}
