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

package org.powernukkit.converters.math

import br.com.gamemods.regionmanipulator.ChunkPos

/**
 * @author joserobjr
 * @since 2020-10-10
 */
data class BlockPos(val xPos: Int, val yPos: Int, val zPos: Int) {
    operator fun plus(pos: BlockPos) = BlockPos(
        xPos + pos.xPos,
        yPos + pos.yPos,
        zPos + pos.zPos,
    )

    val chunkPos: ChunkPos get() = ChunkPos(xPos shr 4, zPos shr 4)

    override fun toString(): String {
        return "[$xPos, $yPos, $zPos]"
    }

    companion object {
        val ZERO = BlockPos(0, 0, 0)
    }
}
