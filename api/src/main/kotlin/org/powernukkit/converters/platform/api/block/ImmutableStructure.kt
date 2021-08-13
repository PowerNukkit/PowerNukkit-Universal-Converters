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

import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.api.Platform
import java.util.*

/**
 * @author joserobjr
 * @since 2020-10-19
 */
class ImmutableStructure<P : Platform<P>>(
    platform: P,
    blocks: Map<BlockPos, PlatformBlock<P>>,
) : PlatformStructure<P>(platform) {

    override val blocks: Map<BlockPos, PlatformBlock<P>> = Collections.unmodifiableMap(blocks.toMap())

    constructor(platform: P, mainBlock: PlatformBlock<P>) : this(platform, mapOf(BlockPos.ZERO to mainBlock))

    init {
        require(BlockPos.ZERO in blocks) {
            "Immutable platform structures must have at least one block at 0, 0, 0. Got: $blocks"
        }
    }

    override fun toImmutableStructure() = this

    fun positionedAt(worldPos: BlockPos) = PositionedStructure(worldPos, this)
}
