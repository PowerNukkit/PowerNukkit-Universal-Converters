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

package org.powernukkit.converters.storage.leveldb

import br.com.gamemods.regionmanipulator.ChunkPos
import io.ktor.utils.io.bits.*
import org.powernukkit.converters.math.nibble.NibbleMemory
import org.powernukkit.converters.math.nibble.toNibbleMemory
import org.powernukkit.converters.platform.api.Platform

/**
 * SubChunkPrefix record format used before Minecraft BE 1.2.13.
 *
 * @author joserobjr
 * @since 2021-06-06
 */
@ExperimentalUnsignedTypes
class LevelDBLegacyChunkSection<P : Platform<P>> private constructor(
    override val world: LevelDBProviderWorld<P>,
    override val chunkPos: ChunkPos,
    override val sectionNumber: Int,
    override val version: Byte,
    private val blockIds: Memory,
    private val blockData: NibbleMemory,
    private val skyLight: NibbleMemory,
    private val blockLight: NibbleMemory,
) : LevelDBChunkSection<P> {

    private companion object {
        private const val BLOCK_ID_INDEX = 0
        private const val BLOCK_DATA_INDEX = BLOCK_ID_INDEX + 4096
        private const val SKY_LIGHT_INDEX = BLOCK_DATA_INDEX + 2048
        private const val BLOCK_LIGHT_INDEX = SKY_LIGHT_INDEX + 2048
    }

    private constructor(
        world: LevelDBProviderWorld<P>, chunkPos: ChunkPos, sectionNumber: Int, version: Byte, data: Memory
    ) : this(
        world = world, chunkPos = chunkPos, sectionNumber = sectionNumber, version = version,
        blockIds = data.slice(BLOCK_ID_INDEX, 4096),
        blockData = data.slice(BLOCK_DATA_INDEX, 2048).toNibbleMemory(),
        skyLight = data.slice(SKY_LIGHT_INDEX, 2048).toNibbleMemory(),
        blockLight = data.slice(BLOCK_LIGHT_INDEX, 2048).toNibbleMemory(),
    )

    constructor(world: LevelDBProviderWorld<P>, chunkPos: ChunkPos, sectionNumber: Int, bytes: ByteArray) : this(
        world = world, chunkPos = chunkPos, sectionNumber = sectionNumber,
        version = bytes[0],
        data = Memory.of(bytes.copyOfRange(1, bytes.size))
    )
}
