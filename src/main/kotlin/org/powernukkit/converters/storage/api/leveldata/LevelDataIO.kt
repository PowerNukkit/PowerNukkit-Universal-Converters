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
import org.powernukkit.converters.storage.api.Dialect
import org.powernukkit.converters.storage.api.StorageEngine
import org.powernukkit.converters.storage.api.leveldata.model.*
import org.powernukkit.version.Version
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import javax.imageio.ImageIO

/**
 * @author joserobjr
 * @since 2020-10-20
 */
object LevelDataIO {
    private val log = InlineLogger()
    private val yearFifthThousand get() = OffsetDateTime.of(50_000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant()

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
            levelName = levelData["LevelName"].stringOrNull,

            // Since JE Beta 1.5
            rainTime = levelData["rainTime"].intOrNull,

            // Since JE Beta 1.8
            gameType = levelData["GameType"].intOrNull,

            // Since JE 1.8
            difficulty = levelData["Difficulty"].intOrNull,
        )
    }

    private fun findUniqueGameRules(gameRules: Map<String, String>): Set<VanillaGameRule>? {
        return gameRules.keys.asSequence()
            .mapNotNull(VanillaGameRule::withName)
            .filter { it.inBedrock != it.inJava }
            .toSet()
            .takeUnless { it.isEmpty() }
    }

    private fun parseUndefinedEditionLevelData(
        levelData: NbtCompound, versionData: LevelVersionData?, nbtFile: NbtFile?
    ): LevelData {
        val javaParse = parseJavaEditionLevelData(levelData, versionData).copy(dataFile = nbtFile)

        // Attempting to detect PocketMine's level.dat
        // It saves LastPlayed with milliseconds and uses different NBT types
        // for some data
        with(javaParse) {
            if (versionData?.nbtVersionTag == -1 && dayTime == null && levelData["DayTime"].intOrNull != null) {
                return parsePocketMineLevelData(levelData, javaParse)
            }
        }

        var completedData = javaParse
        fun detectByGameRule(setStorage: StorageEngine): Boolean {
            val uniqueGameRules = completedData.gameRules?.let(this::findUniqueGameRules)

            val detectedEdition = when {
                uniqueGameRules?.all { it.inJava } == true -> {
                    MinecraftEdition.JAVA
                }
                uniqueGameRules?.all { it.inBedrock } == true -> {
                    MinecraftEdition.BEDROCK
                }
                else -> {
                    return false
                }
            }

            completedData = with(completedData) {
                copy(
                    storageEngine = storageEngine ?: setStorage,
                    versionData = with(versionData ?: LevelVersionData()) {
                        copy(
                            minecraftEdition = minecraftEdition ?: detectedEdition
                        )
                    }
                )
            }

            return true
        }

        val suspectingStorageEngine = detectJavaStorageEngine(completedData)
        if (detectByGameRule(suspectingStorageEngine)) {
            return completedData
        }

        completedData = parseBedrockEditionLevelData(levelData, javaParse.versionData, javaParse, false)
        detectByGameRule(StorageEngine.LEVELDB)
        return completedData
    }

    private fun detectJavaStorageEngine(currentLevelData: LevelData) = with(currentLevelData) {
        if (levelName == null && sizeOnDisk?.let { it > 0 } == true && versionData?.nbtVersionTag == null
            && copy(
                versionData = null, dataFile = null, storageEngine = null, dialect = null, folder = null
            ) == LevelData(
                spawn = spawn, lastPlayed = lastPlayed, randomSeed = randomSeed, sizeOnDisk = sizeOnDisk,
                time = time
            )
        ) {
            StorageEngine.ALPHA
        } else {
            StorageEngine.ANVIL
        }
    }

    private fun parsePocketMineLevelData(levelData: NbtCompound, current: LevelData) = with(current) {
        copy(
            storageEngine = storageEngine
                ?: StorageEngine.POCKET_MINE,

            dialect = dialect
                ?: Dialect.POCKET_MINE,

            versionData = with(versionData ?: LevelVersionData()) {
                copy(
                    minecraftEdition = minecraftEdition
                        ?: MinecraftEdition.BEDROCK,
                )
            },

            dayTime = dayTime
                ?: levelData["DayTime"].intOrNull?.toLong(),
        )
    }

    private fun parseJavaEditionLevelData(
        levelData: NbtCompound, versionData: LevelVersionData?,
        current: LevelData = parseCommonLevelDataProperties(levelData)
    ): LevelData {

        val endDimensionRootCompound = levelData["DragonFight"] ?: levelData["DimensionData"]["1"]

        return with(current) {
            copy(
                // Since JE inf-dev
                lastPlayed = lastPlayed?.takeUnless { it > yearFifthThousand }
                    ?: levelData["LastPlayed"].longOrNull?.let(Instant::ofEpochMilli),
                randomSeed = randomSeed
                    ?: levelData["WorldGenSettings"]["seed"].longOrNull,
                sizeOnDisk = sizeOnDisk
                    ?: levelData["SizeOnDisk"].longOrNull,

                // Since Alpha
                snowCovered = snowCovered
                    ?: levelData["SnowCovered"].booleanOrNull,

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
                dayTime = dayTime
                    ?: levelData["DayTime"].longOrNull,

                // Since JE 1.4
                gameRules = gameRules
                    ?: levelData["GameRules"].compoundOrNull?.mapValues { (_, tag) -> tag.string },
                generatorOptions = generatorOptions
                    ?: levelData["generatorOptions"].stringOrNull,

                // Since JE 1.8
                difficulty = difficulty
                    ?: levelData["Difficulty"].uByteOrNull,
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
                borderWarningBlocks = borderWarningBlocks
                    ?: levelData["BorderWarningBlocks"].doubleOrNull,
                borderWarningTime = borderWarningTime
                    ?: levelData["BorderWarningTime"].doubleOrNull,

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

                bonusChest = bonusChest
                    ?: levelData["WorldGenSettings"]["bonus_chest"].booleanOrNull,
                dimensionGeneratorSettings = dimensionGeneratorSettings
                    ?: levelData["WorldGenSettings"]["dimensions"].compoundOrNull?.let(::parseJavaWorldGenSettings),

                // Since JE 1.16.3
                spawnAngle = spawnAngle
                    ?: levelData["SpawnAngle"].floatOrNull?.toDouble(),
            )
        }
    }

    private fun parseBedrockEditionLevelData(
        levelData: NbtCompound, versionData: LevelVersionData?,
        current: LevelData = parseCommonLevelDataProperties(levelData),
        forSure: Boolean,
    ): LevelData {
        return with(current) {
            copy(
                storageEngine = storageEngine
                    ?: StorageEngine.LEVELDB.takeIf {
                        (versionData?.nbtVersionHeader ?: 0) > 0
                    },

                dialect = dialect
                    ?: Dialect.VANILLA_BEDROCK_EDITION.takeIf { forSure },

                versionData = with((versionData ?: LevelVersionData())) {
                    copy(
                        minecraftEdition = minecraftEdition
                            ?: MinecraftEdition.BEDROCK.takeIf { forSure }
                    )
                },

                thunderTime = thunderTime
                    ?: levelData["lightningTime"].intOrNull,

                allowCommands = allowCommands
                    ?: levelData["commandsEnabled"].booleanOrNull,

                gameRules = gameRules ?: VanillaGameRule.values().asSequence()
                    .filter { it.inBedrock }
                    .filter { it.bedrockName in levelData }
                    .associate {
                        it.name to if (it.isBoolean) {
                            levelData[it.bedrockName].boolean.toString()
                        } else {
                            levelData[it.bedrockName].int.toString()
                        }
                    },

                bonusChest = bonusChest
                    ?: levelData["bonusChestEnabled"].booleanOrNull,
                bonusChestSpawned = bonusChestSpawned
                    ?: levelData["bonusChestSpawned"].booleanOrNull,
                centerMapsToOrigin = centerMapsToOrigin
                    ?: levelData["CenterMapsToOrigin"].booleanOrNull,
                confirmedPlatformLockedContent = confirmedPlatformLockedContent
                    ?: levelData["ConfirmedPlatformLockedContent"].booleanOrNull,
                educationFeaturesEnabled = educationFeaturesEnabled
                    ?: levelData["educationFeaturesEnabled"].booleanOrNull,
                forceGameType = forceGameType
                    ?: levelData["ForceGameType"].booleanOrNull,
                hasBeenLoadedInCreative = hasBeenLoadedInCreative
                    ?: levelData["hasBeenLoadedInCreative"].booleanOrNull,
                hasLockedBehaviorPack = hasLockedBehaviorPack
                    ?: levelData["hasLockedBehaviorPack"].booleanOrNull,
                hasLockedResourcePack = hasLockedResourcePack
                    ?: levelData["hasLockedResourcePack"].booleanOrNull,
                immutableWorld = immutableWorld
                    ?: levelData["immutableWorld"].booleanOrNull,
                isFromLockedTemplate = isFromLockedTemplate
                    ?: levelData["isFromLockedTemplate"].booleanOrNull,
                isFromWorldTemplate = isFromWorldTemplate
                    ?: levelData["isFromWorldTemplate"].booleanOrNull,
                isSingleUseWorld = isSingleUseWorld
                    ?: levelData["isSingleUseWorld"].booleanOrNull,
                isWorldTemplateOptionLocked = isWorldTemplateOptionLocked
                    ?: levelData["isWorldTemplateOptionLocked"].booleanOrNull,
                lanBroadcast = lanBroadcast
                    ?: levelData["LANBroadcast"].booleanOrNull,
                lanBroadcastIntent = lanBroadcastIntent
                    ?: levelData["LANBroadcastIntent"].booleanOrNull,
                multiplayerGame = multiplayerGame
                    ?: levelData["MultiplayerGame"].booleanOrNull,
                multiplayerGameIntent = multiplayerGameIntent
                    ?: levelData["MultiplayerGameIntent"].booleanOrNull,
                requiresCopiedPackRemovalCheck = requiresCopiedPackRemovalCheck
                    ?: levelData["requiresCopiedPackRemovalCheck"].booleanOrNull,
                spawnMobs = spawnMobs
                    ?: levelData["spawnMobs"].booleanOrNull,
                spawnV1Villagers = spawnV1Villagers
                    ?: levelData["SpawnV1Villagers"].booleanOrNull,
                startWithMap = startWithMap
                    ?: levelData["startWithMapEnabled"].booleanOrNull,
                texturePacksRequired = texturePacksRequired
                    ?: levelData["texturePacksRequired"].booleanOrNull,
                useMsaGamerTagsOnly = useMsaGamerTagsOnly
                    ?: levelData["useMsaGamertagsOnly"].booleanOrNull,
                eduOffer = eduOffer
                    ?: levelData["eduOffer"].intOrNull,
                generator = generator
                    ?: levelData["Generator"].intOrNull,
                limitedWorldDepth = limitedWorldDepth
                    ?: levelData["limitedWorldDepth"].intOrNull,
                limitedWorldWidth = limitedWorldWidth
                    ?: levelData["limitedWorldWidth"].intOrNull,
                limitedWorldOrigin = limitedWorldOrigin
                    ?: takeIf { listOf("X", "Y", "Z").all { "LimitedWorldOrigin$it" in levelData } }
                        ?.let {
                            BlockPos(
                                levelData["LimitedWorldOriginX"].int,
                                levelData["LimitedWorldOriginY"].int,
                                levelData["LimitedWorldOriginZ"].int
                            )
                        },
                netherScale = netherScale
                    ?: levelData["NetherScale"].intOrNull,
                platformBroadcastIntent = platformBroadcastIntent
                    ?: levelData["PlatformBroadcastIntent"].intOrNull,
                serverChunkTickRange = serverChunkTickRange
                    ?: levelData["serverChunkTickRange"].intOrNull,
                xBoxLiveBroadcastIntent = xBoxLiveBroadcastIntent
                    ?: levelData["XBLBroadcastIntent"].intOrNull,
                currentTick = currentTick
                    ?: levelData["currentTick"].longOrNull,
                worldStartCount = worldStartCount
                    ?: levelData["worldStartCount"].longOrNull,
                lightningLevel = lightningLevel
                    ?: levelData["lightningLevel"].floatOrNull,
                rainLevel = rainLevel
                    ?: levelData["rainLevel"].floatOrNull,
                biomeOverride = biomeOverride
                    ?: levelData["BiomeOverride"].stringOrNull,
                flatWorldLayers = flatWorldLayers
                    ?: levelData["FlatWorldLayers"].stringOrNull,
                prid = prid
                    ?: levelData["prid"].stringOrNull,
            )
        }
    }

    private fun parseJavaWorldGenSettings(dimensions: NbtCompound): Map<NamespacedId, DimensionGeneratorData> {
        return dimensions.entries.asSequence()
            .filter { (_, genTag) ->
                genTag is NbtCompound && genTag["generator"] is NbtCompound
            }
            .associate { (name, genTag) ->
                NamespacedId(name) to parseJavaGenTag(genTag["generator"].compound, genTag["type"].stringOrNull)
            }
    }

    private fun parseJavaGenTag(genTag: NbtCompound, dimType: String?) = DimensionGeneratorData(
        generatorType = genTag["type"].stringOrNull?.let(::NamespacedId),
        settingsPreset = genTag["settings"].stringOrNull,
        generatorSeed = genTag["seed"].longOrNull,
        customSettings = genTag["settings"].compoundOrNull,
        biomeSource = genTag["biome_source"].compoundOrNull?.let(::parseJavaBiomeSource),
        dimensionType = dimType?.let(::NamespacedId)
    )

    private fun parseJavaBiomeSource(source: NbtCompound) = BiomeGeneratorData(
        biomeSourceType = source["type"].stringOrNull?.let(::NamespacedId),
        biomeSeed = source["seed"].longOrNull,
        largeBiomes = source["large_biomes"].booleanOrNull,
        biomePreset = source["preset"].stringOrNull?.let(::NamespacedId),
        legacyBiomeInitLayer = source["legacy_biome_init_layer "].booleanOrNull,
        biomesList = source["biomes"].stringListOrNull?.map(::NamespacedId),
        biomeParameters = source["biomes"].compoundListOrNull?.map(::parseJavaBiomeMultiNoiseParam),
        altitudeNoise = source["altitude_noise"].compoundOrNull?.let(::parseJavaNoiseParam),
        humidityNoise = source["humidity_noise"].compoundOrNull?.let(::parseJavaNoiseParam),
        temperatureNoise = source["temperature_noise"].compoundOrNull?.let(::parseJavaNoiseParam),
        weirdnessNoise = source["weirdness_noise"].compoundOrNull?.let(::parseJavaNoiseParam),
        scale = source["scale"].intOrNull,
        fixedBiomeId = source["biome"].stringOrNull?.let(::NamespacedId)
    )

    private fun parseJavaBiomeMultiNoiseParam(params: NbtCompound) = BiomeParametersData(
        biome = params["biome"].stringOrNull,
        altitude = params["params"]["altitude"].floatOrNull,
        humidity = params["params"]["humidity"].floatOrNull,
        offset = params["params"]["offset"].floatOrNull,
        temperature = params["params"]["temperature"].floatOrNull,
        weirdness = params["params"]["weirdness"].floatOrNull,
    )

    private fun parseJavaNoiseParam(noise: NbtCompound) = NoiseSettingsData(
        firstOctave = noise["firstOctave"].intOrNull,
        amplitudes = noise["amplitudes"].doubleListOrNull,
    )

    private fun bedrockVersion(version: NbtList<NbtInt>): Version {
        return Version(buildString {
            version.forEach { part ->
                append(part.value).append('.')
            }
            setLength(length - 1)
        })
    }

    private fun detectMinecraftEditionVersion(levelData: NbtCompound, nbtFile: NbtFile?): LevelVersionData? {
        val minVersion = levelData["MinimumCompatibleClientVersion"].nbtIntListOrNull?.takeUnless { it.isEmpty() }
        val lastOpened = levelData["lastOpenedWithVersion"].nbtIntListOrNull?.takeUnless { it.isEmpty() }
        val platform = levelData["Platform"].intOrNull
        val networkVersion = levelData["NetworkVersion"].intOrNull

        val nbtVersion = nbtFile?.version
        val version = levelData["Version"].compoundOrNull
        val versionName = version["Name"].stringOrNull
        val versionId = version["Id"].intOrNull
        val versionSnapshot = version["Snapshot"].booleanOrNull

        var minecraftEdition: MinecraftEdition? = null
        if (minVersion != null || lastOpened != null || platform != null || networkVersion != null) {
            minecraftEdition = MinecraftEdition.BEDROCK
        } else if (versionId != null || versionName != null) {
            minecraftEdition = MinecraftEdition.JAVA
        } else if (nbtFile?.isLittleEndian == true && nbtFile.isCompressed == false && (nbtFile.version ?: 0) > 0) {
            minecraftEdition = MinecraftEdition.BEDROCK
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
            nbtVersionHeader = nbtVersion,
            nbtVersionTag = levelData["version"].intOrNull,
            worldVersion = levelData["DataVersion"].intOrNull,

            lastOpenedWithVersion = lastOpenedVersion,
            minimumCompatibleClientVersion = minClientVersion,
            baseGameVersion = levelData["baseGameVersion"].stringOrNull?.let(::Version),
            inventoryVersion = levelData["InventoryVersion"].stringOrNull?.let(::Version),
            storageVersion = levelData["StorageVersion"].intOrNull,

            platform = platform,
            networkVersion = networkVersion,
        )
    }

    fun parseLevelData(levelData: NbtCompound, nbtFile: NbtFile? = null): LevelData {
        val version = detectMinecraftEditionVersion(levelData, nbtFile)
        return when (version?.minecraftEdition) {
            MinecraftEdition.BEDROCK -> parseBedrockEditionLevelData(levelData, version, forSure = true)
            MinecraftEdition.JAVA -> with(parseJavaEditionLevelData(levelData, version)) {
                copy(
                    dialect = dialect ?: Dialect.VANILLA_JAVA_EDITION,
                    storageEngine = storageEngine ?: detectJavaStorageEngine(this)
                )
            }
            null -> parseUndefinedEditionLevelData(levelData, version, nbtFile)
            MinecraftEdition.UNIVERSAL -> throw UnsupportedOperationException("Universal don't persist files")
        }.copy(dataFile = nbtFile)
    }

    fun parseLevelData(nbtFile: NbtFile): LevelData {
        val nbtData = nbtFile.compound.let { root ->
            root["Data"].compoundOrNull ?: root
        }
        return parseLevelData(nbtData, nbtFile)
    }

    fun readLevelDataBlocking(levelDataFile: File) =
        runBlocking(Dispatchers.IO + CoroutineName("Awaiting level.dat: $levelDataFile")) {
            readLevelData(levelDataFile).await()
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

            val nbtFile = try {
                withTimeout(timeout) {
                    runInterruptible {
                        NbtIO.readNbtFileDetectingSettings(levelDataFile).also {
                            check(it.tag is NbtCompound) {
                                "The root tag of the NBT structure in $levelDataFile is not a compound, " +
                                        "it is a ${it.tag::class.java.simpleName}"
                            }
                        }
                    }
                }
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

            val nbtData = nbtFile.compound.let { root ->
                root["Data"].compoundOrNull ?: root
            }

            if ("Time" !in nbtData && "SpawnX" !in nbtData) {
                coroutineContext.cancelChildren()
                throw InvalidLevelDataException(
                    "The level.dat file is a NBT file but the internal structure don't looks like from a level.dat file. "
                )
            }

            parseLevelData(nbtFile).copy(
                folder = levelDataFile.parentFile.absoluteFile.toPath(),
                icon = icon.await()
            )
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
