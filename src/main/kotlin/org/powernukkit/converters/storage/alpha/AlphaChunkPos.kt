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

package org.powernukkit.converters.storage.alpha

import br.com.gamemods.regionmanipulator.ChunkPos

/**
 * @author joserobjr
 * @since 2020-11-16
 */
inline class AlphaChunkPos(
    val asChunkPos: ChunkPos
) {
    constructor(xPos: Int, zPos: Int) : this(ChunkPos(xPos, zPos))
    constructor(xName: String, zName: String) : this(ChunkPos(xName.toInt(36), zName.toInt(36)))

    inline val xPos get() = asChunkPos.xPos
    inline val zPos get() = asChunkPos.zPos

    val xName get() = xPos.toString(36)
    val zName get() = zPos.toString(36)

    val folderPos get() = AlphaFolderPos(xPos % 64, zPos % 64)
    val path get() = folderPos.path.resolve("c.$xName.$zName.dat")
}
