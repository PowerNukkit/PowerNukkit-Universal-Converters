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

import org.junit.jupiter.api.Test
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.math.EntityPos
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.storage.api.Dialect
import org.powernukkit.converters.storage.api.StorageEngine
import org.powernukkit.version.Version
import java.io.File
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * @author joserobjr
 * @since 2020-10-20
 */
internal class LevelDataIOTest {
    @Test
    fun testJavaEdition() {
        val sampleFolder = "sample-worlds/Fresh default worlds/Java Edition/1.16.3"
        val levelFile = File("$sampleFolder/level.dat")
        val java = LevelDataIO.readLevelDataBlocking(levelFile)
        with(java) {
            assertEquals(MinecraftEdition.JAVA, versionData?.minecraftEdition)
            assertEquals(Dialect.VANILLA_JAVA_EDITION, dialect)
            assertEquals(StorageEngine.ANVIL, storageEngine)
            assertEquals(emptyList<Any>(), scheduledEvents)
            assertEquals(listOf("vanilla"), serverBrands)
            assertEquals(false, allowCommands)
            assertEquals(2, difficulty)
            assertEquals(false, difficultyLocked)
            assertEquals(false, hardcore)
            assertEquals(true, initialized)
            assertEquals(false, thundering)
            assertEquals(false, wasModded)
            assertEquals(0, clearWeatherTime)
            assertEquals(2580, versionData?.worldVersion)
            assertEquals(0, gameType)
            assertEquals(23043, rainTime)
            assertEquals(BlockPos(-240, 67, 112), spawn)
            assertEquals(112259, thunderTime)
            assertEquals(19133, versionData?.nbtVersionTag)
            assertEquals(25, wanderingTraderSpawnChance)
            assertEquals(24000, wanderingTraderSpawnDelay)
            assertEquals(0, borderSizeLerpTime)
            assertEquals(96, dayTime)
            assertEquals(Instant.ofEpochMilli(1603150609696L), lastPlayed)
            assertEquals(96, time)
            assertEquals(0.0, spawnAngle)
            assertEquals(EntityPos(0.0, 0.0, 0.0), borderCenter)
            assertEquals(0.2, borderDamageperBlock)
            assertEquals(5.0, borderSafeZone)
            assertEquals(60_000_000.0, borderSize)
            assertEquals(600_00_000.0, borderSizeLerpTarget)
            assertEquals(5.0, borderWarningBlocks)
            assertEquals(15.0, borderWarningTime)
            assertEquals("Novo mundo", levelName)

            assertEquals(true, endDimensionData?.dragonKilled)
            assertEquals(true, endDimensionData?.dragonPreviouslyKilled)
            assertEquals(
                listOf(13, 15, 0, 17, 12, 11, 7, 19, 2, 4, 1, 14, 16, 6, 10, 9, 18, 8, 3, 5),
                endDimensionData?.gateways
            )

            assertEquals(
                VanillaGameRule.values()
                    .filter { it.inJava }
                    .associate { it.name to it.defaultJavaStringValue },
                gameRules
            )

            assertEquals(emptyList(), disabledDataPacks)
            assertEquals(listOf("vanilla"), enabledDataPacks)

            assertEquals(Version("1.16.3"), versionData?.lastOpenedWithVersion)
            assertEquals(false, versionData?.isSnapshot)
            assertEquals(2580, versionData?.minecraftVersionId)

            assertEquals(7717606393361711299L, randomSeed)
            assertEquals(true, mapFeatures)
            assertEquals(false, bonusChest)

            val overworldId = NamespacedId("minecraft:overworld")
            val netherId = NamespacedId("minecraft:the_nether")
            val endId = NamespacedId("minecraft:the_end")

            val dims = assertNotNull(dimensionGeneratorSettings)
            assertEquals(setOf(overworldId, netherId, endId), dims.keys)

            with(assertNotNull(dims[overworldId])) {
                assertEquals(overworldId, dimensionType)

                assertEquals(NamespacedId("minecraft:noise"), generatorType)
                assertEquals(overworldId.toString(), settingsPreset)
                assertEquals(7717606393361711299L, generatorSeed)

                assertEquals(NamespacedId("minecraft:vanilla_layered"), biomeSource?.biomeSourceType)
                assertEquals(7717606393361711299L, biomeSource?.biomeSeed)
                assertEquals(false, biomeSource?.largeBiomes)
            }

            with(assertNotNull(dims[endId])) {
                assertEquals(endId, dimensionType)

                assertEquals(NamespacedId("minecraft:noise"), generatorType)
                assertEquals("minecraft:end", settingsPreset)
                assertEquals(7717606393361711299L, generatorSeed)

                assertEquals(endId, biomeSource?.biomeSourceType)
                assertEquals(7717606393361711299L, biomeSource?.biomeSeed)
            }

            with(assertNotNull(dims[netherId])) {
                assertEquals(netherId, dimensionType)

                assertEquals(NamespacedId("minecraft:noise"), generatorType)
                assertEquals("minecraft:nether", settingsPreset)
                assertEquals(7717606393361711299L, generatorSeed)

                assertEquals(NamespacedId("minecraft:multi_noise"), biomeSource?.biomeSourceType)
                assertEquals(NamespacedId("minecraft:nether"), biomeSource?.biomePreset)
                assertEquals(7717606393361711299L, biomeSource?.biomeSeed)
            }
        }
    }

