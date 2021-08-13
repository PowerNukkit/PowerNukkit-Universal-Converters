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

import javax.swing.JLabel

/**
 * @author joserobjr
 * @since 2020-11-12
 */
fun <L : JLabel> L.makeMultiline(): L {
    val adjusted = text
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\n", "<br/>")

    text = "<html>$adjusted</html>"
    return this
}

var JLabel.multilineText: String
    get() {
        var current = text
        if (!current.startsWith("<html>") || !current.endsWith("</html>")) {
            return text
        }
        current = current.substring(6, current.length - 7)
            .replace("<br/>", "\n")
            .replace("&gt;", ">")
            .replace("&lt;", "<")
        return current
    }
    set(value) {
        text = value
        makeMultiline()
    }
