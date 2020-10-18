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
open class BlockStateConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,

    val blockTypeConverter: BlockTypeConverter<FromPlatform, ToPlatform>,
    val blockPropertyValuesConverter: BlockPropertyValuesConverter<FromPlatform, ToPlatform>,

    val adapters: Adapters<NamespacedId, BlockStateAdapter<FromPlatform, ToPlatform>>? = null
) {
    open fun convert(
        fromState: PlatformBlockState<FromPlatform>,
        fromLayer: Int,
        fromLayers: List<PlatformBlockState<FromPlatform>>,
        fromBlock: PlatformBlock<FromPlatform>,
        fromContainer: BlockContainer<FromPlatform>
    ): List<PlatformBlockState<ToPlatform>> {
        val incomplete = IncompleteBlockState(toPlatform)
        var result: List<PlatformBlockState<ToPlatform>>? = null

        fun List<BlockStateAdapter<FromPlatform, ToPlatform>>.applyAdapters() {
            result = fold(result) { current, adapter ->
                adapter.adaptBlockState(
                    fromPlatform, toPlatform, fromState, incomplete, current,
                    fromLayer, fromLayers, fromBlock, fromContainer
                )
            }
        }

        if (adapters != null) {
            adapters.firstAdapters.applyAdapters()
            adapters.fromAdapters[fromState.type.id]?.applyAdapters()
        }

        if (incomplete.type == null && !incomplete.typeRequiresAdapter) {
            incomplete.type =
                blockTypeConverter.convert(
                    fromState.type,
                    fromState, fromLayer, fromLayers, fromBlock, fromContainer
                )
        }

        incomplete.type?.let { toType ->
            adapters?.toAdapters?.get(toType.id)?.applyAdapters()

            if (incomplete.values == null && !incomplete.valuesRequiresAdapter) {
                incomplete.values = blockPropertyValuesConverter.convert(
                    fromState.values, toType,
                    fromState, fromLayer, fromLayers, fromBlock, fromContainer
                )
            }
        }

        if (adapters != null) {
            incomplete.type?.let { adapters.toAdapters[it.id]?.applyAdapters() }
            adapters.lastAdapters.applyAdapters()
            incomplete.type?.let { adapters.lastToAdapters[it.id]?.applyAdapters() }
        }

        fun List<BlockStateAdapter<FromPlatform, ToPlatform>>.applyListAdapters() {
            result = fold(result) { current, adapter ->
                adapter.adaptBlockStateList(
                    fromPlatform, toPlatform, fromState, incomplete, current,
                    fromLayer, fromLayers, fromBlock, fromContainer
                )
            }
        }

        if (adapters != null) {
            adapters.firstAdapters.applyListAdapters()
            adapters.fromAdapters[fromState.type.id]?.applyListAdapters()
            result.takeUnless { it.isNullOrEmpty() }?.first()?.type?.id?.let { toId ->
                adapters.toAdapters[toId]?.applyListAdapters()
            }
            adapters.lastAdapters.applyListAdapters()
            result.takeUnless { it.isNullOrEmpty() }?.first()?.type?.id?.let { toId ->
                adapters.lastToAdapters[toId]?.applyListAdapters()
            }
        }


        return result.takeUnless { it.isNullOrEmpty() }
            ?: listOf(incomplete.toCompletedState())
    }
}
