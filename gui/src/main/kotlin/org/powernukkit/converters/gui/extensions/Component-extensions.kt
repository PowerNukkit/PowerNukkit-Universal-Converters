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

import java.awt.Component
import java.awt.Dimension
import java.awt.Font

/**
 * @author joserobjr
 * @since 2020-11-12
 */
fun <C : Component> C.withMax(width: Int? = null, height: Int? = null): C {
    maximumSize = if (isMaximumSizeSet) {
        Dimension(width ?: maximumSize.width, height ?: maximumSize.height)
    } else {
        Dimension(width ?: Int.MAX_VALUE, height ?: Int.MAX_VALUE)
    }
    return this
}

fun <C : Component> C.bold(bold: Boolean = true): C {
    this.bold = bold
    return this
}

var Component.bold: Boolean
    get() = font.style and Font.BOLD == Font.BOLD
    set(bold) {
        font = font.deriveFont(if (bold) font.style or Font.BOLD else font.style and Font.BOLD.inv())
    }
