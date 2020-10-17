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
import org.powernukkit.converters.platform.api.block.PlatformBlockEntity
import org.powernukkit.converters.platform.api.block.PlatformBlockState

/**
 * @author joserobjr
 * @since 2020-10-17
 */
open class BlockEntityConverter<
        FromPlatform : Platform<FromPlatform, FromBlock>,
        FromBlock : PlatformBlock<FromPlatform>,
        ToPlatform : Platform<ToPlatform, ToBlock>,
        ToBlock : PlatformBlock<ToPlatform>,
        >(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,
) {
    open fun convert(
        blockEntity: PlatformBlockEntity<FromPlatform>?,
        fromBlock: FromBlock,
        fromContainer: BlockContainer<FromPlatform, FromBlock>,
        convertedLayers: List<PlatformBlockState<ToPlatform>>
    ): PlatformBlockEntity<ToPlatform>? {
        TODO("Not yet implemented")
    }
}
