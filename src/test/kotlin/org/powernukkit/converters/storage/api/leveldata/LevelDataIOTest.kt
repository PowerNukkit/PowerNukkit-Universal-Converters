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
        val levelFile = File("sample-worlds/Fresh default worlds/Java Edition/1.16.3/level.dat")
        println(LevelDataIO.readLevelDataBlocking(levelFile))
    }

    @Test
    fun testWindows10Edition() {
        val levelFile = File("sample-worlds/Fresh default worlds/Windows 10 Edition/1.16.40.2.0/level.dat")
        val win10 = LevelDataIO.readLevelDataBlocking(levelFile)
        assertEquals(StorageEngine.LEVELDB, win10.storageEngine)
        assertEquals(MinecraftEdition.BEDROCK, win10.versionData?.minecraftEdition)
        assertEquals(false, win10.dataFile?.isCompressed)
        assertEquals(true, win10.dataFile?.isLittleEndian)
        assertEquals(8, win10.dataFile?.version)
        assertEquals(8, win10.versionData?.nbtVersion)
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
        assertEquals(false, win10.experimentalGameplay)
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
        val levelFile = File("sample-worlds/Fresh default worlds/PowerNukkit/1.3.1.5-PN/level.dat")
        println(LevelDataIO.readLevelDataBlocking(levelFile))
    }

    @Test
    fun testPocketMine() {
        val levelFile = File("sample-worlds/Fresh default worlds/PocketMine-MP/1.16.20-3.15.2/level.dat")
        val pocketMine = LevelDataIO.readLevelDataBlocking(levelFile)
        assertEquals(StorageEngine.POCKET_MINE, pocketMine.storageEngine)
        assertEquals(MinecraftEdition.BEDROCK, pocketMine.versionData?.minecraftEdition)
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
        assertEquals(-1, pocketMine.version)
        assertEquals(Instant.ofEpochMilli(1603287284042), pocketMine.lastPlayed)
        assertEquals(1138960654L, pocketMine.randomSeed)
        assertEquals(3524, pocketMine.time)
        assertEquals("normal", pocketMine.generatorName)
        assertEquals("", pocketMine.generatorOptions)
        assertEquals("world", pocketMine.levelName)
    }
}
