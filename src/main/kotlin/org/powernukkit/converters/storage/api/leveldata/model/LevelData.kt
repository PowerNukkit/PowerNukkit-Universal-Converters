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

package org.powernukkit.converters.storage.api.leveldata.model

import br.com.gamemods.nbtmanipulator.NbtFile
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.math.EntityPos
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.storage.api.Dialect
import org.powernukkit.converters.storage.api.StorageEngine
import java.awt.image.BufferedImage
import java.nio.file.Path
import java.time.Instant

/**
 * @property dataFile The original NBT file structure where the data were extracted
 * @property storageEngine The detected storage engine used to extract the data
 * @property minecraftEdition The detected minecraft edition based on the folder contents
 * @property minecraftVersion The detected version of the minecraft edition
 * @property folder The root folder of the level
 *
 * @property spawn The server spawn location
 * @property randomSeed The random number used to generate the world
 * @property lastPlayed Last time the world was loaded
 * @property time The current in-game time age of the world, used to calculate Minecraft days
 * @property sizeOnDisk A cached value to show the disk usage
 *
 * @property snowCovered Generates a perpetual snow biome in alpha versions of Minecraft Java Edition
 *
 * @property mapFeatures Also known as MapFeatures from JE Beta 1.8 to 1.15,
 * defines if the map generator should place structures such as villages, strongholds, and mineshafts.
 *
 * @author joserobjr
 * @since 2020-10-19
 */
data class LevelData(
    // Internal data
    val dataFile: NbtFile? = null,
    val storageEngine: StorageEngine? = null,
    val dialect: Dialect? = null,
    val folder: Path? = null,

    // Since JE inf-dev
    val spawn: BlockPos? = null,
    val randomSeed: Long? = null, // Position changed in JE 1.16+
    val lastPlayed: Instant? = null,
    val time: Long?,
    val sizeOnDisk: Long? = null, // Removed in JE 1.16+

    // Since Alpha
    val snowCovered: Boolean? = null, // Removed in JE Beta 1.3

    // Since JE Beta 1.3
    //val version: Int? = null, Moved to the LevelVersionData
    val levelName: String? = null,

    // Since JE Beta 1.5
    val raining: Boolean? = null,
    val thundering: Boolean? = null,
    val rainTime: Int? = null,
    val thunderTime: Int? = null,

    // Since JE Beta 1.8
    val gameType: Int?,
    val mapFeatures: Boolean? = null,

    // Since JE 1.0
    val hardcore: Boolean? = null,

    // Since JE 1.1
    val generatorName: String? = null, //Removed in JE 1.16+

    // Since JE 1.2
    val generatorVersion: Int? = null, //Removed in JE 1.16+

    // Since JE 1.3
    val allowCommands: Boolean? = null,
    val initialized: Boolean? = null,

    // Since JE 1.4
    val dayTime: Long? = null,
    val gameRules: Map<String, String>? = null,
    val generatorOptions: String? = null, //Removed in JE 1.13+

    // Since JE 1.8
    val difficultyLocked: Boolean? = null,
    val difficulty: Int? = null,
    val clearWeatherTime: Int? = null,
    val borderSizeLerpTime: Long? = null,
    val borderCenter: EntityPos? = null,
    val borderDamageperBlock: Double? = null,
    val borderSafeZone: Double? = null,
    val borderSize: Double? = null,
    val borderSizeLerpTarget: Double? = null,
    val borderWarningBlocks: Double? = null,
    val borderWarningTime: Double? = null,

    // Since JE 1.9
    val endDimensionData: EndDimensionData? = null, // Path changed in JE 1.16
    val versionData: LevelVersionData? = null,

    // Since JE 1.13
    val enabledDataPacks: List<String>? = null,
    val disabledDataPacks: List<String>? = null,
    val customBosses: Map<NamespacedId, CustomBossData>? = null,

    // Since JE 1.14
    val scheduledEvents: List<*>? = null, // TODO Find details about this data
    val wanderingTraderSpawnChance: Int? = null,
    val wanderingTraderSpawnDelay: Int? = null,

    // Since JE 1.16
    val serverBrands: List<String>? = null,
    val wasModded: Boolean? = null,
    val bonusChest: Boolean? = null,
    val dimensionGeneratorSettings: Map<NamespacedId, DimensionGeneratorData>? = null,

    // Since JE 1.16.3
    val spawnAngle: Double? = null,

    // Windows 10 Edition
    val bonusChestSpawned: Boolean? = null,
    val centerMapsToOrigin: Boolean? = null,
    val confirmedPlatformLockedContent: Boolean? = null,
    val educationFeaturesEnabled: Boolean? = null,
    val forceGameType: Boolean? = null,
    val hasBeenLoadedInCreative: Boolean? = null,
    val hasLockedBehaviorPack: Boolean? = null,
    val hasLockedResourcePack: Boolean? = null,
    val immutableWorld: Boolean? = null,
    val isFromLockedTemplate: Boolean? = null,
    val isFromWorldTemplate: Boolean? = null,
    val isSingleUseWorld: Boolean? = null,
    val isWorldTemplateOptionLocked: Boolean? = null,
    val lanBroadcast: Boolean? = null,
    val lanBroadcastIntent: Boolean? = null,
    val multiplayerGame: Boolean? = null,
    val multiplayerGameIntent: Boolean? = null,
    val requiresCopiedPackRemovalCheck: Boolean? = null,
    val spawnMobs: Boolean? = null,
    val spawnV1Villagers: Boolean? = null,
    val startWithMap: Boolean? = null,
    val texturePacksRequired: Boolean? = null,
    val useMsaGamerTagsOnly: Boolean? = null,
    val eduOffer: Int? = null,
    val generator: Int? = null,
    val limitedWorldDepth: Int? = null,
    val limitedWorldWidth: Int? = null,
    val limitedWorldOrigin: BlockPos? = null,
    val netherScale: Int? = null,
    val platformBroadcastIntent: Int? = null,
    val serverChunkTickRange: Int? = null,
    val xBoxLiveBroadcastIntent: Int? = null,
    val currentTick: Long? = null,
    val worldStartCount: Long? = null,
    val lightningLevel: Float? = null,
    val rainLevel: Float? = null,
    val biomeOverride: String? = null,
    val flatWorldLayers: String? = null,
    val prid: String? = null,

    val icon: BufferedImage? = null
)
