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

package org.powernukkit.converters.conversion.converter

import org.powernukkit.converters.conversion.adapter.Adapters
import org.powernukkit.converters.conversion.adapter.BlockTypeAdapter
import org.powernukkit.converters.conversion.context.BlockStateConversionContext
import org.powernukkit.converters.conversion.context.BlockTypeConversionContext
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlockType

/**
 * @author joserobjr
 * @since 2020-10-17
 */
open class BlockTypeConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,

    val adapters: Adapters<BlockTypeAdapter<FromPlatform, ToPlatform>>,
) {
    fun convert(
        fromType: PlatformBlockType<FromPlatform>,
        context: BlockStateConversionContext<FromPlatform, ToPlatform>,
    ): PlatformBlockType<ToPlatform> {
        val context = BlockTypeConversionContext(fromType, context)

        fun List<BlockTypeAdapter<FromPlatform, ToPlatform>>.applyAdapters() {
            forEach { adapter ->
                adapter.adaptBlockType(context)
            }
        }

        adapters.firstAdapters.applyAdapters()
        adapters.fromAdapters[fromType.id]?.applyAdapters()
        adapters.midAdapters.applyAdapters()
        context.toBlockType?.let { adapters.toAdapters[it.id]?.applyAdapters() }
        adapters.lastAdapters.applyAdapters()
        context.toBlockType?.let { adapters.lastToAdapters[it.id]?.applyAdapters() }

        return checkNotNull(context.toBlockType) {
            "Could not convert the block type $fromType from ${fromPlatform.name} to ${toPlatform.name}"
        }
    }

}
