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

package org.powernukkit.converters.conversion.context

import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlockPropertyValue
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.platform.api.block.PlatformBlockType

/**
 * @author joserobjr
 * @since 2020-10-18
 */
data class BlockStateConversionContext<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromBlockState: PlatformBlockState<FromPlatform>,
    val parentContext: BlockLayersSingleConversionContext<FromPlatform, ToPlatform>,
) {
    var type: PlatformBlockType<ToPlatform>? = null
    var values: Map<String, PlatformBlockPropertyValue<ToPlatform>>? = null
    var typeRequiresAdapter: Boolean = false
    var valuesRequiresAdapter: Boolean = false

    var result: List<PlatformBlockState<ToPlatform>>? = null

    val fromPlatform get() = parentContext.fromPlatform
    val toPlatform get() = parentContext.toPlatform
    val fromBlock get() = parentContext.fromBlock
    val fromContainer get() = parentContext.fromContainer
    val fromLayers get() = parentContext.fromLayers
    val fromLayer get() = parentContext.fromLayer

    fun toCompletedState(): PlatformBlockState<ToPlatform> {
        val type = requireNotNull(type) {
            "The type is not defined"
        }

        val values = requireNotNull(values) {
            "The properties are not defined"
        }

        return type.withState(values)
    }
}
