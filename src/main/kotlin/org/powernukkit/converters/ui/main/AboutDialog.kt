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
import org.powernukkit.converters.ui.extensions.*
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
    private val lang = gui.loadBundle("gui.dialog.about")

    private val okButton = JButton(action("OK") { dialog.dispose() })
    private val buttonsPanel = JPanel().apply {
        add(okButton)
    }

    private val mainContentPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = EmptyBorder(5, 15, 5, 5)
        add(JLabel("<html><h1>${lang["project.name"].htmlEncoded}</h1></html>"))
        add(JLabel(lang["dialog.about.tagline"].noHtml))
        add(Box.createRigidArea(Dimension(0, 10)))
        add(JSeparator(SwingConstants.HORIZONTAL).apply { maximumSize = Dimension(Int.MAX_VALUE, 1) })
        add(Box.createRigidArea(Dimension(0, 10)))
        add(JLabel("""<html><div WIDTH=400>${lang["dialog.about.description"].lineBreaks}</div></html>"""))
        add(Box.createRigidArea(Dimension(0, 10)))
        add(JSeparator(SwingConstants.HORIZONTAL).apply { maximumSize = Dimension(Int.MAX_VALUE, 1) })
        add(Box.createRigidArea(Dimension(0, 10)))
        add(JLabel("<html><b>${lang["dialog.about.version.api.label"].htmlEncoded}:</b> ${WorldConverterAPI.VERSION}</html>"))
        add(JLabel("<html><b>${lang["dialog.about.version.gui.label"].htmlEncoded}</b> ${WorldConverterGUI.VERSION}</html>"))
        add(Box.createRigidArea(Dimension(0, 10)))
        add(
            JButton(
                "<html><b>${lang["dialog.about.license.label"].htmlEncoded}</b> " +
                        "<a href='${lang["dialog.about.license.url"].htmlEncoded}'>${lang["dialog.about.license.name"].htmlEncoded}</a></html>"
            )
                .labelUri(lang["dialog.about.license.url"])
        )
        add(
            JButton("<html><b>${lang["dialog.about.source.label"].htmlEncoded}</b> <a href='${WorldConverterGUI.SOURCE_URL}'>${WorldConverterGUI.SOURCE_URL}</a></html>")
                .labelUri(WorldConverterGUI.SOURCE_URL)
        )
        add(Box.createRigidArea(Dimension(0, 20)))
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

    private val dialog = JDialog(gui.main.frame, lang["dialog.about.title"]).apply {
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        isModal = true
        //preferredSize = Dimension(725, 370)
    }

    init {
        with(dialog) {
            contentPane = mainPanel
            pack()
            pack()
            setLocationRelativeTo(owner)
            isResizable = false
            okButton.requestFocus()
            isVisible = true
        }
    }
}
