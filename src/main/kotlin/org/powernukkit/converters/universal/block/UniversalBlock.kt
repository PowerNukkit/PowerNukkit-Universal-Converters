/*
 *     PowerNukkit Universal Worlds & Converters for Minecraft
 *     Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.powernukkit.converters.universal.block

import org.powernukkit.converters.api.block.PlatformBlock
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.universal.UniversalPlatform
import org.powernukkit.converters.universal.entity.UniversalEntity

/**
 * @author joserobjr
 * @since 2020-10-10
 */
class UniversalBlock(
    pos: BlockPos,
    override val blockLayers: List<UniversalBlockState>, 
    override var blockEntity: UniversalBlockEntity? = null,
    override val entities: List<UniversalEntity> = emptyList()
): PlatformBlock<UniversalPlatform>(UniversalPlatform, pos)