    @Test
    fun testWindows10Edition() {
        val sampleFolder = "sample-worlds/Fresh default worlds/Windows 10 Edition/1.16.40.2.0"
        val levelFile = File("$sampleFolder/level.dat")
        val win10 = LevelDataIO.readLevelDataBlocking(levelFile)
        with(win10) {
            assertEquals(StorageEngine.LEVELDB, storageEngine)
            assertEquals(MinecraftEdition.BEDROCK, versionData?.minecraftEdition)
            assertEquals(Dialect.VANILLA_BEDROCK_EDITION, dialect)
            assert(folder?.endsWith(sampleFolder) == true)
            assertEquals(false, dataFile?.isCompressed)
            assertEquals(true, dataFile?.isLittleEndian)
            assertEquals(8, dataFile?.version)
            assertEquals(8, versionData?.nbtVersionHeader)
            assertEquals(Version("1.16.40.2.0"), versionData?.lastOpenedWithVersion)
            assertEquals(Version("1.16.0.0.0"), versionData?.minimumCompatibleClientVersion)
            assertEquals(
                VanillaGameRule.values()
                    .filter { it.inBedrock }
                    .associate { it.name to it.defaultBedrockStringValue },
                win10.gameRules
            )
            assertEquals(false, bonusChest)
            assertEquals(false, bonusChestSpawned)
            assertEquals(true, centerMapsToOrigin)
            assertEquals(true, allowCommands)
            assertEquals(false, confirmedPlatformLockedContent)
            assertEquals(false, educationFeaturesEnabled)
            assertEquals(false, forceGameType)
            assertEquals(true, hasBeenLoadedInCreative)
            assertEquals(false, hasLockedBehaviorPack)
            assertEquals(false, hasLockedResourcePack)
            assertEquals(false, immutableWorld)
            assertEquals(false, isFromLockedTemplate)
            assertEquals(false, isFromWorldTemplate)
            assertEquals(false, isSingleUseWorld)
            assertEquals(false, isWorldTemplateOptionLocked)
            assertEquals(true, lanBroadcast)
            assertEquals(true, lanBroadcastIntent)
            assertEquals(true, multiplayerGame)
            assertEquals(true, multiplayerGameIntent)
            assertEquals(false, requiresCopiedPackRemovalCheck)
            assertEquals(true, spawnMobs)
            assertEquals(false, spawnV1Villagers)
            assertEquals(false, startWithMap)
            assertEquals(false, texturePacksRequired)
            assertEquals(false, useMsaGamerTagsOnly)
            assertEquals(0, difficulty)
            assertEquals(0, eduOffer)
            assertEquals(1, gameType)
            assertEquals(1, generator)
            assertEquals(3942, thunderTime)
            assertEquals(16, limitedWorldDepth)
            assertEquals(16, limitedWorldWidth)
            assertEquals(BlockPos(216, 32767, 4), limitedWorldOrigin)
            assertEquals(8, netherScale)
            assertEquals(407, versionData?.networkVersion)
            assertEquals(2, versionData?.platform)
            assertEquals(3, platformBroadcastIntent)
            assertEquals(9608, rainTime)
            assertEquals(10, serverChunkTickRange)
            assertEquals(BlockPos(216, 0x7FFF, 4), spawn)
            assertEquals(8, versionData?.storageVersion)
            assertEquals(3, xBoxLiveBroadcastIntent)
            assertEquals(286204L, currentTick)
            assertEquals(Instant.ofEpochSecond(1601063011), lastPlayed)
            assertEquals(3410562280L, randomSeed)
            assertEquals(293581L, time)
            assertEquals(4294967260L, worldStartCount)
            assertEquals(0F, lightningLevel)
            assertEquals(0F, rainLevel)
            assertEquals(Version("*"), versionData?.baseGameVersion)
            assertEquals("", biomeOverride)
            assertEquals("null\n", flatWorldLayers)
            assertEquals(Version("1.16.10"), versionData?.inventoryVersion)
            assertEquals("Meu mundo", levelName)
            assertEquals("", prid)
        }
    }

