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

import org.powernukkit.converters.gui.extensions.action
import org.powernukkit.converters.gui.extensions.withMax
import java.awt.*
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.filechooser.FileFilter

/**
 * @author joserobjr
 * @since 2020-11-12
 */
class SelectWorldPanel {
    private val worldSelectionField = JTextField().apply {
        document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                checkFile()
            }

            override fun removeUpdate(e: DocumentEvent?) {
                checkFile()
            }

            override fun changedUpdate(e: DocumentEvent?) {
                checkFile()
            }
        })
    }

    private val worldSelectionLabel = JLabel("Input world:").apply {
        labelFor = worldSelectionField
    }

    private val header = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(JLabel("<html><h1>PowerNukkit Universal Converters</h1></html>"))
        add(JSeparator(JSeparator.HORIZONTAL).withMax(height = 1))
        add(Box.createRigidArea(Dimension(0, 10)))
        add(
            JLabel(
                "<html><p>Select the <b>level.dat</b> file which is directly inside the folder of the world that you want to convert. " +
                        "Worlds with type <b>mcworld</p> and zipped inside and <b>zip</b> and <b>tar.gz</b> are also supported.</p></html>"
            )
        )
    }

    private val debug = JLabel("Test")

    private val form = JPanel(GridBagLayout()).apply {

        add(worldSelectionLabel, GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            fill = GridBagConstraints.NONE
        })

        add(worldSelectionField, GridBagConstraints().apply {
            gridx = 1
            gridy = 0
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
            minimumSize = Dimension(300, 20)
        })

        add(JButton(action("Browse...") {
            openFileChooser()
        }), GridBagConstraints().apply {
            gridx = 2
            gridy = 0
            fill = GridBagConstraints.NONE
        })

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
                openFileChooser()
            } else {
                openFileChooser(location)
            }
        }), GridBagConstraints().apply {
            gridx = 3
            gridy = 0
            fill = GridBagConstraints.NONE
        })

        if (isWindows) {
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
                    openFileChooser(location)
                } else {
                    openFileChooser(location)
                }
            }), GridBagConstraints().apply {
                gridx = 4
                gridy = 0
                fill = GridBagConstraints.NONE
            })
        }

        add(debug, GridBagConstraints().apply {
            gridx = 0
            gridy = 1
            gridwidth = 3
        })

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

    private fun openFileChooser(location: File? = null) {
        var current = location ?: worldSelectionField.text?.takeIf { it.isNotBlank() }?.let(::File)
        while (current != null && !current.exists()) {
            current = current.parentFile
        }

        JFileChooser(current).apply {
            isMultiSelectionEnabled = true
            isAcceptAllFileFilterUsed = false
            fileView = WorldPreviewIcon()
            accessory = WorldValidationPanel().component
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
                if (ev.propertyName == "directoryChanged") {
                    ev.newValue?.toString()?.let(::File)?.takeIf { it.isDirectory }?.let { newDir ->
                        newDir.resolve("level.dat").let { levelDatFile ->
                            if (levelDatFile.isFile) {
                                selectedFile = levelDatFile
                                approveSelection()
                            }
                        }
                    }
                }
            }

            if (showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                worldSelectionField.text = selectedFile.absolutePath
            }
        }
    }

    private fun checkFile() {
        debug.text = worldSelectionField.text
    }
}
