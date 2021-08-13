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

package org.powernukkit.converters.gui.extensions

import java.awt.Color
import java.awt.Cursor
import java.awt.Desktop
import java.awt.Insets
import java.net.URI
import javax.swing.JButton
import javax.swing.SwingConstants


/**
 * @author joserobjr
 * @since 2020-11-12
 */
fun <B : JButton> B.labelUri(uri: String): B {
    horizontalAlignment = SwingConstants.LEFT
    isBorderPainted = false
    isOpaque = false
    background = Color.WHITE
    toolTipText = uri

    cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
    margin = Insets(0, 0, 0, 0)
    isContentAreaFilled = false

    addActionListener {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI(uri))
        }
    }
    return this
}
