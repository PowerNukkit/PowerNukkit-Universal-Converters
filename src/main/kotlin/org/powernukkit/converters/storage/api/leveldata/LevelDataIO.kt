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

package org.powernukkit.converters.storage.api.leveldata

import br.com.gamemods.nbtmanipulator.*
import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.*
import org.powernukkit.converters.JavaJsonText
import org.powernukkit.converters.internal.*
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.math.EntityPos
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.storage.api.leveldata.model.CustomBossData
import org.powernukkit.converters.storage.api.leveldata.model.EndDimensionData
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import org.powernukkit.converters.storage.api.leveldata.model.LevelVersionData
import org.powernukkit.version.Version
import java.awt.image.BufferedImage
import java.io.*
import java.time.Instant
import java.util.*
import java.util.zip.ZipException
import javax.imageio.ImageIO

/**
 * @author joserobjr
 * @since 2020-10-20
 */
object LevelDataIO {
    private val log = InlineLogger()

    private fun parseCommonLevelDataProperties(levelData: NbtCompound): LevelData {
        return LevelData(
            // Since JE inf-dev
            spawn = takeIf { "SpawnX" in levelData && "SpawnY" in levelData && "SpawnZ" in levelData }?.let {
                BlockPos(levelData["SpawnX"].int, levelData["SpawnY"].int, levelData["SpawnZ"].int)
            },
            randomSeed = levelData["RandomSeed"].longOrNull,
            lastPlayed = levelData["LastPlayed"].longOrNull?.let(Instant::ofEpochSecond),
            time = levelData["Time"].longOrNull,

            // Since JE Beta 1.3
            name = levelData["LevelName"].stringOrNull,

            // Since JE Beta 1.5
            rainTime = levelData["rainTime"].intOrNull,

            // Since JE Beta 1.8
            gameType = levelData["GameType"].intOrNull,

            // Since JE 1.8
            difficulty = levelData["Difficulty"].intOrNull,
        )
    }

    private fun parseUndefinedEditionLevelData(levelData: NbtCompound): LevelData {
        return with(parseCommonLevelDataProperties(levelData)) {
            copy(
                // TODO Detect if it is a PowerNukkit or an old JavaEdition levelData somehow
            )
        }
    }

