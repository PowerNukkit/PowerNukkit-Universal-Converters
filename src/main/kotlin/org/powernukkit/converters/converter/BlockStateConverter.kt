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

    val blockTypeConverter: BlockTypeConverter<
            FromPlatform, ToPlatform
            > = BlockTypeConverter(fromPlatform, toPlatform),

    val blockPropertyValuesConverter: BlockPropertyValuesConverter<
            FromPlatform, ToPlatform
            > = BlockPropertyValuesConverter(fromPlatform, toPlatform)
) {
    open fun convert(
        fromState: PlatformBlockState<FromPlatform>,
        fromLayer: Int,
        fromLayers: List<PlatformBlockState<FromPlatform>>,
        fromBlock: PlatformBlock<FromPlatform>,
        fromContainer: BlockContainer<FromPlatform>
    ): List<PlatformBlockState<ToPlatform>> {
        val type =
            blockTypeConverter.convert(
                fromState.type,
                fromState, fromLayer, fromLayers, fromBlock, fromContainer
            )

        val values = blockPropertyValuesConverter.convert(
            fromState.values,
            type, fromState, fromLayer, fromLayers, fromBlock, fromContainer
        )

        val mainState = type.withState(values)
        return listOf(mainState)
    }
}
