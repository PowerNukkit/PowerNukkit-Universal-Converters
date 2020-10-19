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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlock
import org.powernukkit.converters.platform.api.block.PlatformStructure
import org.powernukkit.converters.platform.api.block.createStructure

/**
 * @author joserobjr
 * @since 2020-10-15
 */
open class StructureConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,
    val blockConverter: BlockConverter<FromPlatform, ToPlatform>,
) {
    fun CoroutineScope.convertAll(
        fromStructures: ReceiveChannel<PlatformStructure<FromPlatform>>,
        toStructures: SendChannel<PlatformStructure<ToPlatform>>,
        problems: SendChannel<ConversionProblem>? = null,
    ) = launch {
        try {
            val singleBlockStructureCache = mutableMapOf<PlatformBlock<FromPlatform>, PlatformStructure<ToPlatform>>()
            for (fromStructure in fromStructures) {
                val (toStructure, conversionProblems) = convert(fromStructure, singleBlockStructureCache)
                if (toStructure.blocks.isNotEmpty()) {
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

    fun convert(from: PlatformStructure<FromPlatform>): Pair<PlatformStructure<ToPlatform>, List<ConversionProblem>> {
        return convert(from, mutableMapOf())
    }

    protected open fun convert(
        fromStructure: PlatformStructure<FromPlatform>,
        singleBlockStructureCache: MutableMap<PlatformBlock<FromPlatform>, PlatformStructure<ToPlatform>>,
    ): Pair<PlatformStructure<ToPlatform>, List<ConversionProblem>> {
        val size = fromStructure.blocks.size

        val toStructure = toPlatform.createStructure(size)

        val problems = if (size == 1) {
            convertSingleStructure(fromStructure, toStructure, singleBlockStructureCache)
        } else {
            convertMultiStructure(fromStructure, toStructure)
        }
        return toStructure to problems
    }

    protected open fun convertSingleStructure(
        fromStructure: PlatformStructure<FromPlatform>,
        toStructure: PlatformStructure<ToPlatform>,
        singleBlockStructureCache: MutableMap<PlatformBlock<FromPlatform>, PlatformStructure<ToPlatform>>
    ): List<ConversionProblem> {
        val (pos, block) = fromStructure.blocks.entries.first()
        var problems = emptyList<ConversionProblem>()
        val cacheStructure = singleBlockStructureCache.computeIfAbsent(block) { _ ->
            toStructure.createStructure(1).also {
                problems = blockConverter.convert(block, pos, fromStructure, it)
            }
        }

        if (problems.isNotEmpty()) {
            singleBlockStructureCache.remove(block)
        }

        toStructure.merge(cacheStructure, pos)
        return problems
    }

    protected open fun convertMultiStructure(
        fromStructure: PlatformStructure<FromPlatform>,
        toStructure: PlatformStructure<ToPlatform>,
    ): List<ConversionProblem> {
        val problems = mutableListOf<ConversionProblem>()
        fromStructure.blocks.forEach { (pos, block) ->
            problems += blockConverter.convert(block, pos, fromStructure, toStructure)
        }
        return problems
    }
}