    private fun parseJavaEditionLevelData(levelData: NbtCompound, versionData: LevelVersionData): LevelData {

        val endDimensionRootCompound = levelData["DragonFight"] ?: levelData["DimensionData"]["1"]

        return with(parseCommonLevelDataProperties(levelData)) {
            copy(
                // Since JE inf-dev
                randomSeed = randomSeed
                    ?: levelData["WorldGenSettings"]["seed"].longOrNull,
                sizeOnDisk = sizeOnDisk
                    ?: levelData["SizeOnDisk"].longOrNull?.takeUnless { it == 0L },

                // Since Alpha
                snowCovered = snowCovered
                    ?: levelData["SnowCovered"]?.booleanOrNull,

                // Since JE Beta 1.5
                raining = raining
                    ?: levelData["raining"].booleanOrNull,
                thundering = thundering
                    ?: levelData["thundering"].booleanOrNull,
                thunderTime = thunderTime
                    ?: levelData["thunderTime"].intOrNull,

                // Since JE Beta 1.8
                // TODO What is the equivalent in BE?
                mapFeatures = mapFeatures
                    ?: levelData["WorldGenSettings"]["generate_features"].booleanOrNull
                    ?: levelData["MapFeatures"].booleanOrNull,

                // Since JE 1.0
                hardcore = hardcore
                    ?: levelData["hardcore"].booleanOrNull,

                // Since JE 1.1
                generatorName = generatorName
                    ?: levelData["generatorName"].stringOrNull,

                // Since JE 1.2
                generatorVersion = generatorVersion
                    ?: levelData["generatorVersion"].intOrNull,

                // Since JE 1.3
                allowCommands = allowCommands
                    ?: levelData["allowCommands"].booleanOrNull,
                initialized = initialized
                    ?: levelData["initialized"].booleanOrNull,

                // Since JE 1.4
                gameRules = gameRules
                    ?: levelData["GameRules"].compoundOrNull?.mapValues { (_, tag) -> tag.string },
                generatorOptions = generatorOptions
                    ?: levelData["generatorOptions"].stringOrNull,

                // Since JE 1.8
                difficultyLocked = difficultyLocked
                    ?: levelData["DifficultyLocked"].booleanOrNull,
                clearWeatherTime = clearWeatherTime
                    ?: levelData["clearWeatherTime"].intOrNull,
                borderSizeLerpTime = borderSizeLerpTime
                    ?: levelData["BorderSizeLerpTime"].longOrNull,
                borderCenter = borderCenter
                    ?: takeIf { "BorderCenterX" in levelData && "BorderCenterZ" in levelData }?.let {
                        EntityPos(levelData["BorderCenterX"].double, 0.0, levelData["BorderCenterZ"].double)
                    },
                borderDamageperBlock = borderDamageperBlock
                    ?: levelData["BorderDamagePerBlock"].doubleOrNull,
                borderSafeZone = borderSafeZone
                    ?: levelData["BorderSafeZone"].doubleOrNull,
                borderSize = borderSize
                    ?: levelData["BorderSize"].doubleOrNull,
                borderSizeLerpTarget = borderSizeLerpTarget
                    ?: levelData["BorderSizeLerpTarget"].doubleOrNull,

                // Since JE 1.9
                versionData = versionData,
                endDimensionData = endDimensionData ?: endDimensionRootCompound?.compound?.let { dragonFight ->
                    EndDimensionData(
                        exitPortalLocation = dragonFight["ExitPortalLocation"].compoundOrNull
                            ?.takeUnless { "X" !in it || "Y" !in it || "Z" !in it }
                            ?.let { BlockPos(it["X"].int, it["Y"].int, it["Z"].int) },
                        gateways = dragonFight["Gateways"].intListOrNull,
                        dragonKilled = dragonFight["DragonKilled"].booleanOrNull,
                        dragonPreviouslyKilled = dragonFight["PreviouslyKilled"].booleanOrNull,
                        dragonUUID = takeIf { "DragonUUIDLeast" in dragonFight && "DragonUUIDMost" in dragonFight }
                            ?.let { UUID(dragonFight["DragonUUIDMost"].long, dragonFight["DragonUUIDLeast"].long) }
                    )
                },

                // Since JE 1.13
                enabledDataPacks = enabledDataPacks
                    ?: levelData["DataPacks"]["Enabled"].stringListOrNull,
                disabledDataPacks = disabledDataPacks
                    ?: levelData["DataPacks"]["Disabled"].stringListOrNull,
                customBosses = customBosses
                    ?: levelData["CustomBossEvents"].compoundOrNull?.map { (id, customBossNbt) ->
                        NamespacedId(id) to customBossNbt.compound.let { customBossData ->
                            CustomBossData(
                                players = customBossData["Players"].compoundListOrNull?.map { playerCompound ->
                                    UUID(playerCompound["M"].long, playerCompound["L"].long)
                                },
                                color = customBossData["Color"].stringOrNull,
                                createWorldFog = customBossData["CreateWorldFog"].booleanOrNull,
                                darkenScreen = customBossData["DarkenScreen"].booleanOrNull,
                                maxHealth = customBossData["Max"].int,
                                currentHealth = customBossData["Value"].int,
                                name = JavaJsonText(customBossData["Name"].string),
                                overlay = customBossData["Overlay"].stringOrNull,
                                playBossMusic = customBossData["PlayBossMusic"].booleanOrNull,
                                visible = customBossData["Visible"].booleanOrNull,
                            )
                        }
                    }?.toMap(),

                // Since JE 1.14
                scheduledEvents = scheduledEvents
                    ?: levelData.getNullableList("ScheduledEvents"),
                wanderingTraderSpawnChance = wanderingTraderSpawnChance
                    ?: levelData["WanderingTraderSpawnChance"].intOrNull,
                wanderingTraderSpawnDelay = wanderingTraderSpawnDelay
                    ?: levelData["WanderingTraderSpawnDelay"].intOrNull,

                // Since JE 1.16
                serverBrands = serverBrands
                    ?: levelData["ServerBrands"].stringListOrNull,
                wasModded = wasModded
                    ?: levelData["WasModded"].booleanOrNull,
                dayTime = dayTime
                    ?: levelData["DayTime"].longOrNull,

                bonusChest = bonusChest
                    ?: levelData["WorldGenSettings"]["bonus_chest"].booleanOrNull,
                //TODO dimensionGeneratorSettings

                // Since JE 1.16.3
                spawnAngle = spawnAngle
                    ?: levelData["SpawnAngle"].floatOrNull?.toDouble(),
            )
        }
    }

