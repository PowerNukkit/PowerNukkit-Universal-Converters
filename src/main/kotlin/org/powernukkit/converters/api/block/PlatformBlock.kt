/*
 * PowerNukkit Universal Worlds & Converters for Minecraft
 *  Copyright (C) 2020  José Roberto de Araújo Júnior
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
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.powernukkit.converters.api.block

import org.powernukkit.converters.api.Platform
import org.powernukkit.converters.api.PlatformObject
import org.powernukkit.converters.api.entity.PlatformEntity
import org.powernukkit.converters.math.BlockPos

/**
 * @author joserobjr
 * @since 2020-10-11
 */
abstract class PlatformBlock<P: Platform<P>>(
    override val platform: P,
    val pos: BlockPos
): PlatformObject<P> {
    protected abstract val blockLayers: List<PlatformBlockState<P>>
    protected abstract val blockEntity: PlatformBlockEntity<P>?
    protected abstract val entities: List<PlatformEntity<P>>
    override fun toString(): String {
        return "${platform.name}Block(pos=$pos, blockLayers=$blockLayers, blockEntity=$blockEntity, entities=$entities)"
    }
}
