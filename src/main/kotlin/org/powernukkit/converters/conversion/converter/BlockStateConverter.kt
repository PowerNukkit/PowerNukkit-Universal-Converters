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
import org.powernukkit.converters.platform.api.NamespacedId
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

    val adapters: Adapters<NamespacedId, BlockStateAdapter<FromPlatform, ToPlatform>>? = null
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

        if (context.toType == null && !context.typeRequiresAdapter) {
            context.toType = blockTypeConverter.convert(fromState.type, context)
        }

        context.toType?.let { toType ->
            adapters?.toAdapters?.get(toType.id)?.applyAdapters()
        }

        context.toType?.let { toType ->
            if (context.toPropertyValues == null && !context.valuesRequiresAdapter) {
                context.toPropertyValues = blockPropertyValuesConverter.convert(
                    fromState.values, toType, context
                )
            }
        }

        if (adapters != null) {
            context.toType?.let { adapters.toAdapters[it.id]?.applyAdapters() }
            adapters.lastAdapters.applyAdapters()
            context.toType?.let { adapters.lastToAdapters[it.id]?.applyAdapters() }
        }

        fun List<BlockStateAdapter<FromPlatform, ToPlatform>>.applyListAdapters() {
            forEach { adapter ->
                adapter.adaptBlockStateList(context)
            }
        }

        if (adapters != null) {
            adapters.firstAdapters.applyListAdapters()
            adapters.fromAdapters[fromState.type.id]?.applyListAdapters()
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
