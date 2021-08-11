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

package org.powernukkit.converters.storage.leveldb

import br.com.gamemods.nbtmanipulator.NbtFile
import br.com.gamemods.regionmanipulator.ChunkPos
import kotlinx.coroutines.flow.Flow
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlock
import org.powernukkit.converters.platform.api.block.PositionedStructure
import org.powernukkit.converters.storage.api.Chunk
import org.powernukkit.converters.storage.api.StorageProblemManager

/**
 * @author joserobjr
 * @since 2020-11-17
 */
class LevelDBChunk<P : Platform<P>>(
    private val world: LevelDBProviderWorld<P>,
    override val chunkPos: ChunkPos,
    problemManager: StorageProblemManager,
    val version: Byte?,
    val checksum: ByteArray?,
    val finalized: Int?,
    val blockEntities: List<NbtFile>?,
    val entities: List<NbtFile>?,
    val biomes: ByteArray?,
    val pendingTicks: NbtFile?,
    val randomTicks: NbtFile?,
    val biomeState: ByteArray?,
    val borderBlocks: NbtFile?,
    val hardcodedSpawns: NbtFile?,
    val sections: Array<LevelDBChunkSection<P>>,
) : Chunk<P>(problemManager) {

    override val entityCount: Int
        get() = entities?.size ?: 0
    override val chunkSectionCount: Int
        get() = sections.size
    override val blockEntityCount: Int
        get() = blockEntities?.size ?: 0

    override fun countNonAirBlocks(): Int {
        TODO("Not yet implemented")
    }

    override fun structureFlow(): Flow<PositionedStructure<P>> {
        TODO("Not yet implemented")
    }

    override fun get(blockInWorld: BlockPos): PlatformBlock<P> {
        TODO("Not yet implemented")
    }

    override fun set(blockInWorld: BlockPos, block: PlatformBlock<P>) {
        TODO("Not yet implemented")
    }
}
