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

package org.powernukkit.converters.math

/**
 * @author joserobjr
 * @since 2020-10-17
 */
data class BoundingBox(
    val min: EntityPos,
    val max: EntityPos
) {
    val xRange = min.xPos until max.xPos
    val yRange = min.yPos until max.yPos
    val zRange = min.zPos until max.zPos

    operator fun contains(pos: EntityPos) =
        pos.xPos in xRange &&
                pos.yPos in yRange &&
                pos.zPos in zRange

    companion object {
        val SIMPLE_BOX = BoundingBox(EntityPos.ZERO, EntityPos.ONE)
    }
}
