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
open class BlockConverter<
        FromPlatform : Platform<FromPlatform, FromBlock>,
        FromBlock : PlatformBlock<FromPlatform>,
        ToPlatform : Platform<ToPlatform, ToBlock>,
        ToBlock : PlatformBlock<ToPlatform>,
        >(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,

    val blockLayersConverter: BlockLayersConverter<
            FromPlatform, FromBlock, ToPlatform, ToBlock
            > = BlockLayersConverter(fromPlatform, toPlatform),

    val blockEntityConverter: BlockEntityConverter<
            FromPlatform, FromBlock, ToPlatform, ToBlock
            > = BlockEntityConverter(fromPlatform, toPlatform),

    val entityConverter: EntityConverter<
            FromPlatform, FromBlock, ToPlatform, ToBlock
            > = EntityConverter(fromPlatform, toPlatform),
) {
    open fun convert(
        fromContainer: BlockContainer<FromPlatform, FromBlock>,
        pos: BlockPos,
        fromBlock: FromBlock,

        toContainer: MutableBlockContainer<ToPlatform, ToBlock>
    ) {
        val layers = blockLayersConverter.convert(fromBlock.blockLayers, fromBlock, fromContainer)
        val blockEntity = blockEntityConverter.convert(fromBlock.blockEntity, fromBlock, fromContainer, layers)
        val entities = entityConverter.convert(fromBlock, fromContainer, layers, blockEntity)

        if (entities.all { it.pos in BoundingBox.SIMPLE_BOX }) {
            toContainer[pos] = toPlatform.createPlatformBlock(layers, blockEntity, entities)
        } else {
            val sameBlock = entities.filter { it.pos in BoundingBox.SIMPLE_BOX }
            toContainer[pos] = toPlatform.createPlatformBlock(layers, blockEntity, sameBlock)

            val otherBlocks = entities.asSequence()
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
    }
}
