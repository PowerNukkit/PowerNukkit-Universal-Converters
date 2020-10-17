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

import com.google.common.collect.MapMaker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlock
import org.powernukkit.converters.platform.api.block.PlatformStructure
import org.powernukkit.converters.platform.api.block.createStructure

/**
 * @author joserobjr
 * @since 2020-10-15
 */
class StructureConverter<
        FromPlatform : Platform<FromPlatform, FromBlock>,
        FromBlock : PlatformBlock<FromPlatform>,
        FromStructure : PlatformStructure<FromPlatform, FromBlock>,
        ToPlatform : Platform<ToPlatform, ToBlock>,
        ToBlock : PlatformBlock<ToPlatform>,
        ToStructure : PlatformStructure<ToPlatform, ToBlock>,
        >(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,
) {
    val blockConverter = BlockConverter(fromPlatform, toPlatform)

    // TODO Replace caching with a Kotlin coroutine semantic (I just don't know how yet lol) 
    private val singleBlockStructureCache = MapMaker().weakKeys().weakValues().makeMap<FromBlock, ToStructure>()

    // TODO Still deciding if we should use Flow or Channel
    fun convertAll(fromStructures: Flow<FromStructure>) = fromStructures.map(this::convert)

    private fun convert(fromStructure: FromStructure): ToStructure {
        val size = fromStructure.blocks.size

        @Suppress("UNCHECKED_CAST")
        val toStructure = toPlatform.createStructure(size) as ToStructure

        if (size == 1) {
            convertSingleStructure(fromStructure, toStructure)
        } else {
            convertMultiStructure(fromStructure, toStructure)
        }
        return toStructure
    }

    private fun convertSingleStructure(fromStructure: FromStructure, toStructure: ToStructure) {
        val (pos, block) = fromStructure.blocks.entries.first()
        val cacheStructure = singleBlockStructureCache.computeIfAbsent(block) { _ ->
            toStructure.createStructure(1).also {
                blockConverter.convert(fromStructure, pos, block, it)
            }
        }

        toStructure.merge(cacheStructure, pos)
    }

    private fun convertMultiStructure(fromStructure: FromStructure, toStructure: ToStructure) {
        fromStructure.blocks.forEach { (pos, block) ->
            blockConverter.convert(fromStructure, pos, block, toStructure)
        }
    }
}
