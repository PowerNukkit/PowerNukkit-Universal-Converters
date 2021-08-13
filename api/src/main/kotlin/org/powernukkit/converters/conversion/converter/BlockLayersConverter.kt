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
import org.powernukkit.converters.conversion.adapter.BlockLayersAdapter
import org.powernukkit.converters.conversion.context.BlockConversionContext
import org.powernukkit.converters.conversion.context.BlockLayersFullConversionContext
import org.powernukkit.converters.conversion.context.BlockLayersSingleConversionContext
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlockState

/**
 * @author joserobjr
 * @since 2020-10-17
 */
open class BlockLayersConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,

    val blockStateConverter: BlockStateConverter<FromPlatform, ToPlatform>,
    val adapters: Adapters<BlockLayersAdapter<FromPlatform, ToPlatform>>? = null,
) {
    private fun List<BlockLayersAdapter<FromPlatform, ToPlatform>>.applyEntireBlockStateLayersAdapters(
        context: BlockLayersFullConversionContext<FromPlatform, ToPlatform>
    ) {
        forEach { adapter ->
            adapter.adaptEntireBlockStateLayers(context)
        }
    }

    private fun List<BlockLayersAdapter<FromPlatform, ToPlatform>>.applyLayerAdapters(
        context: BlockLayersSingleConversionContext<FromPlatform, ToPlatform>,
    ) {
        return forEach { adapter ->
            adapter.adaptBlockStateToLayers(context)
        }
    }

    open fun convert(
        fromLayers: List<PlatformBlockState<FromPlatform>>,
        context: BlockConversionContext<FromPlatform, ToPlatform>,
    ): List<PlatformBlockState<ToPlatform>> {
        val context = BlockLayersFullConversionContext(fromLayers, context)
        if (adapters == null) {
            return fromLayers.flatMapIndexed { fromLayer: Int, fromBlockState: PlatformBlockState<FromPlatform> ->
                blockStateConverter.convert(
                    fromBlockState,
                    BlockLayersSingleConversionContext(fromLayer, context)
                )
            }
        }

        adapters.firstAdapters.applyEntireBlockStateLayersAdapters(context)
        adapters.fromAdapters[fromLayers.first().type.id]?.applyEntireBlockStateLayersAdapters(context)

        if (!context.layersRequiresAdapter) {
            context.toLayers =
                fromLayers.flatMapIndexed { fromLayer: Int, fromBlockState: PlatformBlockState<FromPlatform> ->
                    val subContext = BlockLayersSingleConversionContext(fromLayer, context)

                    adapters.firstAdapters.applyLayerAdapters(subContext)

                    adapters.fromAdapters[fromBlockState.type.id]?.applyLayerAdapters(subContext)

                    adapters.midAdapters.applyLayerAdapters(subContext)

                    subContext.toBlockStateLayers.takeUnless { it.isNullOrEmpty() }?.first()?.type?.id
                        ?.let { adapters.toAdapters[it]?.applyLayerAdapters(subContext) }

                    adapters.lastAdapters.applyLayerAdapters(subContext)

                    subContext.toBlockStateLayers.takeUnless { it.isNullOrEmpty() }?.first()?.type?.id
                        ?.let { adapters.lastToAdapters[it]?.applyLayerAdapters(subContext) }

                    if (subContext.toBlockStateLayers.isNullOrEmpty() && subContext.requiresAdapter) {
                        error("Could not convert the block state in $subContext to a list of block layers")
                    }

                    subContext.toBlockStateLayers.takeUnless { it.isNullOrEmpty() }
                        ?: blockStateConverter.convert(fromBlockState, subContext)
                }
        }

        adapters.midAdapters.applyEntireBlockStateLayersAdapters(context)

        context.toLayers.takeUnless { it.isNullOrEmpty() }?.first()?.type?.id?.let {
            adapters.toAdapters[it]?.applyEntireBlockStateLayersAdapters(context)
        }

        adapters.lastAdapters.applyEntireBlockStateLayersAdapters(context)

        context.toLayers.takeUnless { it.isNullOrEmpty() }?.first()?.type?.id?.let {
            adapters.lastToAdapters[it]?.applyEntireBlockStateLayersAdapters(context)
        }

        return context.toLayers.takeUnless { it.isNullOrEmpty() }
            ?: error("Could not convert the layers from the context $context")
    }
}
