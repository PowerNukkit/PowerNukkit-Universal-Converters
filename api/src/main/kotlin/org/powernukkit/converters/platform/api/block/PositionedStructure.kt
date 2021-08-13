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

package org.powernukkit.converters.platform.api.block

import br.com.gamemods.regionmanipulator.ChunkPos
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.api.Platform

/**
 * @author joserobjr
 * @since 2020-10-19
 */
data class PositionedStructure<P : Platform<P>>(
    val worldPos: BlockPos,
    val content: ImmutableStructure<P>
) {
    fun chunkPositions(): Set<ChunkPos> {
        val blocks = content.blocks
        if (blocks.size == 1) {
            return setOf(worldPos.chunkPos)
        }
        return blocks.keys.map { (worldPos + it).chunkPos }.toSet()
    }
}
