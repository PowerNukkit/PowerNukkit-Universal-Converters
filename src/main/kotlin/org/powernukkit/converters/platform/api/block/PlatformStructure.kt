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
import org.powernukkit.converters.platform.api.PlatformObject

abstract class PlatformStructure<P : Platform<P>>(
    final override val platform: P,
    final override var worldPos: BlockPos,
) : PlatformObject<P>, MutableBlockContainer<P> {
    val blocks = mutableMapOf<BlockPos, PlatformBlock<P>>()

    var mainBlock
        get() = blocks[BlockPos.ZERO] ?: platform.airBlock
        set(value) {
            blocks[BlockPos.ZERO] = value
        }

    final override fun contains(key: BlockPos) = key in blocks
    override fun getBlock(pos: BlockPos) = blocks[pos]
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

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlatformStructure<*>

        if (platform != other.platform) return false
        if (blocks != other.blocks) return false

        return true
    }

    final override fun hashCode(): Int {
        var result = platform.hashCode()
        result = 31 * result + blocks.hashCode()
        return result
    }

    final override fun toString(): String {
        return "${platform.name}Structure(blocks=$blocks)"
    }
}
