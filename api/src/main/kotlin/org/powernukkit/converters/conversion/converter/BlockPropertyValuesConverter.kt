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

package org.powernukkit.converters.conversion.converter

import org.powernukkit.converters.conversion.adapter.Adapters
import org.powernukkit.converters.conversion.adapter.BlockPropertyValuesAdapter
import org.powernukkit.converters.conversion.context.BlockPropertyValuesConversionContext
import org.powernukkit.converters.conversion.context.BlockStateConversionContext
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlockPropertyValue
import org.powernukkit.converters.platform.api.block.PlatformBlockType

/**
 * @author joserobjr
 * @since 2020-10-17
 */
open class BlockPropertyValuesConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,

    val adapters: Adapters<BlockPropertyValuesAdapter<FromPlatform, ToPlatform>>,
) {
    open fun convert(
        fromValues: Map<String, PlatformBlockPropertyValue<FromPlatform>>,
        toType: PlatformBlockType<ToPlatform>,
        context: BlockStateConversionContext<FromPlatform, ToPlatform>,
    ): Pair<PlatformBlockType<ToPlatform>, Map<String, PlatformBlockPropertyValue<ToPlatform>>> {
        val context = BlockPropertyValuesConversionContext(fromValues, toType, context)

        fun List<BlockPropertyValuesAdapter<FromPlatform, ToPlatform>>.applyAdapters() {

            forEach { adapter ->
                adapter.adaptBlockPropertyValues(context)
            }

        }

        adapters.firstAdapters.applyAdapters()
        adapters.fromAdapters[context.fromBlockState.type.id]?.applyAdapters()
        adapters.midAdapters.applyAdapters()
        adapters.toAdapters[context.toBlockType.id]?.applyAdapters()
        adapters.lastAdapters.applyAdapters()
        adapters.lastToAdapters[context.toBlockType.id]?.applyAdapters()

        return context.toBlockType to checkNotNull(context.toBlockPropertyValues) {
            "Could not convert the properties from the block state ${context.fromBlockState} to type $toType, from platform ${fromPlatform.name} to ${toPlatform.name}"
        }
    }
}
