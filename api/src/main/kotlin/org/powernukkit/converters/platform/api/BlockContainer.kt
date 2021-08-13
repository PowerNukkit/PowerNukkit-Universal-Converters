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

package org.powernukkit.converters.platform.api

import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.api.block.PlatformBlock
import org.powernukkit.converters.platform.api.block.PositionedBlock
import org.powernukkit.converters.platform.api.block.positionedAt

/**
 * @author joserobjr
 * @since 2020-10-17
 */
interface BlockContainer<P : Platform<P>> : Container<BlockPos, PositionedBlock<P>> {
    val mainBlock: PlatformBlock<P>

    fun getBlock(pos: BlockPos): PlatformBlock<P>?

    override fun get(key: BlockPos) = getBlock(key)?.positionedAt(this, key)
}
