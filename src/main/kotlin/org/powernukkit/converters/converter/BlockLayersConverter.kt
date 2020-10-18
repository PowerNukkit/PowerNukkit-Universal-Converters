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

package org.powernukkit.converters.converter

import org.powernukkit.converters.platform.api.BlockContainer
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlock
import org.powernukkit.converters.platform.api.block.PlatformBlockState

/**
 * @author joserobjr
 * @since 2020-10-17
 */
open class BlockLayersConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,

    val blockStateConverter: BlockStateConverter<FromPlatform, ToPlatform>,
    val adapters: Adapters<NamespacedId, BlockLayersAdapter<FromPlatform, ToPlatform>>? = null,
) {
    open fun convert(
        fromLayers: List<PlatformBlockState<FromPlatform>>,
        fromBlock: PlatformBlock<FromPlatform>,
        fromContainer: BlockContainer<FromPlatform>,
    ): List<PlatformBlockState<ToPlatform>> {
        if (adapters == null) {
            return fromLayers.flatMapIndexed { fromLayer: Int, fromBlockState: PlatformBlockState<FromPlatform> ->
                blockStateConverter.convert(fromBlockState, fromLayer, fromLayers, fromBlock, fromContainer)
            }
        }

        val context = FullBlockLayersConversionContext(fromPlatform, toPlatform, fromLayers, fromBlock, fromContainer)

        fun List<BlockLayersAdapter<FromPlatform, ToPlatform>>.applyEntireBlockStateLayersAdapters() {
            forEach { adapter ->
                adapter.adaptEntireBlockStateLayers(context)
            }
        }

        fun List<BlockLayersAdapter<FromPlatform, ToPlatform>>.applyLayerAdapters(
            context: BlockLayerConversionContext<FromPlatform, ToPlatform>,
        ) {
            return forEach { adapter ->
                adapter.adaptBlockStateToLayers(context)
            }
        }

        adapters.firstAdapters.applyEntireBlockStateLayersAdapters()
        adapters.fromAdapters[fromLayers.first().type.id]?.applyEntireBlockStateLayersAdapters()

        if (!context.layersRequiresAdapter) {
            context.toLayers =
                fromLayers.flatMapIndexed { fromLayer: Int, fromBlockState: PlatformBlockState<FromPlatform> ->
                    val subContext = BlockLayerConversionContext(context, fromLayer, fromBlockState)

                    adapters.firstAdapters.applyLayerAdapters(subContext)

                    adapters.fromAdapters[fromBlockState.type.id]?.applyLayerAdapters(subContext)

                    subContext.toBlockStateLayers.takeUnless { it.isNullOrEmpty() }?.first()?.type?.id
                        ?.let { adapters.toAdapters[it]?.applyLayerAdapters(subContext) }

                    adapters.lastAdapters.applyLayerAdapters(subContext)

                    subContext.toBlockStateLayers.takeUnless { it.isNullOrEmpty() }?.first()?.type?.id
                        ?.let { adapters.lastToAdapters[it]?.applyLayerAdapters(subContext) }

                    if (subContext.toBlockStateLayers.isNullOrEmpty() && subContext.requiresAdapter) {
                        error("Could not convert the block state in $subContext to a list of block layers")
                    }

                    subContext.toBlockStateLayers.takeUnless { it.isNullOrEmpty() }
                        ?: blockStateConverter.convert(fromBlockState, fromLayer, fromLayers, fromBlock, fromContainer)
                }
        }

        context.toLayers.takeUnless { it.isNullOrEmpty() }?.first()?.type?.id?.let {
            adapters.toAdapters[it]?.applyEntireBlockStateLayersAdapters()
        }

        adapters.lastAdapters.applyEntireBlockStateLayersAdapters()

        context.toLayers.takeUnless { it.isNullOrEmpty() }?.first()?.type?.id?.let {
            adapters.lastToAdapters[it]?.applyEntireBlockStateLayersAdapters()
        }

        return context.toLayers.takeUnless { it.isNullOrEmpty() }
            ?: error("Could not convert the layers from the context $context")
    }
}
