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

import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.api.Container
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlock
import org.powernukkit.converters.platform.api.block.PlatformStructure
import org.powernukkit.converters.platform.api.block.PositionedBlock

/**
 * @author joserobjr
 * @since 2020-10-16
 */
class BlockConverter<
        FromPlatform : Platform<FromPlatform, FromBlock>,
        FromBlock : PlatformBlock<FromPlatform>,
        ToPlatform : Platform<ToPlatform, ToBlock>,
        ToBlock : PlatformBlock<ToPlatform>,
        ToStructure : PlatformStructure<ToPlatform, ToBlock>,
        >(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform
) {
    fun convert(
        container: Container<BlockPos, PositionedBlock<FromPlatform, FromBlock>>,
        pos: BlockPos,
        fromBlock: FromBlock,
        toStructure: ToStructure
    ): ToBlock {
        TODO()
    }
}
