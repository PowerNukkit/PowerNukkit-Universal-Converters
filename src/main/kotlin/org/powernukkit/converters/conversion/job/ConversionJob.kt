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

package org.powernukkit.converters.conversion.job

import kotlinx.coroutines.*
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.storage.api.StorageProber
import org.powernukkit.converters.storage.api.StorageProblemManager
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import org.powernukkit.converters.storage.api.leveldata.model.LevelVersionData
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * @author joserobjr
 * @since 2020-11-15
 */
@ExperimentalCoroutinesApi
class ConversionJob(
    val levelDatFile: File,
    levelData: LevelData,
    universalPlatform: UniversalPlatform,
    val problemManager: StorageProblemManager,
    parent: Job
) : CoroutineScope {
    private val job = Job(parent)
    override val coroutineContext: CoroutineContext
        get() = job

    private val countingJob = Job(job)

    private val _chunkCount = MutableEstimation(0, 0, countingJob)
    val chunkCount = _chunkCount.estimation

    private val _chunkSectionCount = MutableEstimation(0, 0, countingJob)
    val chunkSectionCount = _chunkSectionCount.estimation

    private val _blockEntityCount = MutableEstimation(0, 0, countingJob)
    val blockEntityCount = _blockEntityCount.estimation

    private val _entityCount = MutableEstimation(0, 0, countingJob)
    val entityCount = _entityCount.estimation

    private val storageProbe = StorageProber(levelDatFile, levelData, job)

    val definitiveLevelData = async {
        levelData.copy(
            storageEngineType = storageProbe.storageEngines.await().let { detection ->
                when {
                    detection.size == 1 -> detection.first()
                    levelData.storageEngineType in detection -> levelData.storageEngineType
                    else -> null
                }
            },
            dialect = storageProbe.dialects.await().let { detection ->
                when {
                    detection.size == 1 -> detection.first()
                    levelData.dialect in detection -> levelData.dialect
                    else -> null
                }
            },
            versionData = with(levelData.versionData ?: LevelVersionData()) {
                copy(
                    minecraftEdition = storageProbe.edition.await().let { detection ->
                        when {
                            detection.size == 1 -> detection.first()
                            minecraftEdition in detection -> minecraftEdition
                            else -> null
                        }
                    }
                )
            }
        )
    }

    var inputWorld: InputWorld? = null
        private set(settings) {
            stopCounting()
            field = settings
            startCounting()
        }

    init {
        launch {
            val definitiveData = definitiveLevelData.await()
            val storageEngine = definitiveData.storageEngineType ?: return@launch
            val dialect = definitiveData.dialect ?: return@launch
            val minecraftEdition = definitiveData.versionData?.minecraftEdition ?: return@launch

            inputWorld = InputWorld(
                levelDatFile.parentFile,
                levelData, storageEngine, dialect, minecraftEdition,
                universalPlatform, problemManager,
                job
            )
        }
    }

    private fun startCounting() {
        stopCounting()
        val provider = inputWorld?.providerWorld ?: return
        _chunkCount.startCounting(provider.countChunks())
        _chunkSectionCount.startCounting(provider.countChunkSections())
        _blockEntityCount.startCounting(provider.countBlockEntities())
        _entityCount.startCounting(provider.countEntities())
    }

    private fun stopCounting() {
        _chunkCount.stopCounting()
        _chunkSectionCount.stopCounting()
        _blockEntityCount.stopCounting()
        _entityCount.stopCounting()
    }

    fun cancel() {
        job.cancel()
    }
}
