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
import com.github.michaelbull.logging.InlineLogger
import com.google.common.cache.CacheBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.powernukkit.converters.conversion.job.InputWorld
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.storage.api.ProviderWorld
import org.powernukkit.converters.storage.api.StorageException
import org.powernukkit.converters.storage.api.StorageProblemManager
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import org.powernukkit.converters.storage.leveldb.ChunkKeyType.*
import org.powernukkit.converters.storage.leveldb.facade.LevelDB
import org.powernukkit.converters.storage.leveldb.facade.LevelDBFactory
import org.powernukkit.converters.storage.leveldb.facade.LevelDBReadContainer
import org.powernukkit.converters.storage.leveldb.facade.LevelDBSnapshot
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * @author joserobjr
 * @since 2020-11-16
 */
class LevelDBProviderWorld<P : Platform<P>>(
    override val platform: P,
    private val worldDir: Path,
    override val levelData: LevelData,
    problemManager: StorageProblemManager,
    override val storageEngine: LevelDBStorageEngine,
    levelDBFactory: LevelDBFactory = LevelDB.defaultFactory
) : ProviderWorld<P>(problemManager) {
    private val blockStateCache = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build<Int, PlatformBlockState<P>>()

    private companion object {
        private val log = InlineLogger()
    }

    @Suppress("UNCHECKED_CAST")
    constructor(storageEngine: LevelDBStorageEngine, inputWorld: InputWorld) : this(
        inputWorld.platform as P,
        inputWorld.levelFolder.toPath(),
        inputWorld.levelData,
        inputWorld.problemManager,
        storageEngine
    )

    private val db = levelDBFactory.open(worldDir.resolve("db").toFile())

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun readChunk(container: LevelDBReadContainer, pos: ChunkPos, dimension: Int? = null) = try {
        val scope = ChunkKeyQueryScope(container, pos, dimension)

        val version = VERSION[scope]
        val checksum = CHECKSUM[scope]
        val finalized = FINALIZED_STATE[scope]

        val blockEntities = BLOCK_ENTITY[scope]
        val entities = ENTITY[scope]
        val biomes = DATA_2D[scope]

        val pendingTicks = PENDING_TICKS[scope]
        val randomTicks = RANDOM_TICKS[scope]

        val biomeState = BIOME_STATE[scope]
        val borderBlocks = BORDER_BLOCKS[scope]
        val hardcodedSpawns = HARDCODED_SPAWN_AREAS[scope]

        val sections = Array(16) { section ->
            val bytes = SUB_CHUNK_PREFIX[container, pos, dimension, section]
            if (bytes == null || bytes.isEmpty()) {
                return@Array LevelDBEmptyChunkSection(this, pos, section)
            }

            try {
                when (bytes[0].toInt()) {
                    0, 2, 3, 4, 5, 6, 7 -> LevelDBLegacyChunkSection(this, pos, section, bytes)
                    1, 8 -> LevelDBPaletteChunkSection(this, problemManager, pos, section, bytes)
                    else -> throw UnsupportedOperationException("Unsupported chunk section version ${bytes[0]}")
                }
            } catch (e: Exception) {
                problemManager.handleReadChunkSectionFailure(
                    e,
                    LevelDBFailedChunkSection(this, pos, section, bytes[0], bytes)
                )
            }
        }

        LevelDBChunk(
            this, pos, problemManager,
            version,
            checksum,
            finalized,
            blockEntities,
            entities,
            biomes,
            pendingTicks,
            randomTicks,
            biomeState,
            borderBlocks,
            hardcodedSpawns,
            sections,
        )
    } catch (e: Exception) {
        throw StorageException(cause = e)
    }

    internal fun decodeBlockState(
        chunkSectionVersion: Byte,
        storageHash: Int,
        decoder: () -> PlatformBlockState<P>
    ): PlatformBlockState<P> {
        return blockStateCache[hashStorage(chunkSectionVersion, storageHash), decoder]
    }

    private fun hashStorage(chunkSectionVersion: Byte, storageHash: Int) =
        31 * chunkSectionVersion.toInt() + storageHash

    private fun LevelDBReadContainer.chunkPosFlow(): Flow<ChunkPos> =
        parsedKeyIterator {
            asSequence()
                .mapNotNull { it as? ChunkKey }
                .filter { it.bufferSize == 9 && it.type == VERSION }
                .map { it.pos }
                .asFlow().flowOn(Dispatchers.IO)
        }

    override fun chunkFlow(): Flow<LevelDBChunk<P>> = flow {
        db.createSnapshot().use { snapshot ->
            emitAll(snapshot.chunkFlow())
        }
    }.flowOn(Dispatchers.IO)

    private fun LevelDBReadContainer.chunkFlow(): Flow<LevelDBChunk<P>> =
        chunkPosFlow()
            .map { readChunk(this, it) }
            .flowOn(Dispatchers.IO)

    override fun countChunks(): Flow<Int> = snapshotFlow { snapshot ->
        var count = 0
        snapshot.chunkPosFlow().collect {
            if (++count >= 500) {
                emit(count)
                count = 0
            }
        }
        emit(count)
    }

    override fun countEntities(): Flow<Int> = snapshotFlow { snapshot ->
        snapshot.chunkPosFlow()
            .mapNotNull { snapshot[ChunkKey(it, ENTITY)] }
            .map { ENTITY.loadValue(it).size }
            .filter { it > 0 }
            .collect { emit(it) }
    }

    override fun countBlockEntities(): Flow<Int> = snapshotFlow { snapshot ->
        snapshot.chunkPosFlow()
            .mapNotNull { snapshot[ChunkKey(it, BLOCK_ENTITY)] }
            .map { BLOCK_ENTITY.loadValue(it).size }
            .filter { it > 0 }
            .collect { emit(it) }
    }

    @OptIn(ExperimentalContracts::class)
    private inline fun <T> snapshotFlow(
        crossinline action: suspend FlowCollector<T>.(snapshot: LevelDBSnapshot) -> Unit
    ): Flow<T> {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
        return flow {
            db.createSnapshot().use {
                action(it)
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun close() {
        db.close()
    }
}
