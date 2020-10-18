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
open class StructureConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,
    val blockConverter: BlockConverter<FromPlatform, ToPlatform>,
) {
    fun convertAll(fromStructures: Flow<PlatformStructure<FromPlatform>>): Flow<PlatformStructure<ToPlatform>> {
        val singleBlockStructureCache = mutableMapOf<PlatformBlock<FromPlatform>, PlatformStructure<ToPlatform>>()
        return fromStructures.map {
            convert(it, singleBlockStructureCache)
        }
    }

    protected open fun convert(
        fromStructure: PlatformStructure<FromPlatform>,
        singleBlockStructureCache: MutableMap<PlatformBlock<FromPlatform>, PlatformStructure<ToPlatform>>,
    ): PlatformStructure<ToPlatform> {
        val size = fromStructure.blocks.size

        val toStructure = toPlatform.createStructure(size)

        if (size == 1) {
            convertSingleStructure(fromStructure, toStructure, singleBlockStructureCache)
        } else {
            convertMultiStructure(fromStructure, toStructure)
        }
        return toStructure
    }

    protected open fun convertSingleStructure(
        fromStructure: PlatformStructure<FromPlatform>,
        toStructure: PlatformStructure<ToPlatform>,
        singleBlockStructureCache: MutableMap<PlatformBlock<FromPlatform>, PlatformStructure<ToPlatform>>
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
        fromStructure: PlatformStructure<FromPlatform>,
        toStructure: PlatformStructure<ToPlatform>,
    ) {
        fromStructure.blocks.forEach { (pos, block) ->
            blockConverter.convert(fromStructure, pos, block, toStructure)
        }
    }
}
