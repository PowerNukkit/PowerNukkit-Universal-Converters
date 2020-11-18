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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.storage.api.Chunk
import org.powernukkit.converters.storage.api.ProviderWorld
import org.powernukkit.converters.storage.api.StorageException
import org.powernukkit.converters.storage.api.StorageProblemManager
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import org.powernukkit.converters.storage.leveldb.facade.LevelDB
import org.powernukkit.converters.storage.leveldb.facade.LevelDBFactory
import org.powernukkit.converters.storage.leveldb.facade.LevelDBSnapshot
import java.nio.file.Path
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * @author joserobjr
 * @since 2020-11-16
 */
class LevelDBProviderWorld<P : Platform<P>> private constructor(
    override val platform: P,
    private val worldDir: Path,
    override val levelData: LevelData,
    problemManager: StorageProblemManager,
    override val storageEngine: LevelDBStorageEngine,
    levelDBFactory: LevelDBFactory = LevelDB.defaultFactory
) : ProviderWorld<P>(problemManager) {

    private val db = levelDBFactory.open(worldDir.resolve("db").toFile())

    private fun readChunk(snapshot: LevelDBSnapshot, pos: ChunkPos) = try {
        LevelDBChunk(this, pos, problemManager)
    } catch (e: Exception) {
        throw StorageException(cause = e)
    }

    @ExperimentalCoroutinesApi
    private fun chunkPosFlow(snapshot: LevelDBSnapshot): Flow<ChunkPos> =
        snapshot.parsedKeyIterator {
            asSequence()
                .mapNotNull { it as? ChunkKey }
                .filter { it.bufferSize == 9 && it.type == ChunkKeyType.VERSION }
                .map { it.pos }
                .asFlow().flowOn(Dispatchers.IO)
        }

    @ExperimentalCoroutinesApi
    override fun chunkFlow(): Flow<Chunk<P>> = flow {
        db.createSnapshot().use { snapshot ->
            emitAll(chunkFlow(snapshot))
        }
    }.flowOn(Dispatchers.IO)

    @ExperimentalCoroutinesApi
    private fun chunkFlow(snapshot: LevelDBSnapshot): Flow<Chunk<P>> =
        chunkPosFlow(snapshot)
            .map { readChunk(snapshot, it) }
            .flowOn(Dispatchers.IO)

    @ExperimentalContracts
    @ExperimentalCoroutinesApi
    override fun countChunks(): Flow<Int> = snapshotFlow { snapshot ->
        var count = 0
        chunkPosFlow(snapshot).collect {
            if (++count >= 20) {
                send(count)
                count = 0
            }
        }
        send(count)
    }

    @ExperimentalCoroutinesApi
    @OptIn(ExperimentalContracts::class)
    override fun countEntities(): Flow<Int> = snapshotFlow { snapshot ->
        chunkPosFlow(snapshot)
            .mapNotNull { snapshot[ChunkKey(it, ChunkKeyType.ENTITY)] }
            .map { ChunkKeyType.ENTITY.loadValue(it).size }
            .filter { it > 0 }
            .collect { send(it) }
    }

    @ExperimentalCoroutinesApi
    @OptIn(ExperimentalContracts::class)
    override fun countBlockEntities(): Flow<Int> = snapshotFlow { snapshot ->
        chunkPosFlow(snapshot)
            .mapNotNull { snapshot[ChunkKey(it, ChunkKeyType.BLOCK_ENTITY)] }
            .map { ChunkKeyType.BLOCK_ENTITY.loadValue(it).size }
            .filter { it > 0 }
            .collect { send(it) }
    }

    @ExperimentalContracts
    @ExperimentalCoroutinesApi
    private inline fun <T> snapshotFlow(
        crossinline action: suspend SendChannel<T>.(snapshot: LevelDBSnapshot) -> Unit
    ): Flow<T> {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
        return callbackFlow {
            withSnapshot { snapshot ->
                action(snapshot)
            }
        }.flowOn(Dispatchers.IO)
    }

    @ExperimentalContracts
    private inline fun <R> withSnapshot(action: (LevelDBSnapshot) -> R): R {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
        db.createSnapshot().use { snapshot ->
            return action(snapshot)
        }
    }

    override fun close() {
        db.close()
    }
}