    private fun parseBedrockEditionLevelData(levelData: NbtCompound, versionData: LevelVersionData): LevelData {
        return with(parseCommonLevelDataProperties(levelData)) {
            copy(
                thunderTime = thunderTime ?: levelData["lightningTime"].intOrNull,

                allowCommands = levelData["commandsEnabled"].booleanOrNull,

                gameRules = VanillaGameRule.values().asSequence()
                    .filter { it.inBedrock }
                    .filter { it.bedrockName in levelData }
                    .associate {
                        it.name to if (it.isBoolean) {
                            levelData[it.bedrockName].boolean.toString()
                        } else {
                            levelData[it.bedrockName].int.toString()
                        }
                    }
            )
        }
    }

    private fun bedrockVersion(version: NbtList<NbtInt>): Version {
        return Version(buildString {
            version.forEach { part ->
                append(part.value).append('.')
            }
            setLength(length - 1)
        })
    }

    private fun detectMinecraftEditionVersion(levelData: NbtCompound): LevelVersionData? {
        val minVersion = levelData["MinimumCompatibleClientVersion"].nbtIntListOrNull?.takeUnless { it.isEmpty() }
        val lastOpened = levelData["lastOpenedWithVersion"].nbtIntListOrNull?.takeUnless { it.isEmpty() }
        val platform = levelData["Platform"].intOrNull
        val networkVersion = levelData["NetworkVersion"].intOrNull

        val nbtVersion = levelData["version"].intOrNull
        val version = levelData["Version"].compoundOrNull
        val versionName = version["Name"].stringOrNull
        val versionId = version["Id"].intOrNull
        val versionSnapshot = version["Snapshot"].booleanOrNull

        var minecraftEdition: MinecraftEdition? = null
        if (minVersion != null || lastOpened != null || platform != null || networkVersion != null) {
            minecraftEdition = MinecraftEdition.BEDROCK
        } else if (versionId != null || versionName != null) {
            minecraftEdition = MinecraftEdition.JAVA
        }

        val minClientVersion = minVersion?.let { bedrockVersion(it) }

        val lastOpenedVersion = when {
            lastOpened != null -> bedrockVersion(lastOpened)
            versionName != null -> Version(versionName)
            else -> null
        }

        return LevelVersionData(
            minecraftEdition = minecraftEdition,

            minecraftVersionId = versionId,
            isSnapshot = versionSnapshot,
            nbtVersion = nbtVersion,

            lastOpenedWithVersion = lastOpenedVersion,
            minimumCompatibleClientVersion = minClientVersion,
            baseGameVersion = levelData["baseGameVersion"]?.stringOrNull,
            inventoryVersion = levelData["InventoryVersion"]?.stringOrNull,

            platform = platform,
            networkVersion = networkVersion,
        )
    }

