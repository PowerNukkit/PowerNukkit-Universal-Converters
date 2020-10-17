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

package org.powernukkit.converters.platform.api.block

import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.PlatformObject

/**
 * @author joserobjr
 * @since 2020-10-16
 */
data class PositionedBlock<
        P : Platform<P, Block>,
        Block : PlatformBlock<P>,
        >(
    val pos: BlockPos,
    val block: Block
) : PlatformObject<P> by block {
    val layers get() = block.blockLayers
    val blockEntity get() = block.blockEntity
    val entities by lazy {
        block.entities.map { it.withPos(it.pos + pos) }
    }

    fun moveTo(pos: BlockPos) = PositionedBlock(pos, block)

    override fun toString(): String {
        return "${platform.name}PositionedBlock(pos=$pos, layers=$layers, blockEntity=$blockEntity, entities=${block.entities.size}"
    }
}
