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
/*
package org.powernukkit.converters.conversion.job

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.powernukkit.converters.conversion.converter.ConversionProblem
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.platform.universal.definitions.DefinitionLoader
import org.powernukkit.converters.storage.api.ProviderWorld
import org.powernukkit.converters.storage.api.ReceivingWorld
import org.powernukkit.converters.storage.api.StorageEngine
import org.powernukkit.converters.storage.api.leveldata.LevelDataIO
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.io.File
import java.util.zip.ZipFile
import kotlin.coroutines.CoroutineContext

/**
 * @author joserobjr
 * @since 2020-10-23
 */
@ExperimentalCoroutinesApi
class ConversionProcess(
    val fromFile: File,
    val toFile: File,
    val toStorageEngine: StorageEngine,
    val problemsChannel: SendChannel<ConversionProblem>? = null,
    val fromStorageEngine: StorageEngine? = null,
    val universalPlatform: UniversalPlatform? = null,
) : CoroutineScope {
    private val job = Job()
    private var _state = MutableStateFlow(Stage.NOT_STARTED)
    val state = _state.asStateFlow()

    private lateinit var levelData: LevelData

    override val coroutineContext: CoroutineContext = job +
            CoroutineName("Conversion from $fromFile to $toFile")

    fun start(): Job {
        check(_state.compareAndSet(Stage.NOT_STARTED, Stage.STARTING)) {
            "The conversion has already started!"
        }
        return launch {
            val mustDelete: Boolean
            val fromDir = if (
                fromFile.isFile
                && fromFile.name.toLowerCase().let { it.endsWith(".mcworld") || it.endsWith(".zip") }
            ) {
                _state.value = Stage.EXTRACTING
                mustDelete = true
                withContext(Dispatchers.IO) {
                    createTempDir("mcworld").also {
                        deZip(it, fromFile)
                    }
                }
            } else {
                mustDelete = false
                fromFile
            }

            try {
                process(fromDir)
            } catch (e: Throwable) {
                _state.value = Stage.FAILED
                throw e
            } finally {
                if (mustDelete) {
                    fromDir.deleteRecursively()
                }
            }
        }
    }

    private suspend fun process(fromDir: File) = coroutineScope {
        _state.value = Stage.PARSING_LEVEL_DATA
        val levelData = with(LevelDataIO) {
            readLevelData(fromDir.resolve("level.dat"))
        }.await().let {
            _state.value = Stage.DETECTING_EXTRA_INFORMATION
            fillMissingInformation(it)
        }

        val fromStorageEngine = fromStorageEngine
            ?: levelData.storageEngineType?.default
            ?: fail("Could not detect the storage engine of $fromFile")

        _state.value = Stage.PREPARING_WORLDS
        val universalPlatformLoader = async {
            universalPlatform ?: DefinitionLoader().loadBuiltin()
        }
        val loadWorldTask = async {
            fromStorageEngine.loadWorld(fromDir, levelData, universalPlatformLoader)
        }
        val prepareTargetTask = async {
            toStorageEngine.prepareToReceive(toFile, loadWorldTask, universalPlatformLoader)
        }

        val fromWorld = loadWorldTask.await()
        val toWorld = prepareTargetTask.await()

        _state.value = Stage.CONVERTING
        processConversion<Nothing, Nothing>(fromWorld, toWorld)
    }

    private suspend fun <FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>
            processConversion(deferredProvider: ProviderWorld<*>, deferredReceiver: ReceivingWorld<*>) {
        @Suppress("UNCHECKED_CAST")
        val fromWorld = (deferredProvider as ProviderWorld<FromPlatform>)

        @Suppress("UNCHECKED_CAST")
        val toWorld = (deferredReceiver as ReceivingWorld<ToPlatform>)

        val fromPlatform = fromWorld.platform
        val toPlatform = toWorld.platform

        val converter = fromPlatform.convertToUniversal().convertToPlatform(toPlatform)

        val fromChunks = fromWorld.chunkFlow()
    }

    private suspend fun fillMissingInformation(data: LevelData): LevelData {
        return data
    }

    private suspend fun deZip(target: File, file: File) {
        runInterruptible {
            ZipFile(file).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    if (Thread.currentThread().isInterrupted) {
                        throw InterruptedException()
                    }
                    zip.getInputStream(entry).use { input ->
                        target.resolve(entry.name).outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
        }
    }

    private fun fail(message: String, cause: Throwable? = null): Nothing {
        CancellationException(message, cause).let {
            abort(it)
            throw it
        }
    }

    fun abort(cause: Throwable) {
        _state.value = Stage.FAILED
        job.cancel("The conversion was aborted!", cause)
    }

    enum class Stage {
        NOT_STARTED,
        STARTING,
        FAILED,
        EXTRACTING,
        PARSING_LEVEL_DATA,
        DETECTING_EXTRA_INFORMATION,
        PREPARING_WORLDS,
        CONVERTING,
    }
} 
*/
