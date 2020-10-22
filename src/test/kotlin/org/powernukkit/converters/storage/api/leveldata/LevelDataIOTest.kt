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
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.storage.api.Dialect
import org.powernukkit.converters.storage.api.StorageEngine
import org.powernukkit.version.Version
import java.io.File
import java.time.Instant
import kotlin.test.assertEquals

/**
 * @author joserobjr
 * @since 2020-10-20
 */
internal class LevelDataIOTest {
    @Test
    fun testJavaEdition() {
        val folder = "sample-worlds/Fresh default worlds/Java Edition/1.16.3"
        val levelFile = File("$folder/level.dat")
        val java = LevelDataIO.readLevelDataBlocking(levelFile)
        assertEquals(MinecraftEdition.JAVA, java.versionData?.minecraftEdition)
        assertEquals(Dialect.VANILLA_JAVA_EDITION, java.dialect)
    }

    @Test
    fun testWindows10Edition() {
        val folder = "sample-worlds/Fresh default worlds/Windows 10 Edition/1.16.40.2.0"
        val levelFile = File("$folder/level.dat")
        val win10 = LevelDataIO.readLevelDataBlocking(levelFile)
        assertEquals(StorageEngine.LEVELDB, win10.storageEngine)
        assertEquals(MinecraftEdition.BEDROCK, win10.versionData?.minecraftEdition)
        assertEquals(Dialect.VANILLA_BEDROCK_EDITION, win10.dialect)
        assert(win10.folder?.endsWith(folder) == true)
        assertEquals(false, win10.dataFile?.isCompressed)
        assertEquals(true, win10.dataFile?.isLittleEndian)
        assertEquals(8, win10.dataFile?.version)
        assertEquals(8, win10.versionData?.nbtVersionHeader)
        assertEquals(Version("1.16.40.2.0"), win10.versionData?.lastOpenedWithVersion)
        assertEquals(Version("1.16.0.0.0"), win10.versionData?.minimumCompatibleClientVersion)
        assertEquals(
            VanillaGameRule.values()
                .filter { it.inBedrock }
                .associate { it.name to it.defaultBedrockStringValue },
            win10.gameRules
        )
        assertEquals(false, win10.bonusChest)
        assertEquals(false, win10.bonusChestSpawned)
        assertEquals(true, win10.centerMapsToOrigin)
        assertEquals(true, win10.allowCommands)
        assertEquals(false, win10.confirmedPlatformLockedContent)
        assertEquals(false, win10.educationFeaturesEnabled)
        assertEquals(false, win10.forceGameType)
        assertEquals(true, win10.hasBeenLoadedInCreative)
        assertEquals(false, win10.hasLockedBehaviorPack)
        assertEquals(false, win10.hasLockedResourcePack)
        assertEquals(false, win10.immutableWorld)
        assertEquals(false, win10.isFromLockedTemplate)
        assertEquals(false, win10.isFromWorldTemplate)
        assertEquals(false, win10.isSingleUseWorld)
        assertEquals(false, win10.isWorldTemplateOptionLocked)
        assertEquals(true, win10.lanBroadcast)
        assertEquals(true, win10.lanBroadcastIntent)
        assertEquals(true, win10.multiplayerGame)
        assertEquals(true, win10.multiplayerGameIntent)
        assertEquals(false, win10.requiresCopiedPackRemovalCheck)
        assertEquals(true, win10.spawnMobs)
        assertEquals(false, win10.spawnV1Villagers)
        assertEquals(false, win10.startWithMap)
        assertEquals(false, win10.texturePacksRequired)
        assertEquals(false, win10.useMsaGamerTagsOnly)
        assertEquals(0, win10.difficulty)
        assertEquals(0, win10.eduOffer)
        assertEquals(1, win10.gameType)
        assertEquals(1, win10.generator)
        assertEquals(3942, win10.thunderTime)
        assertEquals(16, win10.limitedWorldDepth)
        assertEquals(16, win10.limitedWorldWidth)
        assertEquals(BlockPos(216, 32767, 4), win10.limitedWorldOrigin)
        assertEquals(8, win10.netherScale)
        assertEquals(407, win10.versionData?.networkVersion)
        assertEquals(2, win10.versionData?.platform)
        assertEquals(3, win10.platformBroadcastIntent)
        assertEquals(9608, win10.rainTime)
        assertEquals(10, win10.serverChunkTickRange)
        assertEquals(BlockPos(216, 0x7FFF, 4), win10.spawn)
        assertEquals(8, win10.versionData?.storageVersion)
        assertEquals(3, win10.xBoxLiveBroadcastIntent)
        assertEquals(286204L, win10.currentTick)
        assertEquals(Instant.ofEpochSecond(1601063011), win10.lastPlayed)
        assertEquals(3410562280L, win10.randomSeed)
        assertEquals(293581L, win10.time)
        assertEquals(4294967260L, win10.worldStartCount)
        assertEquals(0F, win10.lightningLevel)
        assertEquals(0F, win10.rainLevel)
        assertEquals(Version("*"), win10.versionData?.baseGameVersion)
        assertEquals("", win10.biomeOverride)
        assertEquals("null\n", win10.flatWorldLayers)
        assertEquals(Version("1.16.10"), win10.versionData?.inventoryVersion)
        assertEquals("Meu mundo", win10.levelName)
        assertEquals("", win10.prid)
    }