    fun parseLevelData(levelData: NbtCompound): LevelData {
        val version = detectMinecraftEditionVersion(levelData)
        return when (version?.minecraftEdition) {
            MinecraftEdition.BEDROCK -> parseBedrockEditionLevelData(levelData, version)
            MinecraftEdition.JAVA -> parseJavaEditionLevelData(levelData, version)
            null -> parseUndefinedEditionLevelData(levelData)
            MinecraftEdition.UNIVERSAL -> throw UnsupportedOperationException("Universal don't persist files")
        }
    }

    fun readLevelDataBlocking(levelDataFile: File) =
        runBlocking(Dispatchers.IO + CoroutineName("Awaiting level.dat: $levelDataFile")) {
            readLevelData(levelDataFile).await()
        }

    private fun tryToRead(file: File): NbtFile {
        try {
            return NbtIO.readNbtFile(file)
        } catch (e: ZipException) {
            if (e.message != "Not in GZIP format") {
                throw e
            } // else: Possible Bedrock Edition, it has an extra header
        }

        // New method to make easier to identify issues in stack trace
        return file.inputStream().buffered().use(this::tryToReadBedrockLevelData)
    }

    private fun tryToReadBedrockLevelData(input: InputStream): NbtFile {
        /* val inflater = Inflater(true)
         var bos = ByteArrayOutputStream()
         input.copyTo(bos)
         inflater.setInput(bos.toByteArray())
         inflater.finished()
         bos = ByteArrayOutputStream()
         val buffer = ByteArray(512)
         while (!inflater.finished()) {
             val written = inflater.inflate(buffer)
             check(written > 0)
             bos.write(buffer, 0, written)
         }
         inflater.end()
         val dataIn = DataInputStream(ByteArrayInputStream(bos.toByteArray()))*/
        val dataIn = DataInputStream(input)
        val fileType = dataIn.readInt()
        val fileLength = dataIn.readInt()
        return NbtIO.readNbtFile(dataIn, compressed = false).also {
            it.tag.compoundOrNull?.set("LevelDataIO-FileType", fileType)
            it.tag.compoundOrNull?.set("LevelDataIO-FileLength", fileLength)
        }
    }

    /**
     * @throws FileNotFoundException If the levelDataFile is not a file.
     * @throws InvalidLevelDataException If an error occurred while attempting to load or parse the file.
     */
    fun CoroutineScope.readLevelData(levelDataFile: File, timeout: Long = 10_000) =
        async(Dispatchers.IO + CoroutineName("Reading level.dat: $levelDataFile")) {
            if (!levelDataFile.isFile) {
                throw FileNotFoundException(levelDataFile.toString())
            }

            val icon = async {
                listOf(
                    async { loadIcon(levelDataFile.resolveSibling("icon.png"), timeout) },
                    async { loadIcon(levelDataFile.resolveSibling("world_icon.png"), timeout) }
                ).firstOrNull { it.await() != null }?.await()
            }

            val nbtData = try {
                val nbtFile = withTimeout(timeout) {
                    runInterruptible {
                        tryToRead(levelDataFile)
                    }
                }
                nbtFile.compound.getCompound("Data")
            } catch (cause: Exception) {
                coroutineContext.cancelChildren()
                throw InvalidLevelDataException(
                    "Could not load the level.dat file $levelDataFile, the file is invalid.",
                    cause
                ).also {
                    log.error(it) {
                        "Failed to load the level.dat file: $levelDataFile"
                    }
                }
            }

            parseLevelData(nbtData)
                .copy(icon = icon.await())
        }

    suspend fun loadIcon(iconFile: File, timeout: Long = 10_000): BufferedImage? {
        return try {
            withContext(Dispatchers.IO + CoroutineName("Loading icon: $iconFile")) {
                withTimeout(timeout) {
                    runInterruptible {
                        iconFile.takeIf { it.isFile }?.let { ImageIO.read(it) }
                    }
                }
            }
        } catch (e: IOException) {
            log.warn(e) { "Could not load the image $iconFile" }
            null
        } catch (e: CancellationException) {
            log.warn(e) { "The image of $iconFile was cancelled" }
            null
        }
    }
}
