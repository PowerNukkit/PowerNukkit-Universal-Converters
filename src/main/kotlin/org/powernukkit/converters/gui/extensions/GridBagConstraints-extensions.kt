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

package org.powernukkit.converters.gui.extensions

import java.awt.GridBagConstraints
import java.awt.Insets

/**
 * @author joserobjr
 * @since 2020-11-13
 */
fun gridBagData(
    col: Int,
    row: Int,
    width: Int = 1,
    height: Int = 1,
    weightX: Double = 0.0,
    weightY: Double = 0.0,
    anchor: GBAnchor = GBAnchor.CENTER,
    fill: GBFill = GBFill.NONE,
    insets: Insets = Insets(0, 0, 0, 0),
    internalPaddingX: Int = 0,
    internalPaddingY: Int = 0,
) = GridBagConstraints(
    col, row, width, height, weightX, weightY, anchor.magicValue, fill.magicValue,
    insets, internalPaddingX, internalPaddingY
)

enum class GBAnchor {
    CENTER,
    NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST,

    PAGE_START, PAGE_END,
    LINE_START, LINE_END,
    FIRST_LINE_START, FIRST_LINE_END,
    LAST_LINE_START, LAST_LINE_END,

    BASELINE, BASELINE_LEADING, BASELINE_TRAILING,
    ABOVE_BASELINE, ABOVE_BASELINE_LEADING, ABOVE_BASELINE_TRAILING,
    BELOW_BASELINE, BELOW_BASELINE_LEADING, BELOW_BASELINE_TRAILING
    ;

    val magicValue = GridBagConstraints::class.java.getDeclaredField(name).getInt(null)
}

enum class GBFill {
    NONE, HORIZONTAL, VERTICAL, BOTH
    ;

    val magicValue = GridBagConstraints::class.java.getDeclaredField(name).getInt(null)
}
