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

import br.com.gamemods.regionmanipulator.ChunkPos
import io.ktor.utils.io.bits.*
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.storage.api.StorageProblemManager

/**
 * @author joserobjr
 * @since 2021-06-06
 */
class LevelDBPaletteChunkSection<P : Platform<P>>(
    override val world: LevelDBProviderWorld<P>,
    override val chunkPos: ChunkPos,
    override val sectionNumber: Int,
    override val version: Byte,
    private val layers: LevelDBBlockLayers<P>
) : LevelDBChunkSection<P> {
    private constructor(
        world: LevelDBProviderWorld<P>,
        problemManager: StorageProblemManager,
        chunkPos: ChunkPos,
        sectionNumber: Int,
        version: Byte,
        data: Memory
    ) : this(
        world = world, chunkPos = chunkPos, sectionNumber = sectionNumber, version = version,
        layers = LevelDBBlockLayers(world, problemManager, version, data)
    )

    constructor(
        world: LevelDBProviderWorld<P>,
        problemManager: StorageProblemManager,
        chunkPos: ChunkPos,
        sectionNumber: Int,
        bytes: ByteArray
    ) : this(
        world = world, problemManager = problemManager, chunkPos = chunkPos, sectionNumber = sectionNumber,
        version = bytes[0],
        data = Memory.of(bytes).slice(1, bytes.size - 1)
    )
}