    @Test
    fun testPowerNukkit() {
        val sampleFolder = "sample-worlds/Fresh default worlds/PowerNukkit/1.3.1.5-PN"
        val levelFile = File("$sampleFolder/level.dat")
        val powerNukkit = LevelDataIO.readLevelDataBlocking(levelFile)

        with(powerNukkit) {
            assert(folder?.endsWith(sampleFolder) == true)
            assertEquals(MinecraftEdition.BEDROCK, versionData?.minecraftEdition)
            assertEquals(StorageEngine.ANVIL, storageEngine)
            //assertEquals(Dialect.POWER_NUKKIT, dialect)
            assertNull(dialect) // PowerNukkit and Nukkit is writting level.dat perfectly, we need to diver it later
            assertEquals(
                mapOf(
                    "commandBlockOutput" to "true",
                    "commandBlocksEnabled" to "true",
                    "doDaylightCycle" to "false",
                    "doEntityDrops" to "true",
                    "doFireTick" to "true",
                    "doImmediateRespawn" to "false",
                    "doInsomnia" to "true",
                    "doMobLoot" to "true",
                    "doMobSpawning" to "true",
                    "doTileDrops" to "true",
                    "doWeatherCycle" to "false",
                    "drowningDamage" to "true",
                    "experimentalGameplay" to "false",
                    "fallDamage" to "true",
                    "fireDamage" to "true",
                    "functionCommandLimit" to "20000",
                    "keepInventory" to "false",
                    "maxCommandChainLength" to "131070",
                    "mobGriefing" to "true",
                    "naturalRegeneration" to "true",
                    "pvp" to "true",
                    "randomTickSpeed" to "3",
                    "sendCommandFeedback" to "true",
                    "showCoordinates" to "false",
                    "showDeathMessages" to "true",
                    "showTags" to "true",
                    "spawnRadius" to "5",
                    "tntExplodes" to "true",
                ),
                gameRules
            )
            assertEquals(false, hardcore)
            assertEquals(true, initialized)
            assertEquals(false, raining)
            assertEquals(false, thundering)
            assertEquals(0, gameType)
            assertEquals(1, generatorVersion)
            assertEquals(239815, rainTime)
            assertEquals(BlockPos(128, 70, 128), spawn)
            assertEquals(239815, thunderTime)
            assertEquals(19133, versionData?.nbtVersionTag)
            assertEquals(80352L, dayTime)
            assertEquals(Instant.ofEpochSecond(1599862221), lastPlayed)
            assertEquals(1599862221905L, randomSeed)
            assertEquals(0L, sizeOnDisk)
            assertEquals(1585787L, time)
            assertEquals("normal", generatorName)
            assertEquals("", generatorOptions)
            assertEquals("world", levelName)
        }
    }

    @Test
    fun testPocketMine() {
        val sampleFolder = "sample-worlds/Fresh default worlds/PocketMine-MP/1.16.20-3.15.2"
        val levelFile = File("$sampleFolder/level.dat")
        val pocketMine = LevelDataIO.readLevelDataBlocking(levelFile)
        with(pocketMine) {
            assertEquals(StorageEngine.POCKET_MINE, storageEngine)
            assertEquals(MinecraftEdition.BEDROCK, versionData?.minecraftEdition)
            assertEquals(Dialect.POCKET_MINE, dialect)
            assert(folder?.endsWith(sampleFolder) == true)
            assertEquals(true, dataFile?.isCompressed)
            assertEquals(false, dataFile?.isLittleEndian)
            assertEquals(null, dataFile?.version)
            assertEquals(emptyMap(), gameRules)
            assertEquals(2, difficulty)
            assertEquals(false, hardcore)
            assertEquals(true, initialized)
            assertEquals(0, dayTime)
            assertEquals(0, gameType)
            assertEquals(1, generatorVersion)
            assertEquals(BlockPos(256, 70, 256), spawn)
            assertEquals(-1, versionData?.nbtVersionTag)
            assertEquals(Instant.ofEpochMilli(1603287284042), lastPlayed)
            assertEquals(1138960654L, randomSeed)
            assertEquals(3524, time)
            assertEquals("normal", generatorName)
            assertEquals("", generatorOptions)
            assertEquals("world", levelName)
        }
    }
}
