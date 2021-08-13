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

import org.powernukkit.converters.math.BoundingBox
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.PlatformObject
import org.powernukkit.converters.platform.api.entity.PlatformEntity

/**
 * @author joserobjr
 * @since 2020-10-11
 */
abstract class PlatformBlock<P : Platform<P>>(
    final override val platform: P
) : PlatformObject<P> {
    abstract val blockLayers: List<PlatformBlockState<P>>
    abstract val blockEntity: PlatformBlockEntity<P>?
    abstract val entities: List<PlatformEntity<P>>

    open val mainState get() = blockLayers.first()

    val isBlockAir
        get() = platform.airBlockState.let { air ->
            blockLayers.all { it == air }
        }

    protected fun validateEntities() {
        require(entities.all { it.pos in BoundingBox.SIMPLE_BOX })
    }

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlatformBlock<*>

        if (platform != other.platform) return false
        if (blockLayers != other.blockLayers) return false
        if (blockEntity != other.blockEntity) return false
        if (entities != other.entities) return false

        return true
    }

    final override fun hashCode(): Int {
        var result = platform.hashCode()
        result = 31 * result + blockLayers.hashCode()
        result = 31 * result + (blockEntity?.hashCode() ?: 0)
        result = 31 * result + entities.hashCode()
        return result
    }

    final override fun toString(): String {
        return "${platform.name}Block(blockLayers=$blockLayers, blockEntity=$blockEntity, entities=$entities)"
    }
}
