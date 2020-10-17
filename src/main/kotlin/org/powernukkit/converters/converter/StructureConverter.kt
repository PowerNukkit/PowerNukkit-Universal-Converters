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
open class StructureConverter<
        FromPlatform : Platform<FromPlatform, FromBlock>,
        FromBlock : PlatformBlock<FromPlatform>,
        FromStructure : PlatformStructure<FromPlatform, FromBlock>,
        ToPlatform : Platform<ToPlatform, ToBlock>,
        ToBlock : PlatformBlock<ToPlatform>,
        ToStructure : PlatformStructure<ToPlatform, ToBlock>,
        >(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,
    val blockConverter: BlockConverter<
            FromPlatform, FromBlock, ToPlatform, ToBlock
            > = BlockConverter(fromPlatform, toPlatform),
) {
    fun convertAll(fromStructures: Flow<FromStructure>): Flow<ToStructure> {
        val singleBlockStructureCache = mutableMapOf<FromBlock, ToStructure>()
        return fromStructures.map {
            convert(it, singleBlockStructureCache)
        }
    }

    protected open fun convert(
        fromStructure: FromStructure,
        singleBlockStructureCache: MutableMap<FromBlock, ToStructure>,
    ): ToStructure {
        val size = fromStructure.blocks.size

        @Suppress("UNCHECKED_CAST")
        val toStructure = toPlatform.createStructure(size) as ToStructure

        if (size == 1) {
            convertSingleStructure(fromStructure, toStructure, singleBlockStructureCache)
        } else {
            convertMultiStructure(fromStructure, toStructure)
        }
        return toStructure
    }

    protected open fun convertSingleStructure(
        fromStructure: FromStructure,
        toStructure: ToStructure,
        singleBlockStructureCache: MutableMap<FromBlock, ToStructure>
    ) {
        val (pos, block) = fromStructure.blocks.entries.first()
        val cacheStructure = singleBlockStructureCache.computeIfAbsent(block) { _ ->
            toStructure.createStructure(1).also {
                blockConverter.convert(fromStructure, pos, block, it)
            }
        }

        toStructure.merge(cacheStructure, pos)
    }

    protected open fun convertMultiStructure(
        fromStructure: FromStructure,
        toStructure: ToStructure,
    ) {
        fromStructure.blocks.forEach { (pos, block) ->
            blockConverter.convert(fromStructure, pos, block, toStructure)
        }
    }
}
