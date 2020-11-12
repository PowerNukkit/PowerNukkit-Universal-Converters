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

package org.powernukkit.converters.ui.main

import org.powernukkit.converters.WorldConverterAPI
import org.powernukkit.converters.ui.WorldConverterGUI
import org.powernukkit.converters.ui.labelUri
import org.powernukkit.converters.ui.makeMultiline
import org.powernukkit.converters.ui.scaleDown
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.KeyEvent
import javax.swing.*
import javax.swing.border.EmptyBorder

/**
 * @author joserobjr
 * @since 2020-10-23
 */
internal class AboutDialog(gui: WorldConverterGUI) {
    private val okButton = JButton(action("OK") { dialog.dispose() })
    private val buttonsPanel = JPanel().apply {
        add(okButton)
    }

    private val mainContentPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = EmptyBorder(5, 15, 5, 5)
        add(JLabel("<html><h1>Universal World Converter</h1></html>"))
        add(JLabel("Convert any Minecraft world to any Minecraft server"))
        add(Box.createRigidArea(Dimension(0, 10)))
        add(JSeparator(SwingConstants.HORIZONTAL).apply { maximumSize = Dimension(Int.MAX_VALUE, 1) })
        add(Box.createRigidArea(Dimension(0, 10)))
        add(
            JLabel(
                """This tool allows you to convert Minecraft worlds between different versions and editions,
                    | a great effort has been done to preserve the integrity and details of the world's content but this
                    | operation can be lossy depending on the conversion configurations and the source content.
                    |
                    | Worlds from modded saves are partially supported, meaning that modded content
                    | may be replaced by something else or ignored.""".trimMargin()
            ).makeMultiline()
        )
        add(Box.createRigidArea(Dimension(0, 10)))
        add(JSeparator(SwingConstants.HORIZONTAL).apply { maximumSize = Dimension(Int.MAX_VALUE, 1) })
        add(Box.createRigidArea(Dimension(0, 10)))
        add(JLabel("<html><b>API Version:</b> ${WorldConverterAPI.VERSION}</html>"))
        add(JLabel("<html><b>GUI Version:</b> ${WorldConverterAPI.VERSION}</html>"))
        add(Box.createRigidArea(Dimension(0, 10)))
        add(
            JButton("<html><b>License:</b> <a href='https://www.gnu.org/licenses/agpl-3.0.html'>GNU Affero General Public License v3</a></html>")
                .labelUri("https://www.gnu.org/licenses/agpl-3.0.html")
        )
        add(
            JButton("<html><b>Source:</b> <a href='${WorldConverterGUI.SOURCE_URL}'>${WorldConverterGUI.SOURCE_URL}</a></html>")
                .labelUri(WorldConverterGUI.SOURCE_URL)
        )
    }

    private val midPanel = JPanel(BorderLayout()).apply {
        add(mainContentPanel, BorderLayout.CENTER)
        if (gui.logo != null) {
            add(JLabel(ImageIcon(gui.logo.scaleDown(0.5))), BorderLayout.LINE_START)
        }
    }

    private val mainPanel = JPanel(BorderLayout()).apply {
        add(midPanel, BorderLayout.CENTER)
        val bottomPane = JPanel(BorderLayout()).apply {
            add(buttonsPanel, BorderLayout.LINE_END)
        }
        add(bottomPane, BorderLayout.SOUTH)

        registerKeyboardAction(
            { dialog.dispose() },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        )
    }

    private val dialog = JDialog(gui.main.frame, "About PowerNukkit Universal Converters").apply {
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        isModal = true
        minimumSize = Dimension(300, 150)
    }

    init {
        with(dialog) {
            contentPane = mainPanel
            pack()
            setLocationRelativeTo(owner)
            isResizable = false
            okButton.requestFocus()
            isVisible = true
        }
    }

    companion object
}
