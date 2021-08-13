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
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.powernukkit.converters.conversion.job.ConversionProcess
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PositionedStructure
import org.powernukkit.converters.storage.api.Chunk
import org.powernukkit.converters.storage.api.ProviderWorld
import org.powernukkit.converters.storage.api.ReceivingWorld
import org.powernukkit.converters.storage.api.StorageProblemManager

/**
 * @author joserobjr
 * @since 2020-10-19
 */
abstract class PlatformConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,
) {
    abstract fun convertStructure(
        from: PositionedStructure<FromPlatform>
    ): Pair<PositionedStructure<ToPlatform>, List<ConversionProblem>>

    abstract fun CoroutineScope.convertAllStructures(
        from: ReceiveChannel<PositionedStructure<FromPlatform>>,
        to: SendChannel<PositionedStructure<ToPlatform>>,
        problems: SendChannel<ConversionProblem>? = null,
    ): Job

    abstract fun convertChunkData(fromChunk: Chunk<FromPlatform>, toChunk: Chunk<ToPlatform>): List<ConversionProblem>

    fun CoroutineScope.convertWorldAsync(
        from: ProviderWorld<FromPlatform>,
        to: ReceivingWorld<ToPlatform>,
        problemManager: StorageProblemManager
    ): ConversionProcess<FromPlatform, ToPlatform> {
        val chunksConverted = MutableStateFlow(0L)
        val job = launch {
            from.chunkFlow().collect { chunk ->
                val currentChunkPos = chunk.chunkPos
                val toChunk = to.getOrCreateEmptyChunk(currentChunkPos)
                convertChunkData(chunk, toChunk)
                chunk.structureFlow().collect {
                    val (newStructure, problems) = convertStructure(it)
                    val finalStructure = newStructure.takeIf { problems.isNotEmpty() }
                        ?: problemManager.handleConvertStructureProblems(this@PlatformConverter, it, newStructure, problemManager)
                    finalStructure.chunkPositions().forEach { chunkPos ->
                        val chunkToAdd =
                            if (chunkPos == currentChunkPos) toChunk else to.getOrCreateEmptyChunk(chunkPos)
                        chunkToAdd += newStructure
                    }
                }
                chunksConverted.update { it + 1 }
            }
        }

        return ConversionProcess(this@PlatformConverter, from, to, chunksConverted.asStateFlow(), job)
    }
}
