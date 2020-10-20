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

package org.powernukkit.converters.storage.anvil

import br.com.gamemods.nbtmanipulator.NbtFile
import br.com.gamemods.nbtmanipulator.NbtIO
import br.com.gamemods.nbtmanipulator.NbtString
import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.*
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.math.EntityPos
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.PlatformObject
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import javax.imageio.ImageIO
import kotlin.coroutines.CoroutineContext

/**
 * @author joserobjr
 * @since 2020-10-19
 */
open class AnvilStorage<P : Platform<P>>(
    final override val platform: P,
    val folder: File
) : CoroutineScope, PlatformObject<P> {
    private val log = InlineLogger()
    open val storageName = "Anvil"
    private val job = Job()
    override val coroutineContext: CoroutineContext by lazy {
        job + Dispatchers.IO + CoroutineName("$storageName Storage: $folder")
    }

    private var isLoaded = AtomicBoolean()

    protected val levelDataFile get() = File(folder, "level.dat")
    protected val levelImage get() = File(folder, "icon.png")

    protected lateinit var loadedData: LevelData

    protected fun load(): Job {
        check(isLoaded.compareAndSet(false, true)) {
            "This storage loading has already started or has already completed"
        }
        return launch {
            val levelData = async {
                runInterruptible { NbtIO.readNbtFile(levelDataFile) }
            }
            val image = async {
                runInterruptible {
                    try {
                        levelImage.takeIf { it.isFile }?.let { ImageIO.read(it) }
                    } catch (e: IOException) {
                        log.warn(e) { "Could not load the image $levelImage" }
                        null
                    }
                }
            }

            loadLevelData(levelData.await(), image)
        }
    }

    protected suspend fun loadLevelData(levelDataFile: NbtFile, levelImage: Deferred<BufferedImage?>) {
        val levelData = levelDataFile.compound.getCompound("Data")
        loadedData = LevelData(
            minecraftEdition = platform.minecraftEdition,
            dataFile = levelDataFile,
            name = levelData.getString("LevelName"),
            borderWarningTime = levelData.getDouble("BorderWarningTime"),
            borderWarningBlocks = levelData.getDouble("BorderWarningBlocks"),
            borderSizeLerpTarget = levelData.getDouble("BorderSizeLerpTarget"),
            borderSize = levelData.getDouble("BorderSize"),
            borderSafeZone = levelData.getDouble("BorderSafeZone"),
            borderDamageperBlock = levelData.getDouble("BorderDamagePerBlock"),
            borderCenter = EntityPos(
                levelData.getDouble("BorderCenterX"), 0.0, levelData.getDouble("BorderCenterZ")
            ),
            spawnAngle = levelData.getFloat("SpawnAngle").toDouble(),
            time = levelData.getLong("Time"),
            lastPlayed = levelData.getLong("LastPlayed"),
            dayTime = levelData.getLong("DayTime"),
            borderSizeLerpTime = levelData.getLong("BorderSizeLerpTime"),
            wanderingTraderSpawnDelay = levelData.getInt("WanderingTraderSpawnDelay"),
            wanderingTraderSpawnChance = levelData.getInt("WanderingTraderSpawnChance"),
            version = levelData.getInt("version"),
            thunderTime = levelData.getInt("thunderTime"),
            spawn = BlockPos(
                levelData.getInt("SpawnX"), levelData.getInt("SpawnY"), levelData.getInt("SpawnZ")
            ),
            rainTime = levelData.getInt("rainTime"),
            gameType = levelData.getInt("GameType"),
            dataVersion = levelData.getInt("DataVersion"),
            clearWeatherTime = levelData.getInt("clearWeatherTime"),
            wasModded = levelData.getBooleanByte("WasModded"),
            thundering = levelData.getBooleanByte("thundering"),
            raining = levelData.getBooleanByte("raining"),
            initialized = levelData.getBooleanByte("initialized"),
            hardcore = levelData.getBooleanByte("hardcore"),
            difficultyLocked = levelData.getBooleanByte("DifficultyLocked"),
            difficulty = levelData.getByte("Difficulty").toInt(),
            allowCommands = levelData.getBooleanByte("allowCommands"),
            randomSeed = levelData.getCompound("WorldGenSettings").getLong("seed"),
            mapFeatures = levelData.getCompound("WorldGenSettings").getBooleanByte("generate_features"),
            bonusChest = levelData.getCompound("WorldGenSettings").getBooleanByte("bonus_chest"),
            gameRules = levelData.getCompound("GameRules").mapValues { (_, v) -> v as NbtString; v.value },
            icon = levelImage.await()
        )
    }

    open fun close() {
        job.cancel()
    }
}