    @Test
    fun testPowerNukkit() {
        val folder = "sample-worlds/Fresh default worlds/PowerNukkit/1.3.1.5-PN"
        val levelFile = File("$folder/level.dat")
        val powerNukkit = LevelDataIO.readLevelDataBlocking(levelFile)

        assert(powerNukkit.folder?.endsWith(folder) == true)
        assertEquals(MinecraftEdition.BEDROCK, powerNukkit.versionData?.minecraftEdition)
        assertEquals(StorageEngine.ANVIL, powerNukkit.storageEngine)
        assertEquals(Dialect.POWER_NUKKIT, powerNukkit.dialect)
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
            powerNukkit.gameRules
        )
        assertEquals(false, powerNukkit.hardcore)
        assertEquals(true, powerNukkit.initialized)
        assertEquals(false, powerNukkit.raining)
        assertEquals(false, powerNukkit.thundering)
        assertEquals(0, powerNukkit.gameType)
        assertEquals(1, powerNukkit.generatorVersion)
        assertEquals(239815, powerNukkit.rainTime)
        assertEquals(BlockPos(128, 70, 128), powerNukkit.spawn)
        assertEquals(239815, powerNukkit.thunderTime)
        assertEquals(19133, powerNukkit.versionData?.nbtVersionTag)
        assertEquals(80352L, powerNukkit.dayTime)
        assertEquals(Instant.ofEpochSecond(1599862221), powerNukkit.lastPlayed)
        assertEquals(1599862221905L, powerNukkit.randomSeed)
        assertEquals(0L, powerNukkit.sizeOnDisk)
        assertEquals(1585787L, powerNukkit.time)
        assertEquals("normal", powerNukkit.generatorName)
        assertEquals("", powerNukkit.generatorOptions)
        assertEquals("world", powerNukkit.levelName)
    }

    @Test
    fun testPocketMine() {
        val folder = "sample-worlds/Fresh default worlds/PocketMine-MP/1.16.20-3.15.2"
        val levelFile = File("$folder/level.dat")
        val pocketMine = LevelDataIO.readLevelDataBlocking(levelFile)
        assertEquals(StorageEngine.POCKET_MINE, pocketMine.storageEngine)
        assertEquals(MinecraftEdition.BEDROCK, pocketMine.versionData?.minecraftEdition)
        assertEquals(Dialect.POCKET_MINE, pocketMine.dialect)
        assert(pocketMine.folder?.endsWith(folder) == true)
        assertEquals(true, pocketMine.dataFile?.isCompressed)
        assertEquals(false, pocketMine.dataFile?.isLittleEndian)
        assertEquals(null, pocketMine.dataFile?.version)
        assertEquals(emptyMap(), pocketMine.gameRules)
        assertEquals(2, pocketMine.difficulty)
        assertEquals(false, pocketMine.hardcore)
        assertEquals(true, pocketMine.initialized)
        assertEquals(0, pocketMine.dayTime)
        assertEquals(0, pocketMine.gameType)
        assertEquals(1, pocketMine.generatorVersion)
        assertEquals(BlockPos(256, 70, 256), pocketMine.spawn)
        assertEquals(-1, pocketMine.versionData?.nbtVersionTag)
        assertEquals(Instant.ofEpochMilli(1603287284042), pocketMine.lastPlayed)
        assertEquals(1138960654L, pocketMine.randomSeed)
        assertEquals(3524, pocketMine.time)
        assertEquals("normal", pocketMine.generatorName)
        assertEquals("", pocketMine.generatorOptions)
        assertEquals("world", pocketMine.levelName)
    }
}
