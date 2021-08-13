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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.*

/**
 * @author joserobjr
 * @since 2020-10-15
 */
class StructureConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,
    val blockConverter: BlockConverter<FromPlatform, ToPlatform>,
) {
    fun CoroutineScope.convertAll(
        fromStructures: ReceiveChannel<PositionedStructure<FromPlatform>>,
        toStructures: SendChannel<PositionedStructure<ToPlatform>>,
        problems: SendChannel<ConversionProblem>? = null,
    ) = launch {
        try {
            val singleBlockStructureCache = mutableMapOf<PlatformBlock<FromPlatform>, ImmutableStructure<ToPlatform>>()
            for (fromStructure in fromStructures) {
                val (toStructure, conversionProblems) = convert(fromStructure, singleBlockStructureCache)
                if (toStructure.content.isNotEmpty()) {
                    toStructures.send(toStructure)
                }
                if (problems != null && conversionProblems.isNotEmpty()) {
                    conversionProblems.forEach { problems.send(it) }
                }
            }
        } finally {
            coroutineContext.cancelChildren()
        }
    }

    fun convert(from: PositionedStructure<FromPlatform>): Pair<PositionedStructure<ToPlatform>, List<ConversionProblem>> {
        return convert(from, mutableMapOf())
    }

    private fun convert(
        fromPositionedStructure: PositionedStructure<FromPlatform>,
        singleBlockStructureCache: MutableMap<PlatformBlock<FromPlatform>, ImmutableStructure<ToPlatform>>,
    ): Pair<PositionedStructure<ToPlatform>, List<ConversionProblem>> {
        val fromStructure = fromPositionedStructure.content
        val size = fromStructure.blocks.size

        val (toStructure, problems) = if (size == 1) {
            convertSingleStructure(fromStructure, singleBlockStructureCache)
        } else {
            convertMultiStructure(fromStructure)
        }
        return PositionedStructure(fromPositionedStructure.worldPos, toStructure) to problems
    }

    private fun convertSingleStructure(
        fromStructure: ImmutableStructure<FromPlatform>,
        singleBlockStructureCache: MutableMap<PlatformBlock<FromPlatform>, ImmutableStructure<ToPlatform>>
    ): Pair<ImmutableStructure<ToPlatform>, List<ConversionProblem>> {
        var problems = emptyList<ConversionProblem>()
        val cacheStructure = singleBlockStructureCache.computeIfAbsent(fromStructure.mainBlock) { fromBlock ->
            val toStructure = MutableStructure(toPlatform, emptyMap())
            problems = blockConverter.convert(fromBlock, BlockPos.ZERO, fromStructure, toStructure)
            toStructure.toImmutableStructure()
        }

        if (problems.isNotEmpty()) {
            singleBlockStructureCache.remove(fromStructure.mainBlock)
        }

        return cacheStructure to problems
    }

    private fun convertMultiStructure(
        fromStructure: PlatformStructure<FromPlatform>,
    ): Pair<ImmutableStructure<ToPlatform>, List<ConversionProblem>> {
        val problems = mutableListOf<ConversionProblem>()
        val toStructure = MutableStructure(toPlatform, emptyMap())
        fromStructure.blocks.forEach { (pos, block) ->
            problems += blockConverter.convert(block, pos, fromStructure, toStructure)
        }
        return toStructure.toImmutableStructure() to problems
    }
}
