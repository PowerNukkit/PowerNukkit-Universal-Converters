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
import org.powernukkit.converters.platform.api.MutableBlockContainer
import org.powernukkit.converters.platform.api.Platform

/**
 * @author joserobjr
 * @since 2020-10-19
 */
class MutableStructure<P : Platform<P>>(
    platform: P,
    blocks: Map<BlockPos, PlatformBlock<P>> = mapOf(BlockPos.ZERO to platform.airBlock)
) : MutableBlockContainer<P>, PlatformStructure<P>(
    platform
) {
    override var blocks = blocks.toMutableMap()

    override var mainBlock
        get() = blocks[BlockPos.ZERO] ?: platform.airBlock
        set(value) {
            blocks[BlockPos.ZERO] = value
        }

    override fun set(pos: BlockPos, block: PlatformBlock<P>) {
        blocks[pos] = block
    }

    fun merge(structure: PlatformStructure<P>, pos: BlockPos) {
        structure.blocks.forEach { (originalPos, block) ->
            blocks.compute(pos + originalPos) { _, previous ->
                (previous ?: platform.airBlock) + block
            }
        }
    }
}
