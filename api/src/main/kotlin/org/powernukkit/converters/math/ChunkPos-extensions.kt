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

package org.powernukkit.converters.math

import br.com.gamemods.regionmanipulator.ChunkPos

/**
 * @author joserobjr
 * @since 2020-11-16
 */
operator fun ChunkPos.contains(blockPos: BlockPos): Boolean {
    val minX = xPos shl 4
    val minZ = zPos shl 4
    return blockPos.xPos >= minX && blockPos.zPos >= minZ
            && blockPos.xPos <= minX + 15 && blockPos.zPos <= minZ + 15
}

operator fun ChunkPos.contains(entityPos: EntityPos): Boolean {
    val minX = xPos shl 4
    val minZ = zPos shl 4
    return entityPos.xPos >= minX && entityPos.zPos >= minZ
            && entityPos.xPos <= minX + 15 && entityPos.zPos <= minZ + 15
}
