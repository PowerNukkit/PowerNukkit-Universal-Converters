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

import br.com.gamemods.nbtmanipulator.NbtIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import org.powernukkit.converters.conversion.job.InputWorld
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.storage.api.Chunk
import org.powernukkit.converters.storage.api.ProviderWorld
import org.powernukkit.converters.storage.api.StorageException
import org.powernukkit.converters.storage.api.StorageProblemManager
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

/**
 * @author joserobjr
 * @since 2020-11-15
 */
class AlphaProviderWorld<P : Platform<P>> private constructor(
    override val platform: P,
    private val worldDir: Path,
    override val levelData: LevelData,
    problemManager: StorageProblemManager,
    override val storageEngine: AlphaStorageEngine
) : ProviderWorld<P>(problemManager) {

    @Suppress("UNCHECKED_CAST")
    constructor(storageEngine: AlphaStorageEngine, inputWorld: InputWorld) : this(
        inputWorld.platform as P,
        inputWorld.levelFolder.toPath(),
        inputWorld.levelData,
        inputWorld.problemManager,
        storageEngine
    )

    private fun readChunk(pos: AlphaChunkPos) = try {
        val nbt = NbtIO.readNbtFile(worldDir.resolve(pos.path).toFile())
        AlphaChunk(this, pos, nbt.compound, problemManager)
    } catch (e: Exception) {
        throw StorageException(cause = e)
    }

    private fun chunkFileStream(): Stream<Path> {
        return with(AlphaStorageEngine) {
            Files.list(worldDir)
                .filterValidFolder()
                .flatMap { xDir -> Files.list(xDir) }
                .filterValidFolder()
                .flatMap { zDir -> Files.list(zDir) }
                .filterValidPath(worldDir)
        }
    }

    private fun chunkPosStream(): Stream<AlphaChunkPos> {
        return chunkFileStream().map { AlphaStorageEngine.parseChunkPos(it.fileName.toString()) }
    }

    @ExperimentalCoroutinesApi
    override fun countChunks(): Flow<Int> {
        return callbackFlow {
            suspendCancellableCoroutine { continuation ->
                with(AlphaStorageEngine) {
                    Files.list(worldDir)
                        .filterValidFolder()
                        .flatMap { xDir -> Files.list(xDir) }
                        .filterValidFolder()
                        .use { zDirStream ->
                            continuation.invokeOnCancellation { zDirStream.close() }
                            zDirStream.forEach { zDir ->
                                val chunks = Files.list(zDir).use { chunkFileStream ->
                                    chunkFileStream.filter { chunkFile ->
                                        Files.isRegularFile(chunkFile) && isChunkFilePathValid(worldDir, chunkFile)
                                    }.count().toInt()
                                }

                                sendBlocking(chunks)
                            }
                        }
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    @ExperimentalCoroutinesApi
    override fun chunkFlow(): Flow<Chunk<P>> {
        return callbackFlow<Chunk<P>> {
            suspendCancellableCoroutine { continuation ->
                chunkPosStream().use { stream ->
                    continuation.invokeOnCancellation { stream.close() }
                    stream.forEach { pos ->
                        try {
                            sendBlocking(readChunk(pos))
                        } catch (e: StorageException) {
                            problemManager.handleReadChunkIssue(this@AlphaProviderWorld, e)
                        }
                    }
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}
