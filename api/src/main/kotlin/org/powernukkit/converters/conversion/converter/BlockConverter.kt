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
import org.powernukkit.converters.conversion.adapter.BlockAdapter
import org.powernukkit.converters.conversion.context.BlockConversionContext
import org.powernukkit.converters.internal.toMapOfList
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.math.BoundingBox
import org.powernukkit.converters.platform.api.BlockContainer
import org.powernukkit.converters.platform.api.MutableBlockContainer
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlock
import org.powernukkit.converters.platform.api.block.plus

/**
 * @author joserobjr
 * @since 2020-10-16
 */
open class BlockConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,

    val blockLayersConverter: BlockLayersConverter<FromPlatform, ToPlatform>,
    val blockEntityConverter: BlockEntityConverter<FromPlatform, ToPlatform>,
    val entityConverter: EntityConverter<FromPlatform, ToPlatform>,

    val adapters: Adapters<BlockAdapter<FromPlatform, ToPlatform>>? = null,
) {
    open fun convert(
        fromBlock: PlatformBlock<FromPlatform>,
        fromPos: BlockPos,
        fromContainer: BlockContainer<FromPlatform>,

        toContainer: MutableBlockContainer<ToPlatform>
    ): List<ConversionProblem> {
        val context = BlockConversionContext(
            fromPlatform, toPlatform,
            fromBlock, fromPos, fromContainer,
            toContainer
        )

        if (adapters != null) {
            adapters.firstAdapters.forEach { it.adaptBlock(context) }
            adapters.fromAdapters[fromBlock.mainState.type.id]?.forEach { it.adaptBlock(context) }
        }

        try {
            context.toLayers = blockLayersConverter.convert(fromBlock.blockLayers, context)
            context.toBlockEntity = blockEntityConverter.convert(fromBlock.blockEntity, context)
            context.toEntities = entityConverter.convertList(fromBlock.entities, context)

            val toLayers = context.toLayers.takeUnless { it.isNullOrEmpty() }
                ?: error("Failed to convert the block layers of $context")

            val toBlockEntity = context.toBlockEntity
            val toEntities = checkNotNull(context.toEntities) { "Failed to convert the entities of $context" }

            if (toEntities.all { it.pos in BoundingBox.SIMPLE_BOX }) {
                toContainer[fromPos] = toPlatform.createPlatformBlock(toLayers, toBlockEntity, toEntities)
            } else {
                val sameBlock = toEntities.filter { it.pos in BoundingBox.SIMPLE_BOX }
                toContainer[fromPos] = toPlatform.createPlatformBlock(toLayers, toBlockEntity, sameBlock)

                val otherBlocks = toEntities.asSequence()
                    .filter { it.pos !in BoundingBox.SIMPLE_BOX }
                    .map {
                        val blockPos = it.pos.toBlockPos()
                        blockPos to it.withPos(it.pos - blockPos)
                    }
                    .toMapOfList()

                otherBlocks.forEach { (blockPos, blockEntities) ->
                    val currentBlock = toContainer.getBlock(blockPos) ?: toPlatform.airBlock
                    val blockWithEntities =
                        toPlatform.createPlatformBlock(toPlatform.airBlockState, entities = blockEntities)
                    toContainer[blockPos] = currentBlock + blockWithEntities
                }
            }
            
            return emptyList()
        } catch (e: Exception) {
            context += ConversionProblem("An exception has been caught while converting the block $fromBlock: $e", e)
            return context.problems
        }
    }
}
