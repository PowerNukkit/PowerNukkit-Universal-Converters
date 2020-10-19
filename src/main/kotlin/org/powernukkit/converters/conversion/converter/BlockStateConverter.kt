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
import org.powernukkit.converters.conversion.adapter.BlockStateAdapter
import org.powernukkit.converters.conversion.context.BlockLayersSingleConversionContext
import org.powernukkit.converters.conversion.context.BlockStateConversionContext
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlockState

/**
 * @author joserobjr
 * @since 2020-10-17
 */
open class BlockStateConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,

    val blockTypeConverter: BlockTypeConverter<FromPlatform, ToPlatform>,
    val blockPropertyValuesConverter: BlockPropertyValuesConverter<FromPlatform, ToPlatform>,

    val adapters: Adapters<BlockStateAdapter<FromPlatform, ToPlatform>>? = null
) {
    open fun convert(
        fromState: PlatformBlockState<FromPlatform>,
        context: BlockLayersSingleConversionContext<FromPlatform, ToPlatform>,
    ): List<PlatformBlockState<ToPlatform>> {
        val context = BlockStateConversionContext(fromState, context)

        fun List<BlockStateAdapter<FromPlatform, ToPlatform>>.applyAdapters() {
            forEach { adapter ->
                adapter.adaptBlockState(context)
            }
        }

        if (adapters != null) {
            adapters.firstAdapters.applyAdapters()
            adapters.fromAdapters[fromState.type.id]?.applyAdapters()
        }

        if (context.toMainBlockType == null && !context.typeRequiresAdapter) {
            context.toMainBlockType = blockTypeConverter.convert(fromState.type, context)
        }

        adapters?.midAdapters?.applyAdapters()

        context.toMainBlockType?.let { toType ->
            adapters?.toAdapters?.get(toType.id)?.applyAdapters()
        }

        context.toMainBlockType?.let { toType ->
            if (context.toMainBlockPropertyValues == null && !context.valuesRequiresAdapter) {
                val (type, values) = blockPropertyValuesConverter.convert(
                    fromState.values, toType, context
                )
                context.toMainBlockType = type
                context.toMainBlockPropertyValues = values
            }
        }

        if (adapters != null) {
            adapters.midAdapters.applyAdapters()
            context.toMainBlockType?.let { adapters.toAdapters[it.id]?.applyAdapters() }
            adapters.lastAdapters.applyAdapters()
            context.toMainBlockType?.let { adapters.lastToAdapters[it.id]?.applyAdapters() }
        }

        fun List<BlockStateAdapter<FromPlatform, ToPlatform>>.applyListAdapters() {
            forEach { adapter ->
                adapter.adaptBlockStateList(context)
            }
        }

        if (adapters != null) {
            adapters.firstAdapters.applyListAdapters()
            adapters.fromAdapters[fromState.type.id]?.applyListAdapters()
            adapters.midAdapters.applyListAdapters()
            context.toBlockStates.takeUnless { it.isNullOrEmpty() }?.first()?.type?.id?.let { toId ->
                adapters.toAdapters[toId]?.applyListAdapters()
            }
            adapters.lastAdapters.applyListAdapters()
            context.toBlockStates.takeUnless { it.isNullOrEmpty() }?.first()?.type?.id?.let { toId ->
                adapters.lastToAdapters[toId]?.applyListAdapters()
            }
        }


        return context.toBlockStates.takeUnless { it.isNullOrEmpty() }
            ?: listOf(context.toCompletedState())
    }
}
